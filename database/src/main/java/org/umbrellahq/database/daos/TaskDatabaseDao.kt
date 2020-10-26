package org.umbrellahq.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.umbrellahq.database.models.TaskDatabaseEntity

@Dao
interface TaskDatabaseDao {
    @Query("SELECT * from tasks")
    fun getAll(): Flow<List<TaskDatabaseEntity>>

    @Query("SELECT * from tasks where uuid = :uuid LIMIT 1")
    suspend fun getByUUID(uuid: String): TaskDatabaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskDatabaseEntity: TaskDatabaseEntity)

    @Query("DELETE from tasks")
    suspend fun deleteAll()
}