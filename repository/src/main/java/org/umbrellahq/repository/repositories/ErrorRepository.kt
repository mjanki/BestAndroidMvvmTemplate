package org.umbrellahq.repository.repositories

import android.content.Context
import io.reactivex.Flowable
import org.umbrellahq.database.daos.ErrorNetworkDatabaseDao
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.mappers.ErrorNetworkRepoDatabaseMapper
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.extensions.execute

open class ErrorRepository(ctx: Context? = null): Repository(ctx) {
    // DAOs
    private lateinit var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao

    // Observables
    private lateinit var allErrorsNetwork: Flowable<List<ErrorNetworkDatabaseEntity>>

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

    fun getErrorsNetwork(): Flowable<List<ErrorNetworkRepoEntity>> {
        return allErrorsNetwork.flatMap { errorNetworkDatabaseEntityList ->
            Flowable.fromArray(
                    errorNetworkDatabaseEntityList.map { errorNetworkDatabaseEntity ->
                        errorNetworkRepoDatabaseMapper.upstream(errorNetworkDatabaseEntity)
                    }
            )
        }
    }

    fun deleteErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.delete(
                errorNetworkRepoDatabaseMapper.downstream(
                        errorNetworkRepoEntity
                )
        ).execute()
    }

    fun insertErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.insert(
                errorNetworkRepoDatabaseMapper.downstream(errorNetworkRepoEntity)
        ).execute()
    }
}