package moiz.dev.mainapp.screens

import android.widget.Toast
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.ui.theme.newBlue

@Composable
fun HomeScreen(navController: NavController,dao:UserDao) {
    var numberPlate by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var status:User? = null
    var userFound by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painterResource(id = R.drawable.finlogo), null, modifier = Modifier.size(200.dp))
        Text(
            text = "Enter Your License Plate",
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = newBlue 
        )
        OutlinedTextField(
            value = numberPlate, 
            onValueChange = { numberPlate = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { (Text(text = "Enter your Number plate")) }
        )
        Spacer(modifier = Modifier.size(4.dp))
        Button(onClick = {
                scope.launch {
                    status = dao.checkEntry(numberPlate)
                    if(status==null){
                        Toast.makeText(context , "No User Found" , Toast.LENGTH_SHORT).show()
                    }else{
                        userFound = true
                        Toast.makeText(context , status!!.name , Toast.LENGTH_SHORT).show()
                    }
                }
        }) {
            Text(text = "Check")
        }
//        if(userFound){
//            Text(text = "Name:${status!!.name}")
//            Text(text = "License Plate:${status!!.licensePlate}")
//            Text(text = "Purpose:${status?.purpose}")
//            Text(text = "Contact Number:${status?.contact}")
//        }
    }
}


