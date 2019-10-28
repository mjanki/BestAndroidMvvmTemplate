package org.umbrellahq.network.daos

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.util.extensions.execute
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseNetworkDao {

    val errorNetwork = PublishSubject.create<ErrorNetworkEntity>()
    protected fun <T> executeNetworkCall(
            observable: Observable<Response<T>>,
            shouldPersist: Boolean = false,
            action: String = "",
            onSuccess: ((value: Response<T>) -> Unit),
            onFailure: ((throwable: Throwable) -> Unit)? = null,
            onComplete: (() -> Unit)? = null): Disposable {

        return observable.execute(
                onSuccess = { response ->
                    onSuccess(response)
                },
                onFailure = { throwable ->
                    val errorNetworkEntity = ErrorNetworkEntity()

                    throwable.message?.let {
                        errorNetworkEntity.message = it
                    }

                    when (throwable) {
                        is SocketTimeoutException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.TIMEOUT
                        }

                        is IOException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.IO
                        }

                        is HttpException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.HTTP
                            errorNetworkEntity.code = throwable.code()
                        }

                        else -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.OTHER
                        }
                    }

                    errorNetworkEntity.shouldPersist = shouldPersist
                    errorNetworkEntity.action = action

                    errorNetwork.onNext(errorNetworkEntity)

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