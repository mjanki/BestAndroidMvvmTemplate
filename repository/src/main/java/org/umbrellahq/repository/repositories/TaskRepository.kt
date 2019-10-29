package org.umbrellahq.repository.repositories

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.umbrellahq.database.daos.TaskDatabaseDao
import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.ErrorNetworkRepoNetworkMapper
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.mappers.TaskRepoNetworkMapper
import org.umbrellahq.repository.models.TaskRepoEntity
import org.umbrellahq.util.extensions.execute
import org.umbrellahq.util.extensions.getValue

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

        taskNetworkDao.errorNetwork.subscribe { errorNetworkEntity ->
            insertErrorNetwork(
                    errorNetworkRepoNetworkMapper.upstream(
                            errorNetworkEntity
                    )
            )
        }.addTo(disposables)

        taskNetworkDao.retrievedTasks.subscribe {
            it.body()?.let { taskNetworkEntities ->
                for (taskNetworkEntity in taskNetworkEntities) {
                    val taskRepoEntity = taskRepoNetworkMapper.upstream(taskNetworkEntity)

                    taskDatabaseDao.getByUUID(taskNetworkEntity.uuid).getValue(
                            onSuccess = { taskDatabaseEntities ->
                                if (taskDatabaseEntities.isNotEmpty()) {
                                    taskRepoEntity.id = taskDatabaseEntities[0].id
                                }

                                insertTask(taskRepoEntity)
                            }
                    )


                }
            }
        }.addTo(disposables)
    }

    fun getTasks(): Flowable<List<TaskRepoEntity>> =
            allTasks.flatMap { taskDatabaseEntityList ->
                Flowable.fromArray(
                        taskDatabaseEntityList.map { taskDatabaseEntity ->
                            taskRepoDatabaseMapper.upstream(
                                    taskDatabaseEntity
                            )
                        }
                )
            }

    lateinit var isRetrievingTasks: PublishSubject<Boolean>
    fun retrieveTasks() {
        taskNetworkDao.retrieveTasks()
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity) {
        taskDatabaseDao.insert(
                taskRepoDatabaseMapper.downstream(
                        taskRepoEntity
                )
        ).execute()
    }
}