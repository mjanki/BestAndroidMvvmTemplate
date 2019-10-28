package org.umbrellahq.viewmodel.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected var disposables = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()
    }
}