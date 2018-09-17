package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.umbrellahq.database.model.TaskEntity
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private var taskRepository: TaskRepository? = null
    private var allTasks: LiveData<List<TaskEntity>>? = null

    init {
        taskRepository = TaskRepository(application)
        allTasks = taskRepository?.getTasks()
    }

    //fun getAllTasks(): LiveData<List<TaskViewModelEntity>>? {
    fun getAllTasks(): LiveData<List<TaskEntity>>? {
        return allTasks
        /*val taskViewModelEntities = ArrayList<TaskViewModelEntity>()

        val taskRepoEntities = allTasks?.value
        if (taskRepoEntities != null) {
            for (taskEntity in taskRepoEntities) {
                taskViewModelEntities.add(
                        TaskViewModelEntity(
                                id = taskEntity.id,
                                name = taskEntity.name,
                                date = taskEntity.date,
                                status = taskEntity.status))
            }
        }


        val taskViewModelLiveData = MutableLiveData<List<TaskViewModelEntity>>()
        taskViewModelLiveData.value = taskViewModelEntities
        return taskViewModelLiveData*/
    }

    //fun insertTask(taskViewModelEntity: TaskViewModelEntity) {
    fun insertTask(name: String) {
        taskRepository?.insertTask(name)

        /*val taskRepoEntity = TaskEntity(
                name = taskViewModelEntity.name,
                date = taskViewModelEntity.date,
                status = taskViewModelEntity.status
        )

        taskRepository?.insertTask(taskRepoEntity)*/
    }
}