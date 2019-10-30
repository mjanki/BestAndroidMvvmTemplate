package org.umbrellahq.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.umbrellahq.database.daos.ErrorNetworkDatabaseDao
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.repository.repositories.ErrorRepository
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.util.extensions.RxKotlinExtensions

@RunWith(MockitoJUnitRunner::class)
class ErrorRepositoryTest {
    private lateinit var errorRepository: ErrorRepository

    // Error Data Source
    @Mock
    private lateinit var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao
    private val databaseErrorsNetwork = PublishSubject.create<List<ErrorNetworkDatabaseEntity>>()

    // Mock Entities
    private var testErrorNetworkDatabaseEntity = ErrorNetworkDatabaseEntity(
            id = 55L,
            type = ErrorNetworkTypes.HTTP,
            action = "Test Should Emit Mock ErrorNetwork"
    )

    private var testErrorNetworkRepoEntity = ErrorNetworkRepoEntity(
            id = 52L,
            type = ErrorNetworkTypes.HTTP,
            action = "TESTING"
    )

    @Before
    fun setup() {
        // Set to true to use correct subscribeOn scheduler for testing
        RxKotlinExtensions.isTesting = true

        errorRepository = ErrorRepository()

        errorNetworkDatabaseDao = mock {
            on { delete(any<ErrorNetworkDatabaseEntity>()) } doAnswer {
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

                Single.just(1)
            }
        }
    }

    @Test
    fun getErrorsNetwork_shouldEmitMockErrorNetwork() {
        // Mock errorNetworkDatabaseDao.getAll() to return test data
        Mockito.`when`(errorNetworkDatabaseDao.getAll()).thenReturn(
                Flowable.just(listOf(testErrorNetworkDatabaseEntity))
        )

        // Initialize errorRepository with mock errorNetworkDatabaseDao
        errorRepository.init(errorNetworkDatabaseDao)

        // Observe errorsNetwork
        val testObservable = errorRepository.getErrorsNetwork().test()

        // Check errorNetwork was emitted 1 time
        testObservable.assertValueCount(1)

        // Check we have 1 errorNetwork
        val values = testObservable.values()[0]
        Assert.assertEquals(1, values.size)

        // Get the only errorNetwork
        val value = values[0]

        // Check it has the same values as the mock data
        Assert.assertEquals(testErrorNetworkDatabaseEntity.id, value.id)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.type, value.type)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.action, value.action)
    }

    @Test
    fun deleteErrorNetwork_shouldEmitErrorNetworkDeletedThenEmpty() {
        // Initialize errorRepository with mock errorNetworkDatabaseDao
        errorRepository.init(errorNetworkDatabaseDao)

        // Observe errorsNetwork
        val testObservable = databaseErrorsNetwork.test()

        // Delete errorNetwork
        errorRepository.deleteErrorNetwork(testErrorNetworkRepoEntity)

        // Check errorNetwork was emitted 2 times (errorNetwork then Empty)
        testObservable.assertValueCount(2)

        // Get first value that should be the errorNetwork to be deleted
        val value1 = testObservable.values()[0]

        // Check it has 1 error network with mock values
        Assert.assertEquals(1, value1.size)
        Assert.assertEquals(testErrorNetworkRepoEntity.id, value1[0].id)
        Assert.assertEquals(testErrorNetworkRepoEntity.type, value1[0].type)
        Assert.assertEquals(testErrorNetworkRepoEntity.action, value1[0].action)

        // Get second value that should be an empty list of errorNetwork
        val value2 = testObservable.values()[1]

        // Check it is empty
        Assert.assertEquals(0, value2.size)
    }

    @Test
    fun insertErrorNetwork_shouldEmitInsertedErrorNetwork() {
        // Initialize errorRepository with mock errorNetworkDatabaseDao
        errorRepository.init(errorNetworkDatabaseDao)

        // Observe errorsNetwork
        val testObservable = databaseErrorsNetwork.test()

        // Insert errorNetwork
        errorRepository.insertErrorNetwork(testErrorNetworkRepoEntity)

        // Check errorNetwork was emitted 1 time
        testObservable.assertValueCount(1)

        // Get emitted value
        val value = testObservable.values()[0]

        // Check it has 1 errorNetwork with mock values
        Assert.assertEquals(1, value.size)
        Assert.assertEquals(testErrorNetworkRepoEntity.id, value[0].id)
        Assert.assertEquals(testErrorNetworkRepoEntity.type, value[0].type)
        Assert.assertEquals(testErrorNetworkRepoEntity.action, value[0].action)
    }
}