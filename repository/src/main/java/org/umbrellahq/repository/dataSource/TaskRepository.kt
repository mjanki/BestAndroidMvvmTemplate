package org.umbrellahq.repository.dataSource

import android.content.Context
import androidx.lifecycle.LiveData
import org.jetbrains.anko.doAsync
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.model.TaskEntity

class TaskRepository(ctx: Context) {
    private var appDatabase: AppDatabase? = null
    private var taskDao: TaskDao? = null
    private var allTasks: LiveData<List<TaskEntity>>? = null

    init {
        appDatabase = AppDatabase.getInstance(ctx)
        taskDao = appDatabase?.taskDao()
        allTasks = taskDao?.getAll()
    }

    fun getTasks(): LiveData<List<TaskEntity>>? {
        return allTasks
    }

    fun insertTask(name: String) {
        val taskEntity = TaskEntity()
        taskEntity.name = name

        doAsync {
            taskDao?.insert(taskEntity)
        }
    }
}