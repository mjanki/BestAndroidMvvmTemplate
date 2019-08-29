package org.umbrellahq.repository.dataSource

import android.content.Context
import org.umbrellahq.database.AppDatabase

open class Repository(ctx: Context) {
    protected var appDatabase: AppDatabase = AppDatabase(ctx)
}