package org.umbrellahq.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.umbrellahq.database.entity.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * from users")
    fun getAll(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskEntity: TaskEntity)

    @Query("DELETE from users")
    fun deleteAll()
}