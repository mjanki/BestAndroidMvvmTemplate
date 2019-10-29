package org.umbrellahq.database.daos

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity

@Dao
interface ErrorNetworkDatabaseDao {
    @Query("SELECT * from network_errors")
    fun getAll(): Flowable<List<ErrorNetworkDatabaseEntity>>

    @Query("SELECT * from network_errors where id = :id")
    fun getById(id: Long): Flowable<List<ErrorNetworkDatabaseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity): Single<Long>

    @Query("DELETE from network_errors where id = :id")
    fun delete(id: Long): Completable

    @Query("DELETE from network_errors")
    fun deleteAll(): Completable

    @Delete
    fun delete(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity): Completable
}