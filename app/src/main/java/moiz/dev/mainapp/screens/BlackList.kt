package moiz.dev.mainapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.utils.Routes

@Composable
fun BlackList(navController: NavController, dao: UserDao) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var numberPlate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    Image(
        painter = painterResource(id = R.drawable.back),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .padding(start = 24.dp).clickable { navController.popBackStack() }
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Black List User", fontSize = 24.sp)
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(value = numberPlate, onValueChange = { numberPlate = it }, label = {
            Text(
                text = "Enter Number Plate to Black List"
            )
        })
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            label = { Text(text = "Enter Reason for Black List") })
        Spacer(modifier = Modifier.size(4.dp))
        Button(onClick = {
            scope.launch {
                dao.blackListUser(numberPlate , reason)
                Toast.makeText(context, "User Blacklisted", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Confirm")
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "See all BlackListed", color = Color.Blue, modifier = Modifier.clickable {
            navController.navigate(Routes.SeeAllBlackListedUsers)
        })
    }


}