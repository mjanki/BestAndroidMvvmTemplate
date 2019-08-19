package org.umbrellahq.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.database.typeConverters.DateTypeConverter

@Database(entities = [TaskDatabaseEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "simplyToDo.db")
                .build()

        fun destroyInstance() {
            instance = null
        }
    }
}