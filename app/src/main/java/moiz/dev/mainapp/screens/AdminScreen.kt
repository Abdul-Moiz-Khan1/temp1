package moiz.dev.mainapp.screens

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.database.UserDatabase
import moiz.dev.mainapp.ui.theme.newBlue
import moiz.dev.mainapp.utils.Routes
import java.io.File
import java.io.FileWriter
import java.io.IOException

@Composable
fun AdminScreen(navController: NavController, dao: UserDao) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminItemCard(
                title1 = "Add New",
                icon1 = R.drawable.add_new,
                title2 = "See All Entries",
                icon2 = R.drawable.see_all,
                navController = navController,
                dao = dao
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AdminItemCard(
                title1 = "Download Report",
                icon1 = R.drawable.download,
                title2 = "BlackList Person",
                icon2 = R.drawable.black_list,
                navController = navController,
                dao = dao
            )
        }
    }
}

@Composable
fun AdminItemCard(
    title1: String,
    icon1: Int,
    title2: String,
    icon2: Int,
    navController: NavController,
    dao: UserDao
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val list by dao.getAll().collectAsState(initial = emptyList())

    Column {
        // First Card (Top)
        OutlinedCard(
            onClick = {
                when (title1) {
                    "Add New" -> navController.navigate(Routes.AddNewEntry)
                    "Download Report" -> {
                        Toast.makeText(context, "Download in Progress", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            saveCSVToDownloads(context, ConvertToCSV(list))
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = icon1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .fillMaxSize()
            )
            Text(
                text = title1,
                color = newBlue,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Second Card (Bottom)
        OutlinedCard(
            onClick = {
                when (title2) {
                    "See All Entries" -> navController.navigate(Routes.SeeAll)
                    "BlackList Person" -> navController.navigate(Routes.BlackList)
                }
            },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = icon2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .fillMaxSize()
            )
            Text(
                text = title2,
                color = newBlue,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}




fun ConvertToCSV(list: List<User>): String {
    val headers = "Name,CNIC,License Plate,Purpose,Contact,Date,Time,List"
    val csvData = list.joinToString("\n") {
        "${it.name},${it.cnic},${it.licensePlate},${it.purpose},${it.contact},${it.date},${it.time},${it.list}"
    }
    return "$headers\n$csvData"
}

fun saveCSVToDownloads(context: Context, csv: String, fileName: String = "user_entries.csv") {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "text/csv")
    }

    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        resolver.openOutputStream(it)?.use { stream ->
            stream.write(csv.toByteArray())
        }
        Toast.makeText(context, "CSV saved to Downloads!", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
    }
}
