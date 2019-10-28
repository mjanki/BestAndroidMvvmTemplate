package org.umbrellahq.network

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.umbrellahq.network.clients.TaskClient
import org.umbrellahq.network.daos.TaskNetworkDao
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.util.extensions.RxKotlinExtensions
import retrofit2.Response

class TaskNetworkDaoTest {
    private lateinit var taskNetworkDao: TaskNetworkDao

    private lateinit var successTaskClient: TaskClient
    private lateinit var failureTaskClient: TaskClient

    @Before
    fun setUp() {
        RxKotlinExtensions.isTesting = true

        val taskNetworkEntityList = listOf(
                TaskNetworkEntity(
                        uuid = "MY UUID",
                        name = "MOCK ENTITY",
                        date = "N/A",
                        status = 0
                )
        )

        successTaskClient = mock {
            on { getTasks() } doReturn Observable.just(
                    Response.success(
                            taskNetworkEntityList
                    )
            )
        }

        failureTaskClient = mock {
            on { getTasks() } doReturn Observable.just(
                    Response.error(
                            404,
                            ResponseBody.create(
                                    MediaType.parse("text/plain"),
                                    "MY PERSONAL ERROR MESSAGE"
                            )
                    )
            )
        }

        taskNetworkDao = TaskNetworkDao()
    }

    @Test
    fun isRetrievingTasks() {
        taskNetworkDao.setRequestInterface(successTaskClient)

        val testObserver = taskNetworkDao.isRetrievingTasks.test()

        taskNetworkDao.retrieveTasks()
        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0, true)
        testObserver.assertValueAt(1, false)
    }

    @Test
    fun testRetrieveTasksSuccess() {
        taskNetworkDao.setRequestInterface(successTaskClient)

        val testObserver = taskNetworkDao.retrievedTasks.test()

        taskNetworkDao.retrieveTasks()

        testObserver.assertValueCount(1)

        val result = testObserver.values()[0].body()

        Assert.assertNotNull(result)

        result?.let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("MY UUID", result[0].uuid)
            Assert.assertEquals("MOCK ENTITY", result[0].name)
            Assert.assertEquals("N/A", result[0].date)
            Assert.assertEquals(0, result[0].status)
        }
    }

    @Test
    fun testRetrieveTasksFailure() {
        taskNetworkDao.setRequestInterface(failureTaskClient)

        val testRetrievedTasksObserver = taskNetworkDao.retrievedTasks.test()
        val testErrorNetworkObserver = taskNetworkDao.errorNetwork.test()

        taskNetworkDao.retrieveTasks()

        testRetrievedTasksObserver.assertValueCount(0)
        testErrorNetworkObserver.assertValueCount(1)

        val errorNetworkValue = testErrorNetworkObserver.values()[0]

        Assert.assertEquals(404, errorNetworkValue.code)
    }
}