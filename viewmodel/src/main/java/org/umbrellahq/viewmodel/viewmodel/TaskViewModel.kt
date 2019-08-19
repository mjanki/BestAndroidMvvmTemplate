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
        disposables.add(taskRepository.getTasks().subscribe {
            val newArray = ArrayList<TaskViewModelEntity>()
            for (task in it) {
                newArray.add(TaskViewModelEntity(
                        task.id,
                        task.name,
                        task.date,
                        task.status
                ))
            }

            allTasks.postValue(newArray)
        })
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>>? {
        return allTasks
    }

    fun insertTask(name: String) {
        taskRepository.insertTask(name)
    }
}