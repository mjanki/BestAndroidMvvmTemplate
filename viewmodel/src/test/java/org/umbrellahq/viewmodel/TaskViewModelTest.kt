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
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.viewmodel.model.TaskViewModelEntity
import org.umbrellahq.viewmodel.viewmodel.TaskViewModel

class TaskViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var taskVM: TaskViewModel
    private val tasksObserver: Observer<List<TaskViewModelEntity>> = mock()
    private val isRetrievingTasksObserver: Observer<Boolean> = mock()

    @Captor
    private lateinit var tasksArgumentCaptor: ArgumentCaptor<List<TaskViewModelEntity>>

    @Captor
    private lateinit var isRetrievingTasksArgumentCaptor: ArgumentCaptor<Boolean>

    // Repository
    private lateinit var taskRepository: TaskRepository
    private val retrievedTasks = PublishSubject.create<List<TaskRepoEntity>>()
    private val isRetrievingTasks = PublishSubject.create<Boolean>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        taskVM = TaskViewModel(mock())
        taskVM.getAllTasks().observeForever(tasksObserver)
        taskVM.getIsRetrievingTasks().observeForever(isRetrievingTasksObserver)

        taskRepository = mock {
            on { isRetrievingTasks } doReturn isRetrievingTasks
            on { getTasks() } doReturn retrievedTasks.toFlowable(BackpressureStrategy.LATEST)
            on { updateTasks() } doAnswer {
                isRetrievingTasks.onNext(true)

                retrievedTasks.onNext(
                        listOf(
                                TaskRepoEntity(
                                        id = 42,
                                        name = "MY UPDATED TASKS",
                                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                                )
                        )
                )

                isRetrievingTasks.onNext(false)
            }
            on { insertTask(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as TaskRepoEntity
                retrievedTasks.onNext(
                        listOf(argument)
                )
            }
        }

        taskVM.init(testTaskRepository = taskRepository)
    }

    @Test
    fun getAllTasks() {
        retrievedTasks.onNext(
                listOf(
                        TaskRepoEntity(
                                id = 42,
                                name = "MY TASKS",
                                date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                        )
                )
        )

        tasksArgumentCaptor.run {
            verify(tasksObserver, times(1)).onChanged(capture())
            assertEquals(42.toLong(), value[0].id)
        }
    }

    @Test
    fun updateTasks() {
        taskVM.updateTasks()

        isRetrievingTasksArgumentCaptor.run {
            verify(isRetrievingTasksObserver, times(2)).onChanged(capture())
            assertEquals(2, this.allValues.size)
            assertEquals(true, this.allValues[0])
            assertEquals(false, this.allValues[1])
        }

        tasksArgumentCaptor.run {
            verify(tasksObserver, times(1)).onChanged(capture())
            assertEquals("MY UPDATED TASKS", value[0].name)
        }
    }

    @Test
    fun insertTask() {
        taskVM.insertTask(
                TaskViewModelEntity(
                        name = "MY TASKS",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                )
        )

        tasksArgumentCaptor.run {
            verify(tasksObserver, times(1)).onChanged(capture())
            assertEquals(value.size, 1)
            assertEquals("MY TASKS", value[0].name)
            assertEquals("2007-12-23T10:15:30+01:00", "${value[0].date}")
        }
    }
}