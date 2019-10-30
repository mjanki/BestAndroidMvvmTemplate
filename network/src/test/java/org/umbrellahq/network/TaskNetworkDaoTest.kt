package org.umbrellahq.network

import io.reactivex.Observable
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
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.util.extensions.RxKotlinExtensions
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class TaskNetworkDaoTest {
    private lateinit var taskNetworkDao: TaskNetworkDao

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
        // Set to true to use correct subscribeOn scheduler for testing
        RxKotlinExtensions.isTesting = true

        taskNetworkDao = TaskNetworkDao()
    }

    @Test
    fun isRetrievingTasks_shouldReturnTrueThenFalseOnRetrieveTasks() {
        // Mock taskClient.getTasks() to return mock success
        Mockito.`when`(taskClient.getTasks()).thenReturn(
                Observable.just(Response.success(listOf(testTaskNetworkEntity)))
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        // Observe isRetrievingTasks
        val testObserver = taskNetworkDao.isRetrievingTasks.test()

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Check we have 2 values for isRetrievingTasks
        testObserver.assertValueCount(2)

        // Check first value is true
        testObserver.assertValueAt(0, true)

        // Check second value is false
        testObserver.assertValueAt(1, false)
    }

    @Test
    fun retrieveTasks_shouldEmitTasksOnSuccess() {
        // Mock taskClient.getTasks() to return mock success
        Mockito.`when`(taskClient.getTasks()).thenReturn(
                Observable.just(Response.success(listOf(testTaskNetworkEntity)))
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        // Observe retrievedTasks
        val testObserver = taskNetworkDao.retrievedTasks.test()

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Check we have 1 task
        testObserver.assertValueCount(1)

        // Get task body
        val result = testObserver.values()[0].body()

        // Check task body is not null
        Assert.assertNotNull(result)

        // Check that task body has same mock values
        result?.let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(testTaskNetworkEntity.uuid, result[0].uuid)
            Assert.assertEquals(testTaskNetworkEntity.name, result[0].name)
            Assert.assertEquals(testTaskNetworkEntity.date, result[0].date)
            Assert.assertEquals(testTaskNetworkEntity.status, result[0].status)
        }
    }

    @Test
    fun retrieveTasks_shouldEmitErrorAndNoTasksOnError() {
        // Mock taskClient.getTasks() to return mock error
        Mockito.`when`(taskClient.getTasks()).thenReturn(
                Observable.just(testResponseError)
        )

        // Set mock request interface
        taskNetworkDao.setRequestInterface(taskClient)

        // Observe retrievedTasks and errorNetwork
        val testRetrievedTasksObserver = taskNetworkDao.retrievedTasks.test()
        val testErrorNetworkObserver = taskNetworkDao.errorNetwork.test()

        // Retrieve tasks
        taskNetworkDao.retrieveTasks()

        // Check we have 0 retrieved tasks
        testRetrievedTasksObserver.assertValueCount(0)

        // Check we have 1 error
        testErrorNetworkObserver.assertValueCount(1)

        // Get error body
        val errorNetworkValue = testErrorNetworkObserver.values()[0]

        // Check that body has same mock values
        Assert.assertEquals(testResponseError.code(), errorNetworkValue.code)
    }
}