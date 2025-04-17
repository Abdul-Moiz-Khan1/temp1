package moiz.dev.mainapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userData: User)

    @Delete
    suspend fun deleteUser(userData: User)

    @Query("SELECT * FROM USER")
     fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM USER WHERE licensePlate = :license")
    suspend fun checkEntry(license: String): User?

}