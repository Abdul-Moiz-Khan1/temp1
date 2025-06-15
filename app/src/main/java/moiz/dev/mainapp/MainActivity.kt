package moiz.dev.mainapp

import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import moiz.dev.mainapp.dataClasses.BottomNavItems
import moiz.dev.mainapp.database.UserDatabase
import moiz.dev.mainapp.screens.AddNew
import moiz.dev.mainapp.screens.AdminScreen
import moiz.dev.mainapp.screens.BlackList
import moiz.dev.mainapp.screens.BlackListedUsersList
import moiz.dev.mainapp.screens.CameraPreview
import moiz.dev.mainapp.screens.DownloadReport
import moiz.dev.mainapp.screens.PinScreen
import moiz.dev.mainapp.screens.SeeAll
import moiz.dev.mainapp.ui.theme.MainAppTheme
import moiz.dev.mainapp.utils.Routes
import moiz.dev.mainapp.viewModel.DetectionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val db = Room.databaseBuilder(context, UserDatabase::class.java, "USER").build()
            val dao = db.userDao()
            var selectedItem by rememberSaveable {
                mutableIntStateOf(0)
            }
            val navController = rememberNavController()
            val items = listOf(
                BottomNavItems("Home", Icons.Filled.Home, Icons.Outlined.Home, Routes.HomeScreen),
                BottomNavItems(
                    "Admin",
                    Icons.Filled.Person,
                    Icons.Outlined.Person,
                    Routes.PinScreen
                ),
            )

            MainAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar() {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItem == index,
                                    onClick = {
                                        selectedItem = index
                                        navController.navigate(item.route)
                                    },
                                    label = {
                                        Text(text = item.title)
                                    },
                                    icon = {
                                        Icon(
                                            if (selectedItem == index) {
                                                item.iconSeleced
                                            } else {
                                                item.notSeleced
                                            }, contentDescription = item.title
                                        )

                                    }

                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HomeScreen,
                        modifier = Modifier.padding(innerPadding),
                        builder = {
                            composable(Routes.HomeScreen) {
                                CameraPreview(dao)
                            }
                            composable(Routes.PinScreen) {
                                PinScreen(navController)
                            }
                            composable(Routes.AdminScreen) {
                                AdminScreen(navController, dao)
                            }
                            composable(Routes.AddNewEntry) {
                                AddNew(navController = navController, dao = dao)
                            }
                            composable(Routes.Download) {
                                DownloadReport(navController = navController, dao = dao)
                            }
                            composable(Routes.SeeAll) {
                                SeeAll(navController = navController, dao = dao)
                            }
                            composable(Routes.BlackList) {
                                BlackList(navController = navController, dao = dao)
                            }
                            composable(Routes.SeeAllBlackListedUsers) {
                                BlackListedUsersList(navController = navController, dao = dao)
                            }
                        })
                }
            }
        }
    }
}
