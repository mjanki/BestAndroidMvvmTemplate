package org.umbrellahq.repository.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.umbrellahq.database.daos.TaskDatabaseDao
import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.repository.mappers.ErrorNetworkRepoNetworkMapper
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.mappers.TaskRepoNetworkMapper
import org.umbrellahq.repository.models.TaskRepoEntity

class TaskRepository(ctx: Context? = null) : ErrorRepository(ctx) {
    // DAOs
    private lateinit var taskNetworkDao: TaskNetworkDao
    private lateinit var taskDatabaseDao: TaskDatabaseDao

    // Observables
    private lateinit var allTasks: Flow<List<TaskDatabaseEntity>>
    lateinit var isRetrievingTasksFlow: StateFlow<Boolean>

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

        isRetrievingTasksFlow = taskNetworkDao.getIsRetrievingTasksFlow()

        GlobalScope.launch(Dispatchers.IO) {
            taskNetworkDao.getErrorNetworkChannel().collect { errorNetworkEntity ->
                val errorRepoEntity = errorNetworkRepoNetworkMapper.upstream(errorNetworkEntity)
                insertErrorNetwork(errorRepoEntity)
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            taskNetworkDao.getRetrievedTasksFlow().collectLatest { taskNetworkEntities ->
                for (taskNetworkEntity in taskNetworkEntities) {
                    val taskRepoEntity = taskRepoNetworkMapper.upstream(taskNetworkEntity)

                    taskDatabaseDao.getByUUID(taskNetworkEntity.uuid)?.let { taskDatabaseEntity ->
                        taskRepoEntity.id = taskDatabaseEntity.id
                    }

                    insertTask(taskRepoEntity)
                }
            }
        }
    }

    fun getTasks(): Flow<List<TaskRepoEntity>> =
            allTasks.map { taskDatabaseEntityList ->
                taskDatabaseEntityList.map { taskDatabaseEntity ->
                    taskRepoDatabaseMapper.upstream(
                            taskDatabaseEntity
                    )
                }
            }

    suspend fun retrieveTasks() {
        taskNetworkDao.retrieveTasks()
    }

    suspend fun insertTask(taskRepoEntity: TaskRepoEntity) {
        taskDatabaseDao.insert(
                taskRepoDatabaseMapper.downstream(
                        taskRepoEntity
                )
        )
    }
}