package org.umbrellahq.database.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.umbrellahq.database.model.ErrorNetworkDatabaseEntity

@Dao
interface ErrorNetworkDatabaseDao {
    @Query("SELECT * from network_errors")
    fun getAll(): Flowable<List<ErrorNetworkDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity): Completable

    @Query("DELETE from network_errors")
    fun deleteAll(): Completable

    @Delete
    fun delete(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity): Completable
}