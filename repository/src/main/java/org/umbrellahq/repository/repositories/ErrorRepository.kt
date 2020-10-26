package org.umbrellahq.repository.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.umbrellahq.database.daos.ErrorNetworkDatabaseDao
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.mappers.ErrorNetworkRepoDatabaseMapper
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity

open class ErrorRepository(ctx: Context? = null): Repository(ctx) {
    // DAOs
    private lateinit var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao

    // Observables
    private lateinit var allErrorsNetwork: Flow<List<ErrorNetworkDatabaseEntity>>

    // Mappers
    private var errorNetworkRepoDatabaseMapper = ErrorNetworkRepoDatabaseMapper()

    override fun init() {
        super.init()

        init(testErrorNetworkDatabaseDao = null)
    }

    fun init(testErrorNetworkDatabaseDao: ErrorNetworkDatabaseDao? = null) {
        errorNetworkDatabaseDao = testErrorNetworkDatabaseDao ?: appDatabase.errorNetworkDao()
        allErrorsNetwork = errorNetworkDatabaseDao.getAll()
    }

    fun getErrorsNetwork(): Flow<List<ErrorNetworkRepoEntity>> = allErrorsNetwork.map {
        it.map { error -> errorNetworkRepoDatabaseMapper.upstream(error) }
    }

    suspend fun deleteErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.delete(
                errorNetworkRepoDatabaseMapper.downstream(
                        errorNetworkRepoEntity
                )
        )
    }

    suspend fun insertErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.insert(
                errorNetworkRepoDatabaseMapper.downstream(errorNetworkRepoEntity)
        )
    }
}