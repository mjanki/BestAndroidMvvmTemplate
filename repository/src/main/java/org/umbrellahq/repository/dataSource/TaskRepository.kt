package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.TaskDatabaseRepoMapper
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.util.extensions.getValue
import org.umbrellahq.util.extensions.subscribeBackground

class TaskRepository(ctx: Context) : Repository(ctx) {

    private var taskNetworkDao = TaskNetworkDao()

    private var taskDao: TaskDao = appDatabase.taskDao()
    private var allTasks: Flowable<List<TaskDatabaseEntity>>

    private var taskRepoDatabaseMapper = TaskDatabaseRepoMapper()

    init {
        allTasks = taskDao.getAll()
    }

    fun getTasks(): Flowable<List<TaskRepoEntity>> {
        updateTasksByMerging()

        return allTasks.flatMap { taskDatabaseEntityList ->
            Flowable.fromArray(
                    taskDatabaseEntityList.map { taskDatabaseEntity ->
                        taskRepoDatabaseMapper.upstream(taskDatabaseEntity)
                    }
            )
        }
    }

    fun updateTasksByMerging() {
        allTasks.getValue({
            // TODO: do something with value of allTasks
        })

        taskNetworkDao.loadTasks().subscribeBackground(onSuccess = {
            // TODO: update database by merging
        }, onFailure = {
            // TODO: handle network error
        })
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}