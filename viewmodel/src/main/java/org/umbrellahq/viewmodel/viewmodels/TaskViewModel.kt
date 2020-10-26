package org.umbrellahq.viewmodel.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.umbrellahq.repository.repositories.TaskRepository
import org.umbrellahq.viewmodel.mappers.TaskViewModelRepoMapper
import org.umbrellahq.viewmodel.models.TaskViewModelEntity

class TaskViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var taskRepository: TaskRepository
    lateinit var allTasks: LiveData<List<TaskViewModelEntity>>

    private var taskViewModelRepoMapper = TaskViewModelRepoMapper()

    fun init() {
        init(testTaskRepository = null)
    }

    fun init(testTaskRepository: TaskRepository? = null) {
        taskRepository = testTaskRepository ?: TaskRepository(getApplication())
        taskRepository.init()

        allTasks = liveData(Dispatchers.IO) {
            taskRepository.getTasks().collect { taskRepoEntityList ->
                emit(taskRepoEntityList.map { taskRepoEntity ->
                    taskViewModelRepoMapper.upstream(taskRepoEntity)
                }.reversed())
            }
        }

        isRetrievingTasks = taskRepository.isRetrievingTasksFlow.asLiveData(Dispatchers.IO)
    }

    private lateinit var isRetrievingTasks: LiveData<Boolean>
    fun getIsRetrievingTasks(): LiveData<Boolean> = isRetrievingTasks

    fun retrieveTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.retrieveTasks()
        }
    }

    var insertingTask = false
    fun insertTask(taskViewModelEntity: TaskViewModelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.insertTask(
                    taskViewModelRepoMapper.downstream(
                            taskViewModelEntity
                    )
            )
        }
    }
}