package moiz.dev.mainapp.screens

import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.YourImageAnalyzer
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.ui.theme.newBlue
import moiz.dev.mainapp.viewModel.DetectionViewModel
import java.util.concurrent.Executor

@Composable
fun CameraPermissionAndPreview() {
    var hasPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        return
    } else {
        Text("Camera permission is required", modifier = Modifier, fontSize = 46.sp)
    }
}

@Composable
fun CameraPreview(dao: UserDao) {

    CameraPermissionAndPreview()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // 📷 Set up the analyzer
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 640)) // match model input
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(ctx),
                        YourImageAnalyzer(ctx , dao)
                    )
                }

            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis // 🔗 Connect analyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(ctx))

        previewView
    }, modifier = Modifier.fillMaxSize())
}






