package org.umbrellahq.util.extensions

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun Completable.subscribeBackgroundObserveOnMain(
        onSuccess: (() -> Unit)? = null,
        onFailure: ((throwable: Throwable) -> Unit)? = null
) : Disposable {

    return observeOn(
            AndroidSchedulers.mainThread()
    ).subscribeOn(
            Schedulers.io()
    ).subscribe (
            { onSuccess?.let { onSuccess() } },
            { throwable ->
                onFailure?.let { onFailure -> onFailure(throwable) }
            }
    )
}