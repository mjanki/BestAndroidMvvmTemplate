package org.umbrellahq.viewmodel.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.concurrent.schedule

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected var disposables = CompositeDisposable()
    private var clickLock = false

    protected fun handleNavigationClick(delay: Long = 0, function: () -> Unit) {
        if (!clickLock) {
            clickLock = true

            Timer().schedule(delay) {
                function()

                Timer().schedule(100) {
                    clickLock = false
                }
            }
        }
    }

    protected fun handleNavigationClickDelayed(function: () -> Unit) {
        handleNavigationClick(150, function)
    }

    override fun onCleared() {
        disposables.clear()
    }
}