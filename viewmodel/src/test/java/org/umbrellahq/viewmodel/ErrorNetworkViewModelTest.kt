package org.umbrellahq.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.MockitoAnnotations
import org.umbrellahq.repository.repositories.ErrorRepository
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity
import org.umbrellahq.viewmodel.viewmodels.ErrorNetworkViewModel

class ErrorNetworkViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var errorNetworkVM: ErrorNetworkViewModel
    private val errorsNetworkObserver: Observer<List<ErrorNetworkViewModelEntity>> = mock()

    @Captor
    private lateinit var errorsNetworkArgumentCaptor: ArgumentCaptor<List<ErrorNetworkViewModelEntity>>

    // Repository
    private lateinit var errorRepository: ErrorRepository
    private val retrievedErrors = PublishSubject.create<List<ErrorNetworkRepoEntity>>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        errorNetworkVM = ErrorNetworkViewModel(mock())
        errorNetworkVM.getErrorsNetwork().observeForever(errorsNetworkObserver)

        errorRepository = mock {
            on { getErrorsNetwork() } doReturn retrievedErrors.toFlowable(BackpressureStrategy.LATEST)
            on { deleteErrorNetwork(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as ErrorNetworkRepoEntity
                retrievedErrors.onNext(
                        listOf(argument)
                )
            }
        }

        errorNetworkVM.init(testErrorRepository = errorRepository)
    }

    @Test
    fun getAllErrors() {
        retrievedErrors.onNext(
                listOf(
                        ErrorNetworkRepoEntity(
                                id = 42,
                                type = ErrorNetworkTypes.HTTP,
                                message = "TESTING ERROR VIEW MODEL"
                        )
                )
        )

        errorsNetworkArgumentCaptor.run {
            verify(errorsNetworkObserver, times(1)).onChanged(capture())
            assertEquals(42.toLong(), value[0].id)
        }
    }

    @Test
    fun insertTask() {
        errorNetworkVM.deleteErrorNetwork(
                ErrorNetworkViewModelEntity(
                        id = 42,
                        type = ErrorNetworkTypes.HTTP,
                        message = "TESTING ERROR VIEW MODEL"
                )
        )

        errorsNetworkArgumentCaptor.run {
            verify(errorsNetworkObserver, times(1)).onChanged(capture())
            assertEquals(value.size, 1)
            assertEquals(42.toLong(), value[0].id)
            assertEquals(ErrorNetworkTypes.HTTP, value[0].type)
            assertEquals("TESTING ERROR VIEW MODEL", value[0].message)
        }
    }
}