package org.umbrellahq.viewmodel.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import org.umbrellahq.repository.repositories.TaskRepository
import org.umbrellahq.viewmodel.mappers.TaskViewModelRepoMapper
import org.umbrellahq.viewmodel.models.TaskViewModelEntity

class TaskViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var taskRepository: TaskRepository
    private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()

    private var taskViewModelRepoMapper = TaskViewModelRepoMapper()

    fun init() {
        init(testTaskRepository = null)
    }

    fun init(testTaskRepository: TaskRepository? = null) {
        taskRepository = testTaskRepository ?: TaskRepository(getApplication())
        taskRepository.init()

        taskRepository.getTasks().subscribe { taskRepoEntityList ->
            allTasks.postValue(
                    taskRepoEntityList.map { taskRepoEntity ->
                        taskViewModelRepoMapper.upstream(taskRepoEntity)
                    }.reversed()
            )
        }.addTo(disposables)

        taskRepository.isRetrievingTasks.subscribe {
            isRetrievingTasks.postValue(it)
        }.addTo(disposables)
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>> = allTasks

    private val isRetrievingTasks = MutableLiveData<Boolean>()
    fun getIsRetrievingTasks(): LiveData<Boolean> = isRetrievingTasks

    fun retrieveTasks() {
        taskRepository.retrieveTasks()
    }

    var insertingTask = false
    fun insertTask(taskViewModelEntity: TaskViewModelEntity) {
        taskRepository.insertTask(
                taskViewModelRepoMapper.downstream(
                        taskViewModelEntity
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        taskRepository.clearDisposables()
    }
}