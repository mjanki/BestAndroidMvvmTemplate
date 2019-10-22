package org.umbrellahq.repository.dataSource

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.umbrellahq.database.dao.TaskDatabaseDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.ErrorNetworkRepoNetworkMapper
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.mappers.TaskRepoNetworkMapper
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.util.extensions.execute

class TaskRepository(ctx: Context? = null) : ErrorRepository(ctx) {
    // DAOs
    private lateinit var taskNetworkDao: TaskNetworkDao
    private lateinit var taskDatabaseDao: TaskDatabaseDao

    // Observables
    private lateinit var allTasks: Flowable<List<TaskDatabaseEntity>>

    // Mappers
    private var taskRepoDatabaseMapper = TaskRepoDatabaseMapper()
    private var taskRepoNetworkMapper = TaskRepoNetworkMapper()
    private var errorNetworkRepoNetworkMapper = ErrorNetworkRepoNetworkMapper()

    override fun init() {
        super.init()

        init(testTaskNetworkDao = null, testTaskDatabaseDao = null)
    }

    fun init(testTaskNetworkDao: TaskNetworkDao? = null, testTaskDatabaseDao: TaskDatabaseDao? = null) {
        taskNetworkDao = testTaskNetworkDao ?: TaskNetworkDao()
        taskDatabaseDao = testTaskDatabaseDao ?: appDatabase.taskDao()

        allTasks = taskDatabaseDao.getAll()

        isRetrievingTasks = taskNetworkDao.isRetrievingTasks

        taskNetworkDao.errorNetwork.subscribe {
            insertErrorNetwork(
                    errorNetworkRepoNetworkMapper.upstream(it)
            )
        }.addTo(disposables)

        taskNetworkDao.retrievedTasks.subscribe {
            it.body()?.let { taskNetworkEntities ->
                for (taskNetworkEntity in taskNetworkEntities) {
                    insertTask(taskRepoNetworkMapper.upstream(taskNetworkEntity)).execute()
                }
            }
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

    lateinit var isRetrievingTasks: PublishSubject<Boolean>
    fun updateTasks() {
        taskNetworkDao.retrieveTasks()
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity): Completable =
            taskDatabaseDao.insert(taskRepoDatabaseMapper.downstream(taskRepoEntity))
}