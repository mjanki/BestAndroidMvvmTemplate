package org.umbrellahq.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.repository.repositories.ErrorRepository
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity
import org.umbrellahq.viewmodel.viewmodels.ErrorNetworkViewModel

@RunWith(MockitoJUnitRunner::class)
class ErrorNetworkViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var errorNetworkVM: ErrorNetworkViewModel
    private val errorsNetworkObserver: Observer<List<ErrorNetworkViewModelEntity>> = mock()

    @Captor
    private lateinit var errorsNetworkArgumentCaptor: ArgumentCaptor<List<ErrorNetworkViewModelEntity>>

    // Repository
    @Mock
    private lateinit var errorRepository: ErrorRepository

    private var retrievedErrors = PublishSubject.create<List<ErrorNetworkRepoEntity>>()

    // Mock Entities
    private var testErrorNetworkRepoEntity = ErrorNetworkRepoEntity(
            id = 42,
            type = ErrorNetworkTypes.HTTP,
            message = "TESTING ERROR VIEW MODEL"
    )

    private var testErrorNetworkViewModelEntity = ErrorNetworkViewModelEntity(
            id = 42,
            type = ErrorNetworkTypes.HTTP,
            message = "TESTING ERROR VIEW MODEL"
    )

    @Before
    fun setup() {
        errorNetworkVM = ErrorNetworkViewModel(mock())
        errorNetworkVM.getErrorsNetwork().observeForever(errorsNetworkObserver)

        errorRepository = mock {
            on { getErrorsNetwork() } doReturn retrievedErrors.toFlowable(BackpressureStrategy.LATEST)
            on { deleteErrorNetwork(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as ErrorNetworkRepoEntity
                retrievedErrors.onNext(listOf(argument))
                retrievedErrors.onNext(listOf())
            }
        }

        errorNetworkVM.init(testErrorRepository = errorRepository)
    }

    @Test
    fun getErrorsNetwork_shouldEmitMockErrorsNetwork() {
        // Emit mock errors
        retrievedErrors.onNext(listOf(testErrorNetworkRepoEntity))

        // Check LiveData
        errorsNetworkArgumentCaptor.run {
            // Check 1 time emitted
            verify(errorsNetworkObserver, times(1)).onChanged(capture())

            // Check 1 error emitted
            Assert.assertEquals(1, value.size)

            // Check error has same mock values
            Assert.assertEquals(testErrorNetworkRepoEntity.id, value[0].id)
        }
    }

    @Test
    fun deleteErrorNetwork_shouldEmitErrorNetworkThenEmpty() {
        // Delete mock error
        errorNetworkVM.deleteErrorNetwork(testErrorNetworkViewModelEntity)

        // Check LiveData
        errorsNetworkArgumentCaptor.run {
            // Check 2 emissions happened (deleted error and empty errors)
            verify(errorsNetworkObserver, times(2)).onChanged(capture())

            // Get first emission
            val firstEmission = allValues[0]

            // Check it has 1 error
            Assert.assertEquals(1, firstEmission.size)

            // Get error
            val firstValue = firstEmission[0]

            // Check it has same mock values
            Assert.assertEquals(testErrorNetworkViewModelEntity.id, firstValue.id)
            Assert.assertEquals(testErrorNetworkViewModelEntity.type, firstValue.type)
            Assert.assertEquals(testErrorNetworkViewModelEntity.message, firstValue.message)

            // Get second emission
            val secondEmission = allValues[1]

            // Check it has no errors
            Assert.assertEquals(0, secondEmission.size)
        }
    }
}