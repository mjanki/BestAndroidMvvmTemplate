package org.umbrellahq.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.database.daos.TaskDatabaseDao
import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.repository.models.TaskRepoEntity
import org.umbrellahq.repository.repositories.TaskRepository
import org.umbrellahq.util.extensions.RxKotlinExtensions
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class TaskRepositoryTest {
    private lateinit var taskRepository: TaskRepository

    // Network Data Source
    private lateinit var taskNetworkDao: TaskNetworkDao
    private val errorNetwork = PublishSubject.create<ErrorNetworkEntity>()
    private val retrievedTasks = PublishSubject.create<Response<List<TaskNetworkEntity>>>()
    private val isRetrievingTasks = PublishSubject.create<Boolean>()

    // Database Data Source
    private lateinit var taskDatabaseDao: TaskDatabaseDao
    private val databaseTasks = PublishSubject.create<List<TaskDatabaseEntity>>()

    // Mock Entities
    private var testTaskDatabaseEntity = TaskDatabaseEntity(
            id = 51,
            uuid = "UUID Test",
            name = "MOCK DATABASE ENTITY",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
            status = 1
    )

    private var testTaskNetworkEntity = TaskNetworkEntity(
            uuid = "UUID Test",
            name = "MOCK NETWORK ENTITY",
            date = "2007-12-23T10:15:30+01:00",
            status = 1
    )

    private var testTaskRepoEntity = TaskRepoEntity(
            id = 51L,
            uuid = "UUID Test",
            name = "MOCK ENTITY",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
            status = 1
    )

    @Before
    fun setup() {
        // Set to true to use correct subscribeOn scheduler for testing
        RxKotlinExtensions.isTesting = true

        taskRepository = TaskRepository()

        taskNetworkDao = mock {
            on { errorNetwork } doReturn errorNetwork
            on { retrievedTasks } doReturn retrievedTasks
            on { isRetrievingTasks } doReturn isRetrievingTasks
            on { retrieveTasks() } doAnswer {
                isRetrievingTasks.onNext(true)

                val taskNetworkEntityList = listOf(testTaskNetworkEntity)

                retrievedTasks.onNext(Response.success(taskNetworkEntityList))

                isRetrievingTasks.onNext(false)
            }
        }

        taskDatabaseDao = mock {
            on { insert(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as TaskDatabaseEntity
                val taskDatabaseEntityList = listOf(argument)
                databaseTasks.onNext(taskDatabaseEntityList)

                Single.just(1)
            }

            on { getAll() } doReturn Flowable.just(listOf(testTaskDatabaseEntity))
        }

        taskRepository.init(taskNetworkDao, taskDatabaseDao)
    }

    @Test
    fun getTasks_shouldGetMockDatabaseTasks() {
        // Change id for testing purposes
        testTaskDatabaseEntity.id = 55L

        // Initialize taskRepository again because we changed mock
        taskRepository.init(taskNetworkDao, taskDatabaseDao)

        // Observe getTasks()
        val testObserver = taskRepository.getTasks().test()

        // Check getTasks was emitted 1 time
        testObserver.assertValueCount(1)

        // Get Task emitted
        val values = testObserver.values()[0]

        // Check we have 1 task
        Assert.assertEquals(1, values.size)

        // Check it has mock values
        Assert.assertEquals(testTaskDatabaseEntity.id, values[0].id)
    }

    @Test
    fun retrieveTasks_shouldEmitMockNetworkTasks() {
        // Observe retrievedTasks
        val testObserver = retrievedTasks.test()

        // Retrieve tasks
        taskRepository.retrieveTasks()

        // Check retrievedTasks was emitted 1 time
        testObserver.assertValueCount(1)

        // Get Task emitted
        val value = testObserver.values()[0].body()

        // Check it's not null
        Assert.assertNotNull(value)

        value?.let {
            // Check we have 1 task
            Assert.assertEquals(1, it.size)

            // Check it has mock values
            Assert.assertEquals(testTaskNetworkEntity.name, it[0].name)
        }
    }

    @Test
    fun clearDisposables_shouldClearDisposables() {
        // Check taskRepository has 2 disposables (errorNetwork & retrievedTasks)
        Assert.assertEquals(2, taskRepository.getDisposablesSize())

        // Check they are not disposed yet
        Assert.assertEquals(false, taskRepository.getDisposableIsDisposed())

        // Clear Disposables
        taskRepository.clearDisposables()

        // Check they are now disposed
        Assert.assertEquals(0, taskRepository.getDisposablesSize())
    }

    @Test
    fun insertTask_mockDatabaseTaskEntity_shouldHaveOneTask() {
        // Observe databaseTasks
        val testObserver = databaseTasks.test()

        // Insert task
        taskRepository.insertTask(testTaskRepoEntity)

        // Check we have 1 task
        testObserver.assertValueCount(1)
    }
}