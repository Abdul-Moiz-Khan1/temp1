package moiz.dev.mainapp.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import moiz.dev.mainapp.R
import moiz.dev.mainapp.utils.Routes
import moiz.dev.mainapp.utils.getSavedPin
import moiz.dev.mainapp.utils.savePin

@Composable
fun PinScreen(navController: NavController) {
    var pin by remember {
        mutableStateOf("")
    }
    var showPinScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(id = R.drawable.lock), null, modifier = Modifier.size(100.dp))
        Text(text = "Enter Pin", fontSize = 20.sp)
        OutlinedTextField(
            value = pin,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            onValueChange = {  if (it.length <= 4) pin = it},
            label = { Text(text = "Enter Pin") },
            maxLines = 1,
            singleLine = true,

            )
        Button(onClick = {
            if (pin == getSavedPin(context)) {
                Toast.makeText(context, "Correct Pin", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.AdminScreen)
            } else if (pin.isEmpty()) {
                Toast.makeText(context, "Please Enter Pin", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Incorrect Pin", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Submit")
        }
        Text(
            text = "Change Pin?",
            color = Color.Blue,
            modifier = Modifier
                .padding(top = 10.dp)
                .clickable {
                    showPinScreen = true
                })
        if (showPinScreen) {
            AlertDialog(
                onDismissRequest = { showPinScreen = false },
                title = { Text(text = "Change Pin") },
                text = {
                    ChangePin(
                        onPinChanged = {
                            showPinScreen = false
                            Toast.makeText(context, "Pin changed successfully", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onCancel = {
                            showPinScreen = false
                        }
                    )
                },
                confirmButton = {

                })

        }
    }
}

@Composable
fun ChangePin(
    onPinChanged: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = oldPin,
            onValueChange = { if (it.length <= 4) oldPin = it },
            label = { Text("Enter Old Pin") },
            maxLines = 1,
            singleLine = true,
        )
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = newPin,
            onValueChange = { if (it.length <= 4) newPin = it },
            label = { Text("Enter New Pin") },
            maxLines = 1,
            singleLine = true,
        )
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = confirmPin,
            onValueChange = {  if (it.length <= 4) confirmPin = it },
            label = { Text("Confirm New Pin") },
            maxLines = 1,
            singleLine = true,
        )
        Spacer(modifier = Modifier.size(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                val savedPin = getSavedPin(context)
                when {
                    oldPin != savedPin ->
                        Toast.makeText(context, "Old Pin is incorrect", Toast.LENGTH_SHORT).show()

                    newPin != confirmPin ->
                        Toast.makeText(context, "New pins do not match", Toast.LENGTH_SHORT).show()

                    else -> {
                        savePin(context, newPin)
                        onPinChanged()
                    }
                }
            }) {
                Text("Save")
            }
        }
    }
}

