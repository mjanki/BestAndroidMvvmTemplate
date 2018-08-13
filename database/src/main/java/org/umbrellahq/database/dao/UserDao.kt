package org.umbrellahq.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.umbrellahq.database.entity.User

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE from users")
    fun deleteAll()
}