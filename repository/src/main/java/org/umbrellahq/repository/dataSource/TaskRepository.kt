package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.TaskDatabaseRepoMapper
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.util.extensions.consume
import org.umbrellahq.util.extensions.getValue

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
        CoroutineScope(Dispatchers.IO).launch {
            taskNetworkDao.loadTasks().consume(
                    onSuccess = { taskNetworkEntityList ->
                        // TODO: use network entity's UUID to sync with database

                        allTasks.getValue({ taskDatabaseEntity ->
                            // TODO: get database entities to update same UUIDs or insert
                        })
                    },
                    onFailure = {
                        // TODO: handle network error (figure out how)
                    }
            )
        }
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}