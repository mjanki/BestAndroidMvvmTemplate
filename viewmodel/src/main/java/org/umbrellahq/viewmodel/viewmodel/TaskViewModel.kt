package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.umbrellahq.repository.dataSource.TaskRepository
import org.umbrellahq.util.extensions.execute
import org.umbrellahq.viewmodel.mappers.TaskRepoViewModelMapper
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewModel(application: Application) : BaseViewModel(application) {
    private var taskRepository = TaskRepository(application)
    private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()

    private var taskViewModelRepoMapper = TaskRepoViewModelMapper()

    init {
        disposables.add(
                taskRepository.getTasks().subscribe { taskRepoEntityList ->
                    allTasks.postValue(
                            taskRepoEntityList.map { taskRepoEntity ->
                                taskViewModelRepoMapper.upstream(taskRepoEntity)
                            }
                    )
                }
        )
    }

    fun getAllTasks(): LiveData<List<TaskViewModelEntity>> = allTasks

    fun update() {
        taskRepository.updateTasksByMerging()
    }

    fun insertTask(name: String) {
        val taskViewModelEntity = TaskViewModelEntity()
        taskViewModelEntity.name = name

        disposables.add(
                taskRepository.insertTask(
                        taskViewModelRepoMapper.downstream(
                                taskViewModelEntity
                        )
                ).execute()
        )
    }
}