package moiz.dev.mainapp.screens

import android.icu.util.LocaleData
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.ui.theme.newBlue
import moiz.dev.mainapp.utils.Lists
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.coroutineContext


@Composable
fun AddNew(navController: NavController, dao: UserDao) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var cnic by remember { mutableStateOf("") }
    var numberPlate by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Entry",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = newBlue
        )
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") })
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = cnic,
            onValueChange = { cnic = it },
            label = { Text(text = "Cnic") })
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = numberPlate,
            onValueChange = { numberPlate = it },
            label = { Text(text = "Licence Plate") })
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text(text = "Contact Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = purpose,
            onValueChange = { purpose = it },
            label = { Text(text = "Purpose") })
        Spacer(modifier = Modifier.size(4.dp))
        Button(onClick = {
            val currentDate = LocalDateTime.now()
            scope.launch {
                dao.insertUser(
                    User(
                        name = name,
                        cnic = cnic,
                        licensePlate = numberPlate,
                        purpose = purpose,
                        contact = contact,
                        date = currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                        time = currentDate.format(DateTimeFormatter.ofPattern("hh:mm:ss a")),
                        list = Lists.WHITE
                    )
                )
                Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }) {
            Text(text = "Confirm")

        }


    }
}