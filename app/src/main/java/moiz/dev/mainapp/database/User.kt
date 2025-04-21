package moiz.dev.mainapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USER")
data class User(
    @PrimaryKey(autoGenerate = true)  val id:Int = 0,
    val name:String,
    val cnic:String,
    val licensePlate:String,
    val purpose:String,
    val contact:String,
    val date:String,
    val time:String,
    val list:String,
    val reasonForBlackList : String? = null
)
