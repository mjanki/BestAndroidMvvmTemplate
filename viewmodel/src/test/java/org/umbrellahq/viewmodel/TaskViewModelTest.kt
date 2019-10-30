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
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.repository.models.TaskRepoEntity
import org.umbrellahq.repository.repositories.TaskRepository
import org.umbrellahq.viewmodel.models.TaskViewModelEntity
import org.umbrellahq.viewmodel.viewmodels.TaskViewModel

@RunWith(MockitoJUnitRunner::class)
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
    @Mock
    private lateinit var taskRepository: TaskRepository

    private val retrievedTasks = PublishSubject.create<List<TaskRepoEntity>>()
    private val isRetrievingTasks = PublishSubject.create<Boolean>()

    // Mock Entities
    private var testTaskRepoEntity = TaskRepoEntity(
            id = 42,
            uuid = "UUID Test",
            name = "MY UPDATED TASKS",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
    )

    private var testTaskViewModelEntity = TaskViewModelEntity(
            name = "MY TASKS",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
    )

    @Before
    fun setup() {
        taskVM = TaskViewModel(mock())
        taskVM.getAllTasks().observeForever(tasksObserver)
        taskVM.getIsRetrievingTasks().observeForever(isRetrievingTasksObserver)

        taskRepository = mock {
            on { isRetrievingTasks } doReturn isRetrievingTasks
            on { getTasks() } doReturn retrievedTasks.toFlowable(BackpressureStrategy.LATEST)
            on { retrieveTasks() } doAnswer {
                isRetrievingTasks.onNext(true)
                retrievedTasks.onNext(listOf(testTaskRepoEntity))
                isRetrievingTasks.onNext(false)
            }
            on { insertTask(any()) } doAnswer {
                Assert.assertEquals(1, it.arguments.size)

                val argument = it.arguments[0] as TaskRepoEntity
                retrievedTasks.onNext(listOf(argument))
            }
        }

        taskVM.init(testTaskRepository = taskRepository)
    }

    @Test
    fun getAllTasks_shouldEmitMockTasks() {
        // Emit mock tasks
        retrievedTasks.onNext(listOf(testTaskRepoEntity))

        // Check LiveData
        tasksArgumentCaptor.run {
            // Check 1 time emitted
            verify(tasksObserver, times(1)).onChanged(capture())

            // Check 1 task emitted
            Assert.assertEquals(1, value.size)

            // Check it has same mock values
            Assert.assertEquals(testTaskRepoEntity.id, value[0].id)
        }
    }

    @Test
    fun isRetrievingTasks_shouldEmitTrueThenFalse() {
        // Emit mock tasks
        taskVM.retrieveTasks()

        // Check LiveData
        isRetrievingTasksArgumentCaptor.run {
            // Check 2 times emitted
            verify(isRetrievingTasksObserver, times(2)).onChanged(capture())

            // Check first emission is True
            Assert.assertEquals(true, this.allValues[0])

            // Check second emission is False
            Assert.assertEquals(false, this.allValues[1])
        }
    }

    @Test
    fun retrieveTasks_shouldEmitMockTasks() {
        // Emit mock tasks
        taskVM.retrieveTasks()

        // Check LiveData
        tasksArgumentCaptor.run {
            // Check 1 time emitted
            verify(tasksObserver, times(1)).onChanged(capture())

            // Check has emitted task
            Assert.assertEquals(1, value.size)

            // Get emitted task
            val emittedTask = value[0]

            // Check it has same mock values
            Assert.assertEquals(testTaskRepoEntity.name, emittedTask.name)
        }
    }

    @Test
    fun insertTask_shouldEmitInsertedMockTask() {
        // Insert mock task
        taskVM.insertTask(testTaskViewModelEntity)

        // Check LiveData
        tasksArgumentCaptor.run {
            // Check 1 time emitted
            verify(tasksObserver, times(1)).onChanged(capture())

            // Check has emitted task
            Assert.assertEquals(1, value.size)

            // Get emitted task
            val emittedTask = value[0]

            // Check it has same mock values
            Assert.assertEquals(testTaskViewModelEntity.name, emittedTask.name)
            Assert.assertEquals("${testTaskViewModelEntity.date}", "${emittedTask.date}")
        }
    }
}