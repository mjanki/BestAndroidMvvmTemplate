package org.umbrellahq.util.extensions

import android.util.Log

fun Throwable.printError(tag: String) {
    Log.e(tag, toString())
    stackTrace.forEach {
        Log.e(tag, it.toString())
    }
}