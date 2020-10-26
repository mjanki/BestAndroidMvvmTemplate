package org.umbrellahq.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity

@Dao
interface ErrorNetworkDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity)

    @Query("SELECT * from network_errors")
    fun getAll(): Flow<List<ErrorNetworkDatabaseEntity>>

    @Query("SELECT * from network_errors where id = :id LIMIT 1")
    fun getById(id: Long): Flow<ErrorNetworkDatabaseEntity>

    @Query("DELETE from network_errors where id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE from network_errors")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(errorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity)
}