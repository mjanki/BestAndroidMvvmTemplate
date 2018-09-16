package org.umbrellahq.repository.dataSource

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.model.TaskEntity
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskDataSource {
    fun getTasks(ctx: Context): LiveData<List<TaskEntity>> {
        val listLiveData = MutableLiveData<List<TaskEntity>>()
        listLiveData.value = AppDatabase.getInstance(ctx)?.taskDao()?.getAll()
        return listLiveData
    }

    fun insertTask(taskRepoEntity: TaskRepoEntity, ctx: Context) {
        val taskEntity = TaskEntity()
        taskEntity.name = taskRepoEntity.name
        taskEntity.date = taskRepoEntity.date
        taskEntity.status = taskRepoEntity.status
        AppDatabase.getInstance(ctx)?.taskDao()?.insert(taskEntity)
    }
}