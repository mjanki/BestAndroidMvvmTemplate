package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Flowable
import org.jetbrains.anko.doAsync
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.repository.mappers.TaskDatabaseRepoMapper
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepository(ctx: Context) {
    private var appDatabase: AppDatabase = AppDatabase(ctx)
    private var taskDao: TaskDao
    private var allTasks: Flowable<List<TaskDatabaseEntity>>

    private var taskRepoDatabaseMapper = TaskDatabaseRepoMapper()

    init {
        taskDao = appDatabase.taskDao()
        allTasks = taskDao.getAll()
    }

    fun getTasks(): Flowable<List<TaskRepoEntity>> {
        return allTasks.flatMap { taskDatabaseEntityList ->
            return@flatMap Flowable.fromArray(
                    taskDatabaseEntityList.map { taskDatabaseEntity ->
                        taskRepoDatabaseMapper.upstream(taskDatabaseEntity)
                    }
            )
        }
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity) {
        doAsync {
            taskDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
        }
    }
}