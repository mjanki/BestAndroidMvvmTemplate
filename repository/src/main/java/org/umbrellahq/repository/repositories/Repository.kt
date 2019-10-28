package org.umbrellahq.repository.repositories

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import org.umbrellahq.database.AppDatabase

open class Repository(ctx: Context?) {
    protected lateinit var appDatabase: AppDatabase
    init {
        ctx?.let {
            appDatabase = AppDatabase(it)
        }
    }

    // To override
    open fun init() { /* Implement in subclasses */ }

    // DAOs
    protected var disposables = CompositeDisposable()

    fun clearDisposables() {
        disposables.clear()
    }

    fun getDisposablesSize() = disposables.size()
    fun getDisposableIsDisposed() = disposables.isDisposed
}