package moiz.dev.mainapp.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao

@Composable
fun Details(navController: NavController, dao: UserDao, userId: Int) {
    Image(
        painter = painterResource(id = R.drawable.back),
        contentDescription = "back",
        modifier = Modifier
            .size(50.dp)
            .padding(start = 24.dp)
            .clickable {
                navController.popBackStack()
            }
    )
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        withContext(Dispatchers.IO) {
            user = dao.getUserById(userId)
        }
    }

    user?.let {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize() , verticalArrangement = Arrangement.Center , horizontalAlignment =  Alignment.CenterHorizontally) {
            Text("Name: ${it.name}", fontSize = 20.sp)
            Text("License Plate: ${it.licensePlate}", fontSize = 18.sp)
            Text("Time: ${it.time}", fontSize = 16.sp)
            Text("Date: ${it.date}", fontSize = 16.sp)
            Text("Cnic: ${it.cnic}", fontSize = 16.sp)

            if (!it.reasonForBlackList.isNullOrEmpty()) {
                Text("Blacklisted Reason: ${it.reasonForBlackList}", fontSize = 16.sp)
            }
        }
    } ?: run {
        Text("Loading...", modifier = Modifier.padding(16.dp))
    }
}

