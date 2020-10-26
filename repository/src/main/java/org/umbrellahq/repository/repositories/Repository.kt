package org.umbrellahq.repository.repositories

import android.content.Context
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
}