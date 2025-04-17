package moiz.dev.mainapp.dataClasses

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItems(
    val title: String,
    val iconSeleced:ImageVector,
    val notSeleced:ImageVector,
    val route:String
    )
