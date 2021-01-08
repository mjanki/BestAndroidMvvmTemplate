package org.umbrellahq.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.umbrellahq.network.clients.TaskClient
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.network.models.TaskNetworkEntity
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class TaskNetworkDaoTest {
    private lateinit var taskNetworkDao: TaskNetworkDao
    private val coroutineDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var taskClient: TaskClient

    // Mock Entities
    private var testTaskNetworkEntity = TaskNetworkEntity(
            uuid = "MY UUID",
            name = "MOCK ENTITY",
            date = "N/A",
            status = 0
    )

    private var testResponseError = Response.error<List<TaskNetworkEntity>>(
            404,
            ResponseBody.create(
                    MediaType.parse("text/plain"),
                    "MY PERSONAL ERROR MESSAGE"
            )
    )

    @Before
    fun setUp() {
        taskNetworkDao = TaskNetworkDao()
    }

    @Test
    fun isRetrievingTasks_shouldReturnFalseThenTrueThenFalseOnRetrieveTasks() = coroutineDispatcher.runBlockingTest {
        // Mock taskClient.getTasks() to return mock success
        Mockito.`when`(
                taskClient.getTasks()
        ).thenReturn(
                Response.success(listOf(testTaskNetworkEntity))
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        val stateList = ArrayList<Boolean>()
        val job = launch {
            taskNetworkDao.getIsRetrievingTasksFlow().collect {
                stateList.add(it)
            }
        }

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Give time to collect all data
        delay(100)

        // Check we have 2 values for isRetrievingTasks
        Assert.assertEquals(3, stateList.size)

        // Check first value is false (initial value)
        Assert.assertEquals(false, stateList[0])

        // Check second value is true
        Assert.assertEquals(true, stateList[1])

        // Check third value is false
        Assert.assertEquals(false, stateList[2])

        // Cancel job so that the test can conclude
        job.cancel()
    }

    @Test
    fun retrieveTasks_shouldEmitTasksOnSuccess() = coroutineDispatcher.runBlockingTest {
        // Mock taskClient.getTasks() to return mock success
        Mockito.`when`(
                taskClient.getTasks()
        ).thenReturn(
                Response.success(listOf(testTaskNetworkEntity))
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        // Observe retrievedTasks and errorNetwork
        var tasks = ArrayList<TaskNetworkEntity>()

        val tasksJob = launch {
            taskNetworkDao.getRetrievedTasksFlow().collect {
                tasks = ArrayList(it)
            }
        }

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Give time to collect all data
        delay(100)

        // Check we have 1 task
        Assert.assertEquals(1, tasks.size)

        // Get task body
        val result = tasks.first()

        // Check that task body has same mock values
        Assert.assertEquals(testTaskNetworkEntity.uuid, result.uuid)
        Assert.assertEquals(testTaskNetworkEntity.name, result.name)
        Assert.assertEquals(testTaskNetworkEntity.date, result.date)
        Assert.assertEquals(testTaskNetworkEntity.status, result.status)

        tasksJob.cancel()
    }

    @Test
    fun retrieveTasks_shouldEmitErrorAndNoTasksOnError() = coroutineDispatcher.runBlockingTest {
        // Mock taskClient.getTasks() to return mock error
        Mockito.`when`(
                taskClient.getTasks()
        ).thenReturn(
                testResponseError
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        // Observe retrievedTasks and errorNetwork
        var tasks = ArrayList<TaskNetworkEntity>()
        val errors = ArrayList<ErrorNetworkEntity>()

        val tasksJob = launch {
            taskNetworkDao.getRetrievedTasksFlow().collect {
                tasks = ArrayList(it)
            }
        }

        val errorsJob = launch {
            taskNetworkDao.getErrorNetworkChannel().collect {
                errors.add(it)
            }
        }

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Give time to collect all data
        delay(100)

        // Check we have 0 retrieved tasks
        Assert.assertEquals(0, tasks.size)

        // Check we have 1 error
        Assert.assertEquals(1, errors.size)

        // Get error body
        val error = errors.first()

        // Check that body has same mock values
        Assert.assertEquals(testResponseError.code(), error.code)

        tasksJob.cancel()
        errorsJob.cancel()
    }
}