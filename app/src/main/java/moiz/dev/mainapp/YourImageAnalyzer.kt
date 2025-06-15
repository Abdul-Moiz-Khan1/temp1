package moiz.dev.mainapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moiz.dev.mainapp.dataClasses.DetectionBox
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.utils.Lists
import moiz.dev.mainapp.utils.YuvToRgbConverter
import moiz.dev.mainapp.viewModel.DetectionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Volatile
private var isInCooldown = false

class YourImageAnalyzer(
    private val context: Context,
    private val dao: UserDao
) : ImageAnalysis.Analyzer {

    private val tfliteModel: Interpreter by lazy {
        Interpreter(FileUtil.loadMappedFile(context, "tested_vehicle_detection_model.tflite"))
    }

    private val licensePlateModel: Interpreter by lazy {
        Interpreter(FileUtil.loadMappedFile(context, "tested_icense_plate_detection_int8.tflite"))
    }

    private var frameCounter = 0

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        frameCounter++
        if (frameCounter % 40 != 0) {
            imageProxy.close()
            return
        }

        val image = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val bitmap = image.toBitmap(context, imageProxy.imageInfo.rotationDegrees)
        val resizedBitmap = bitmap.scale(640, 640)
        val tensorImage = TensorImage(DataType.FLOAT32).apply { load(resizedBitmap) }

        val processedImage = ImageProcessor.Builder()
            .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
            .process(tensorImage)

        val byteBuffer = processedImage.buffer
        val vehicleDetections = Array(1) { Array(300) { FloatArray(6) } }
        val plateDetections = Array(1) { Array(300) { FloatArray(6) } }

        tfliteModel.run(byteBuffer, vehicleDetections)

        vehicleDetections[0].forEach { detection ->
            val score = detection[4]
            val classId = detection[5].toInt()

            if (score > 0.5 && classId == 2) { // Class 2 = car
                licensePlateModel.run(byteBuffer, plateDetections)

                plateDetections[0].forEach { plate ->
                    val plateConfidence = plate[4]
                    if (plateConfidence > 0.5) {
                        handlePlateDetection(plate, resizedBitmap)
                    }
                }

                Log.d("TFliteDetection", "Car detected with confidence: $score")
            }
        }

        imageProxy.close()
    }

    private fun handlePlateDetection(
        detection: FloatArray,
        bitmap: Bitmap
    ) {
        if (isInCooldown) return
        val crop = calculateCropBounds(detection, bitmap.width, bitmap.height) ?: return

        val croppedBitmap = Bitmap.createBitmap(
            bitmap, crop.left, crop.top, crop.width(), crop.height()
        )
        saveBitmapToGallery(context, croppedBitmap)

        val inputImage = InputImage.fromBitmap(croppedBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val input = visionText.text
                    .replace("(?i)\\b(punjab|sindh|islamabad|ict(?:-islamabad)?)\\b".toRegex(), "")
                    .replace("-(0[1-9]|1[0-9]|2[0-5])\\b".toRegex(), "")
                    .replace("[\\s-]+".toRegex(), "")
                    .trim()

                val stopIndex = input.indices.firstOrNull { i ->
                    i > 0 && input[i - 1].isDigit() && input[i].isLetter()
                }

                val cleanedText = if (stopIndex != null) input.substring(0, stopIndex) else input
                CoroutineScope(Dispatchers.IO).launch {
                    val listType = dao.getUserListStatus(cleanedText)
                    Log.d("OCR", "Detected Text: $cleanedText, ListType: $listType")

                    withContext(Dispatchers.Main) {
                        if (listType.equals(Lists.WHITE) || listType.equals(Lists.BLACK)) {
                            isInCooldown = true
                            val dialog = AlertDialog.Builder(context)
                                .setTitle(listType?.uppercase())
                                .setMessage("This vehicle is on the ${listType?.uppercase()}.")
                                .setCancelable(false) // Prevent dismiss by touch outside
                                .create()

                            dialog.show()

                            // Dismiss after 3 seconds
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialog.dismiss()
                                isInCooldown = false
                            }, 6000)
                        } else if (!cleanedText.isNullOrEmpty()) {
                            withContext(Dispatchers.IO) {
                                val currentDate = LocalDateTime.now()
                                dao.insertUser(
                                    User(
                                        id = 0,
                                        name = "greyList Person",
                                        cnic = "Grey List",
                                        licensePlate = cleanedText,
                                        purpose = "Visit",
                                        contact = "XXX",
                                        date = currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                                        time = currentDate.format(DateTimeFormatter.ofPattern("hh:mm:ss a")),
                                        list = Lists.GREY
                                    )
                                )

                            }
                            isInCooldown = true
                            val dialog = AlertDialog.Builder(context)
                                .setTitle(listType?.uppercase())
                                .setMessage("This vehicle is on the ${listType?.uppercase()}.")
                                .setCancelable(false) // Prevent dismiss by touch outside
                                .create()

                            dialog.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialog.dismiss()
                                isInCooldown = false
                            }, 5000)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Error: ${e.message}")
                Toast.makeText(context, "OCR failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private data class CropBounds(val left: Int, val top: Int, val right: Int, val bottom: Int) {
        fun width() = right - left
        fun height() = bottom - top
    }

    private fun calculateCropBounds(
        detection: FloatArray,
        imgWidth: Int,
        imgHeight: Int
    ): CropBounds? {
        val (xCenter, yCenter, width, height) = detection.take(4)

        val x = xCenter * imgWidth
        val y = yCenter * imgHeight
        val w = width * imgWidth
        val h = height * imgHeight

        val left = (x - w / 2).toInt().coerceIn(0, imgWidth)
        val top = (y - h / 2).toInt().coerceIn(0, imgHeight)
        val right = (x + w / 2).toInt().coerceIn(0, imgWidth)
        val bottom = (y + h / 2).toInt().coerceIn(0, imgHeight)

        return if (right > left && bottom > top) {
            CropBounds(left, top, right, bottom)
        } else {
            Log.e("CropError", "Invalid crop size: width=${right - left}, height=${bottom - top}")
            null
        }
    }
}

fun Image.toBitmap(context: Context, rotationDegrees: Int): Bitmap {
    val converter = YuvToRgbConverter(context)
    val bitmap = createBitmap(width, height)
    converter.yuvToRgb(this, bitmap)

    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    filename: String = "cropped_image_${System.currentTimeMillis()}.jpg"
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/VehicleDetection"
        )
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val contentResolver = context.contentResolver
    val uri: Uri? =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(uri, contentValues, null, null)
        Toast.makeText(context, "Image saved: $filename", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}


