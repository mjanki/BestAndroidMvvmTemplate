package org.umbrellahq.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import org.umbrellahq.database.model.TaskDatabaseEntity

@Dao
interface TaskDao {
    @Query("SELECT * from tasks")
    fun getAll(): Flowable<List<TaskDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskDatabaseEntity: TaskDatabaseEntity)

    @Query("DELETE from tasks")
    fun deleteAll()
}