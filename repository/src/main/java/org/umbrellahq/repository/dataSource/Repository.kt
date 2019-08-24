package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import org.umbrellahq.database.AppDatabase

open class Repository(ctx: Context) {
    protected var appDatabase: AppDatabase = AppDatabase(ctx)
    protected var disposables = CompositeDisposable()

    fun clearDisposables() {
        disposables.clear()
    }
}