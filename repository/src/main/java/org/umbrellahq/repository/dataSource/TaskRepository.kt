package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.TaskDatabaseRepoMapper
import org.umbrellahq.repository.model.TaskRepoEntity

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
        disposables.add(taskNetworkDao.loadTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // TODO: update database by merging

                    for (entity in it) {
                        println("NOTE NOTE: ---")
                        println("NOTE NOTE: NETWORK ENTITY UUID: ${entity.uuid}")
                        println("NOTE NOTE: NETWORK ENTITY NAME: ${entity.name}")
                        println("NOTE NOTE: NETWORK ENTITY DATE: ${entity.date}")
                        println("NOTE NOTE: NETWORK ENTITY STATUS: ${entity.status}")
                        println("NOTE NOTE: ---")
                    }
                }, {
                    // TODO: handle network error
                }))
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}