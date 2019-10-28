package org.umbrellahq.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import org.umbrellahq.database.models.TaskDatabaseEntity

@Dao
interface TaskDatabaseDao {
    @Query("SELECT * from tasks")
    fun getAll(): Flowable<List<TaskDatabaseEntity>>

    @Query("SELECT * from tasks where uuid = :uuid")
    fun getWithUUID(uuid: String): Flowable<List<TaskDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskDatabaseEntity: TaskDatabaseEntity): Completable

    @Query("DELETE from tasks")
    fun deleteAll(): Completable
}