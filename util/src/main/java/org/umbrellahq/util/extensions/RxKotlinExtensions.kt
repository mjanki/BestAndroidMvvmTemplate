package org.umbrellahq.util.extensions

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// Use to invoke Completable
fun Completable.execute(
        onSuccess: (() -> Unit)? = null,
        onFailure: ((throwable: Throwable) -> Unit)? = null
) : Disposable {

    return subscribeOn(
            Schedulers.io()
    ).subscribe (
            { onSuccess?.let { onSuccess() } },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}

// Use to get single value of Flowable
fun <T> Flowable<T>.getValue(
        onSuccess: ((value: T) -> Unit),
        onFailure: ((throwable: Throwable) -> Unit)? = null
) {
    Completable.fromAction {
        onSuccess(blockingFirst())
    }.execute(onFailure = onFailure)
}

// Use to invoke Observable
fun <T> Observable<T>.execute(
        onSuccess: ((value: T) -> Unit),
        onFailure: ((throwable: Throwable) -> Unit)? = null
) : Disposable {

    return subscribeOn(
            Schedulers.io()
    ).subscribe (
            { onSuccess(it) },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}