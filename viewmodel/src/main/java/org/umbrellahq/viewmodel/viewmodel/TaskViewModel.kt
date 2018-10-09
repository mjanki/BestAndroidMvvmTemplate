package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.umbrellahq.model.TaskEntity
import org.umbrellahq.repository.dataSource.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private var taskRepository: TaskRepository? = null
    private var allTasks: LiveData<List<TaskEntity>>? = null

    init {
        taskRepository = TaskRepository(application)
        allTasks = taskRepository?.getTasks()
    }

    fun getAllTasks(): LiveData<List<TaskEntity>>? {
        return allTasks
    }

    fun insertTask(name: String) {
        taskRepository?.insertTask(name)
    }
}