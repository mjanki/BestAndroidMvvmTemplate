package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import org.umbrellahq.database.dao.TaskDatabaseDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.ErrorNetworkRepoNetworkMapper
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepository(ctx: Context) : Repository(ctx) {
    // DAOs
    private var taskNetworkDao = TaskNetworkDao()
    private var taskDatabaseDao: TaskDatabaseDao = appDatabase.taskDao()

    // Observables
    private var allTasks: Flowable<List<TaskDatabaseEntity>>

    // Mappers
    private var taskRepoDatabaseMapper = TaskRepoDatabaseMapper()
    private var errorNetworkRepoNetworkMapper = ErrorNetworkRepoNetworkMapper()

    init {
        allTasks = taskDatabaseDao.getAll()

        taskNetworkDao.errorNetwork.subscribe {
            insertErrorNetwork(
                    errorNetworkRepoNetworkMapper.upstream(it)
            )
        }.addTo(disposables)
    }

    fun getTasks(): Flowable<List<TaskRepoEntity>> =
            allTasks.flatMap { taskDatabaseEntityList ->
                Flowable.fromArray(
                        taskDatabaseEntityList.map { taskDatabaseEntity ->
                            taskRepoDatabaseMapper.upstream(taskDatabaseEntity)
                        }
                )
            }

    val isRetrievingTasks = taskNetworkDao.isRetrievingTasks
    fun updateTasksByMerging() {
        taskNetworkDao.retrieveTasks()
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDatabaseDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}