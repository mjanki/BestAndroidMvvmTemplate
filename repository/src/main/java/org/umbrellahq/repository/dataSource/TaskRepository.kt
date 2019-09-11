package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import org.umbrellahq.database.dao.TaskDatabaseDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.TaskDatabaseRepoMapper
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.util.extensions.getValue

class TaskRepository(ctx: Context) : Repository(ctx) {

    // DAOs
    private var taskNetworkDao = TaskNetworkDao()
    private var taskDatabaseDao: TaskDatabaseDao = appDatabase.taskDao()

    // Observables
    private var allTasks: Flowable<List<TaskDatabaseEntity>>

    // Mappers
    private var taskRepoDatabaseMapper = TaskDatabaseRepoMapper()

    init {
        allTasks = taskDatabaseDao.getAll()
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
        executeNetworkCall(
                observable = taskNetworkDao.loadTasks(),
                action = "Fetching tasks from the cloud",
                onSuccess = { taskNetworkEntityList ->
                    // TODO: use network entity's UUID to sync with database
                    println("NOTE NOTE CODE: ${taskNetworkEntityList.code()}")

                    allTasks.getValue({
                        // TODO: get database entities to update same UUIDs or insert
                    })
                }
        )
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDatabaseDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}