package org.umbrellahq.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.entity.TaskEntity

@Database(entities = arrayOf(TaskEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    //TODO: change database name
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "simplyToDo.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}