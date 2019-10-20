package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.ErrorNetworkDatabaseDao
import org.umbrellahq.database.model.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.mappers.ErrorNetworkRepoDatabaseMapper
import org.umbrellahq.repository.model.ErrorNetworkRepoEntity
import org.umbrellahq.util.extensions.execute

open class Repository(ctx: Context) {
    protected var appDatabase: AppDatabase = AppDatabase(ctx)

    // DAOs
    protected var disposables = CompositeDisposable()
    private var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao = appDatabase.errorNetworkDao()

    // Observables
    private var allErrorsNetwork: Flowable<List<ErrorNetworkDatabaseEntity>>

    // Mappers
    private var errorNetworkRepoDatabaseMapper = ErrorNetworkRepoDatabaseMapper()

    init {
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

    fun deleteErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity): Completable =
            errorNetworkDatabaseDao.delete(
                    errorNetworkRepoDatabaseMapper.downstream(
                            errorNetworkRepoEntity
                    )
            )

    protected fun insertErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.insert(
                errorNetworkRepoDatabaseMapper.downstream(errorNetworkRepoEntity)
        ).execute()
    }

    fun clearDisposables() {
        disposables.clear()
    }
}