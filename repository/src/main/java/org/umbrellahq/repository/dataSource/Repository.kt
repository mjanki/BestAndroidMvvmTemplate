package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.ErrorNetworkDatabaseDao
import org.umbrellahq.database.model.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.mappers.ErrorNetworkDatabaseRepoMapper
import org.umbrellahq.repository.model.ErrorNetworkRepoEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.util.extensions.execute
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class Repository(ctx: Context) {
    protected var appDatabase: AppDatabase = AppDatabase(ctx)

    // DAOs
    private var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao = appDatabase.errorNetworkDao()

    // Observables
    private var allErrorsNetwork: Flowable<List<ErrorNetworkDatabaseEntity>>

    // Mappers
    private var errorNetworkRepoDatabaseRepoMapper = ErrorNetworkDatabaseRepoMapper()

    init {
        allErrorsNetwork = errorNetworkDatabaseDao.getAll()
    }

    fun getErrorsNetwork(): Flowable<List<ErrorNetworkRepoEntity>> {
        return allErrorsNetwork.flatMap { errorNetworkDatabaseEntityList ->
            Flowable.fromArray(
                    errorNetworkDatabaseEntityList.map { errorNetworkDatabaseEntity ->
                        errorNetworkRepoDatabaseRepoMapper.upstream(errorNetworkDatabaseEntity)
                    }
            )
        }
    }

    fun deleteErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity): Completable =
            errorNetworkDatabaseDao.delete(
                    errorNetworkRepoDatabaseRepoMapper.downstream(
                            errorNetworkRepoEntity
                    )
            )

    private fun insertErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
        errorNetworkDatabaseDao.insert(
                errorNetworkRepoDatabaseRepoMapper.downstream(errorNetworkRepoEntity)
        ).execute()
    }

    protected fun <T> executeNetworkCall(
            observable: Observable<Response<T>>,
            shouldPersist: Boolean = false,
            action: String = "",
            onSuccess: ((value: Response<T>) -> Unit),
            onFailure: ((throwable: Throwable) -> Unit)? = null,
            onComplete: (() -> Unit)? = null) {

        observable.execute(
                onSuccess = { response ->
                    onSuccess(response)
                },
                onFailure = { throwable ->
                    val errorNetworkRepoEntity = ErrorNetworkRepoEntity(null)

                    throwable.message?.let {
                        errorNetworkRepoEntity.message = it
                    }

                    when (throwable) {
                        is SocketTimeoutException -> {
                            errorNetworkRepoEntity.type = ErrorNetworkTypes.TIMEOUT
                        }

                        is IOException -> {
                            errorNetworkRepoEntity.type = ErrorNetworkTypes.IO
                        }

                        is HttpException -> {
                            errorNetworkRepoEntity.type = ErrorNetworkTypes.HTTP
                            errorNetworkRepoEntity.code = throwable.code()
                        }

                        else -> {
                            errorNetworkRepoEntity.type = ErrorNetworkTypes.OTHER
                        }
                    }

                    errorNetworkRepoEntity.shouldPersist = shouldPersist
                    errorNetworkRepoEntity.action = action

                    insertErrorNetwork(errorNetworkRepoEntity)

                    onFailure?.let { onFailure ->
                        onFailure(throwable)
                    }
                },
                onComplete = {
                    onComplete?.let { onComplete ->
                        onComplete()
                    }
                }
        )
    }
}