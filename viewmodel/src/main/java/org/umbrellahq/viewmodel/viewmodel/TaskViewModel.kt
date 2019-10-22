package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.util.extensions.execute
import org.umbrellahq.viewmodel.mappers.TaskViewModelRepoMapper
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewModel(application: Application) : BaseViewModel(application) {
    private var taskRepository = TaskRepository(application)
    private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()

    private var taskViewModelRepoMapper = TaskViewModelRepoMapper()

    init {
        taskRepository.init()

        taskRepository.getTasks().subscribe { taskRepoEntityList ->
            allTasks.postValue(
                    taskRepoEntityList.map { taskRepoEntity ->
                        taskViewModelRepoMapper.upstream(taskRepoEntity)
                    }
            )
        }.addTo(disposables)

        taskRepository.isRetrievingTasks.subscribe {
            isRetrievingTasks.postValue(it)
        }.addTo(disposables)
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>> = allTasks

    private val isRetrievingTasks = MutableLiveData<Boolean>()
    fun getIsLoading(): LiveData<Boolean> = isRetrievingTasks

    fun insertTask(name: String) {
        val taskViewModelEntity = TaskViewModelEntity()
        taskViewModelEntity.name = name

        taskRepository.insertTask(
                taskViewModelRepoMapper.downstream(
                        taskViewModelEntity
                )
        ).execute()
    }

    fun updateTasks() {
        taskRepository.updateTasks()
    }
    override fun onCleared() {
        super.onCleared()
        taskRepository.clearDisposables()
    }
}