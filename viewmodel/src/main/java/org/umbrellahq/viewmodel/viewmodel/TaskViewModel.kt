package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.viewmodel.mappers.TaskRepoViewModelMapper
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private var taskRepository: TaskRepository = TaskRepository(application)
    private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()

    private var disposables = CompositeDisposable()
    private var taskViewModelRepoMapper = TaskRepoViewModelMapper()

    init {
        disposables.add(taskRepository.getTasks().subscribe { taskRepoEntityList ->
            allTasks.postValue(taskRepoEntityList.map { taskRepoEntity ->
                taskViewModelRepoMapper.upstream(taskRepoEntity)
            })
        })
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>> {
        return allTasks
    }

    fun insertTask(name: String) {
        val taskViewModelEntity = TaskViewModelEntity()
        taskViewModelEntity.name = name

        taskRepository.insertTask(taskViewModelRepoMapper.downstream(taskViewModelEntity))
    }

    override fun onCleared() {
        disposables.clear()
    }
}