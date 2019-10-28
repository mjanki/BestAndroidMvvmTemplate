package org.umbrellahq.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.database.daos.TaskDatabaseDao
import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.repository.repositories.TaskRepository
import org.umbrellahq.repository.models.TaskRepoEntity
import org.umbrellahq.util.extensions.RxKotlinExtensions
import retrofit2.Response

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

    @Before
    fun setup() {
        RxKotlinExtensions.isTesting = true

        taskRepository = TaskRepository()

        taskNetworkDao = mock {
            on { errorNetwork } doReturn errorNetwork
            on { retrievedTasks } doReturn retrievedTasks
            on { isRetrievingTasks } doReturn isRetrievingTasks
            on { retrieveTasks() } doAnswer {
                isRetrievingTasks.onNext(true)

                val taskNetworkEntityList = listOf(
                        TaskNetworkEntity(
                                uuid = "51",
                                name = "MOCK ENTITY",
                                date = "2007-12-23T10:15:30+01:00",
                                status = 1
                        )
                )

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

                Completable.complete()
            }

            on { getAll() } doReturn Flowable.just(listOf(TaskDatabaseEntity(
                    id = 51,
                    uuid = "UUID Test",
                    name = "MOCK ENTITY",
                    date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                    status = 1
            )))
        }

        taskRepository.init(taskNetworkDao, taskDatabaseDao)
    }

    @Test
    fun testGetTasks() {
        val testObserver = taskRepository.getTasks().test()

        testObserver.assertValueCount(1)

        val values = testObserver.values()[0]

        Assert.assertEquals(1, values.size)
        Assert.assertEquals(51L, values[0].id)
    }

    @Test
    fun testRetrieveTasks() {
        val testObserver = retrievedTasks.test()

        taskRepository.retrieveTasks()

        testObserver.assertValueCount(1)

        val value = testObserver.values()[0].body()

        Assert.assertNotNull(value)

        value?.let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("MOCK ENTITY", it[0].name)
        }
    }

    @Test
    fun testDisposables() {
        Assert.assertEquals(2, taskRepository.getDisposablesSize())
        Assert.assertEquals(false, taskRepository.getDisposableIsDisposed())

        taskRepository.clearDisposables()

        Assert.assertEquals(0, taskRepository.getDisposablesSize())
    }

    @Test
    fun testInsertTask() {
        val testObserver = databaseTasks.test()

        taskRepository.insertTask(TaskRepoEntity(
                id = 51L,
                uuid = "UUID Test",
                name = "MOCK ENTITY",
                date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                status = 1
        ))

        testObserver.assertValueCount(1)
    }
}