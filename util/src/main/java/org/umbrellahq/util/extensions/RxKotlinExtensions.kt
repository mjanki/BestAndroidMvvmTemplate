package org.umbrellahq.util.extensions

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response

// Use to invoke Completable
fun Completable.execute(
        onSuccess: (() -> Unit)? = null,
        onFailure: ((throwable: Throwable) -> Unit)? = null
): Disposable {

    return subscribeOn(
            Schedulers.io()
    ).subscribe (
            { onSuccess?.let { onSuccess -> onSuccess() } },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}

// Use to get single value of Flowable
fun <T> Flowable<T>.getValue(
        onSuccess: ((value: T) -> Unit),
        onFailure: ((throwable: Throwable) -> Unit)? = null
): Disposable {
    return Completable.fromAction {
        onSuccess(blockingFirst())
    }.execute(onFailure = onFailure)
}

// Use to invoke Observable
fun <T> Observable<Response<T>>.execute(
        onSuccess: ((value: Response<T>) -> Unit),
        onFailure: ((throwable: Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
): Disposable {

    return subscribeOn(
            Schedulers.io()
    ).doOnComplete {
        onComplete?.let { onComplete -> onComplete() }
    }.subscribe (
            { response ->
                if (response.isSuccessful) {
                    onSuccess(response)
                } else {
                    onFailure?.let { onFailure ->
                        onFailure(HttpException(response))
                    }
                }
            },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}