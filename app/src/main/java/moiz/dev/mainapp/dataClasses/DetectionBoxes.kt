package moiz.dev.mainapp.dataClasses

data class DetectionBox(

    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val label: String,
    val confidence: Float
)