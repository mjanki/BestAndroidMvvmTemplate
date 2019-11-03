package org.umbrellahq.util.extensions

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response

object RxKotlinExtensions {
    var isTesting = false
    fun getScheduler(): Scheduler = if (isTesting) Schedulers.trampoline() else Schedulers.io()
}

// Use to invoke Completable
fun Completable.execute(
        onSuccess: (() -> Unit)? = null,
        onFailure: ((throwable: Throwable) -> Unit)? = null
): Disposable {

    return subscribeOn(
            RxKotlinExtensions.getScheduler()
    ).subscribe (
            { onSuccess?.let { onSuccess -> onSuccess() } },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}

// Use to invoke Single
fun <T> Single<T>.execute(
        onSuccess: ((value: T) -> Unit)? = null,
        onFailure: ((throwable: Throwable) -> Unit)? = null
) {

    subscribeOn(
            RxKotlinExtensions.getScheduler()
    ).subscribe(object : DisposableSingleObserver<T>() {
        override fun onSuccess(t: T) {
            onSuccess?.let { onSuccess -> onSuccess(t) }
        }

        override fun onError(e: Throwable) {
            onFailure?.let { onFailure -> onFailure(e) }
        }
    })
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
            RxKotlinExtensions.getScheduler()
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
                onComplete?.let { onComplete -> onComplete() }
            }
    )
}