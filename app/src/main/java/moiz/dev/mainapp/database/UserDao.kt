package moiz.dev.mainapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM USER WHERE name LIKE '%' || :name || '%' OR licensePlate LIKE '%' || :name || '%'")
    suspend fun getByName(name: String): List<User>

    @Query("UPDATE USER SET list = 'black' ,reasonForBlackList = :reason WHERE licensePlate = :license ")
    suspend fun blackListUser(license: String , reason: String)

    @Query("SELECT * FROM USER Where list = 'black'")
    fun getAllBlackListed(): Flow<List<User>>



}