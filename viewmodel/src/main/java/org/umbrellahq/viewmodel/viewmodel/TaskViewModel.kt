package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private var taskRepository: TaskRepository = TaskRepository(application)

    private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()

    private var disposables = CompositeDisposable()

    init {
        disposables.add(taskRepository.getTasks().subscribe { taskRepoEntityList ->
            allTasks.postValue(taskRepoEntityList.map { taskRepoEntity ->
                TaskViewModelEntity(taskRepoEntity)
            })
        })
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>> {
        return allTasks
    }

    fun insertTask(name: String) {
        val taskViewModelEntity = TaskViewModelEntity()
        taskViewModelEntity.name = name

        taskRepository.insertTask(taskViewModelEntity.mapToRepo())
    }

    override fun onCleared() {
        disposables.clear()
    }
}