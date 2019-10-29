package org.umbrellahq.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.umbrellahq.database.models.TaskDatabaseEntity

@Dao
interface TaskDatabaseDao {
    @Query("SELECT * from tasks")
    fun getAll(): Flowable<List<TaskDatabaseEntity>>

    @Query("SELECT * from tasks where uuid = :uuid")
    fun getByUUID(uuid: String): Flowable<List<TaskDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskDatabaseEntity: TaskDatabaseEntity): Single<Long>

    @Query("DELETE from tasks")
    fun deleteAll(): Completable
}