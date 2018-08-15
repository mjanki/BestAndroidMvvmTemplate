package org.umbrellahq.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.umbrellahq.database.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getAll(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity)

    @Query("DELETE from users")
    fun deleteAll()
}