package org.umbrellahq.database.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.umbrellahq.model.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * from tasks")
    fun getAll(): LiveData<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskEntity: TaskEntity)

    @Query("DELETE from tasks")
    fun deleteAll()
}