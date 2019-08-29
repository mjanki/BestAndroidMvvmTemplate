package org.umbrellahq.util.extensions

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun Completable.subscribeBackground(
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

fun <T> Flowable<T>.getValue(
        onSuccess: ((value: T) -> Unit),
        onFailure: ((throwable: Throwable) -> Unit)? = null
) {
    Completable.fromAction {
        onSuccess(blockingFirst())
    }.subscribeBackground(onFailure = onFailure)
}

fun <T> Observable<T>.subscribeBackground(
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