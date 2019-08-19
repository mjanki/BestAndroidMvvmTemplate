package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Flowable
import org.jetbrains.anko.doAsync
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepository(ctx: Context) {
    private var appDatabase: AppDatabase = AppDatabase(ctx)
    private var taskDao: TaskDao
    private var allTasks: Flowable<List<TaskDatabaseEntity>>

    init {
        taskDao = appDatabase.taskDao()
        allTasks = taskDao.getAll()
    }

    fun getTasks(): Flowable<List<TaskRepoEntity>> {
        return allTasks.flatMap {
            val newArray = ArrayList<TaskRepoEntity>()
            for (task in it) {
                newArray.add(TaskRepoEntity(
                        task.id,
                        task.name,
                        task.date,
                        task.status
                ))
            }

            return@flatMap Flowable.fromArray(newArray)
        }
    }

    fun insertTask(name: String) {
        val taskEntity = TaskDatabaseEntity()
        taskEntity.name = name

        doAsync {
            taskDao.insert(taskEntity)
        }
    }
}