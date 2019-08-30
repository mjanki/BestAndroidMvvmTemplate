package org.umbrellahq.util.extensions

import retrofit2.Response

fun <T> Response<T>.consume(
        onSuccess: ((value: T) -> Unit),
        onFailure: ((response: Response<T>) -> Unit)? = null
) {
    if (isSuccessful) {
        body()?.let {
            onSuccess(it)
        }
    } else if (onFailure != null) {
        onFailure(this)
    }
}