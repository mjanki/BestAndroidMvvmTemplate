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
import org.umbrellahq.database.daos.ErrorNetworkDatabaseDao
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.repositories.ErrorRepository
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.util.extensions.RxKotlinExtensions

class ErrorRepositoryTest {
    private lateinit var errorRepository: ErrorRepository

    // Error Data Source
    private lateinit var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao
    private val databaseErrorsNetwork = PublishSubject.create<List<ErrorNetworkDatabaseEntity>>()

    @Before
    fun setup() {
        RxKotlinExtensions.isTesting = true

        errorRepository = ErrorRepository()

        errorNetworkDatabaseDao = mock {
            on { getAll() } doReturn Flowable.just(listOf(ErrorNetworkDatabaseEntity(
                    id = 51L,
                    type = ErrorNetworkTypes.HTTP,
                    action = "TESTING"
            )))

            on { delete(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as ErrorNetworkDatabaseEntity
                databaseErrorsNetwork.onNext(listOf(argument))
                databaseErrorsNetwork.onNext(listOf())

                Completable.complete()
            }

            on { insert(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as ErrorNetworkDatabaseEntity
                databaseErrorsNetwork.onNext(listOf(argument))

                Completable.complete()
            }
        }

        errorRepository.init(errorNetworkDatabaseDao)
    }

    @Test
    fun testGetErrorsNetwork() {
        val testObservable = errorRepository.getErrorsNetwork().test()

        testObservable.assertValueCount(1)

        val values = testObservable.values()[0]
        Assert.assertEquals(1, values.size)

        val value = values[0]
        Assert.assertEquals(51L, value.id)
        Assert.assertEquals(ErrorNetworkTypes.HTTP, value.type)
        Assert.assertEquals("TESTING", value.action)
    }

    @Test
    fun testDelete() {
        val testObservable = databaseErrorsNetwork.test()

        errorRepository.deleteErrorNetwork(
                ErrorNetworkRepoEntity(
                        id = 52L,
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        testObservable.assertValueCount(2)

        val value1 = testObservable.values()[0]
        Assert.assertEquals(1, value1.size)
        Assert.assertEquals(52L, value1[0].id)
        Assert.assertEquals(ErrorNetworkTypes.HTTP, value1[0].type)
        Assert.assertEquals("TESTING", value1[0].action)

        Assert.assertEquals(0, testObservable.values()[1].size)
    }

    @Test
    fun testInsert() {
        val testObservable = databaseErrorsNetwork.test()

        errorRepository.insertErrorNetwork(
                ErrorNetworkRepoEntity(
                        id = 52L,
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        testObservable.assertValueCount(1)

        val value = testObservable.values()[0]
        Assert.assertEquals(1, value.size)
        Assert.assertEquals(52L, value[0].id)
        Assert.assertEquals(ErrorNetworkTypes.HTTP, value[0].type)
        Assert.assertEquals("TESTING", value[0].action)
    }
}