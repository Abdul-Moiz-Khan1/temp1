package moiz.dev.mainapp.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.launch
import moiz.dev.mainapp.R
import moiz.dev.mainapp.database.User
import moiz.dev.mainapp.database.UserDao
import moiz.dev.mainapp.ui.theme.newBlue


@Composable
fun SeeAll(navController: NavController, dao: UserDao) {
    var filteredList by remember { mutableStateOf(emptyList<User>()) }
    val allUsers by dao.getAll().collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            filteredList = dao.getByName(searchText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "back",
                modifier = Modifier.align(Alignment.CenterVertically)
                    .size(35.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search by name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            val displayList = if (searchText.isEmpty()) allUsers else filteredList
            items(displayList) { item ->
                ColumnItem(item = item, dao = dao)
                HorizontalDivider(color = newBlue)
            }
        }
    }
}

@Composable
fun ColumnItem(item: User, dao: UserDao) {
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(text = item.name, fontSize = 18.sp)
            Text(text = item.licensePlate, fontSize = 14.sp)
            Text(text = item.time, fontSize = 12.sp)
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = item.date, fontSize = 18.sp, modifier = Modifier.align(Alignment.End))
            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.End)
                    .size(32.dp)
                    .padding(top = 2.dp)
                    .clickable {
                        scope.launch {
                            dao.deleteUser(item)
                        }
                    }
            )
        }
    }
}