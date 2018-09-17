package org.umbrellahq.repository.dataSource

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.jetbrains.anko.doAsync
import org.umbrellahq.database.AppDatabase
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskEntity
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepository(ctx: Context) {
    private var appDatabase: AppDatabase? = null
    private var taskDao: TaskDao? = null
    private var allTasks: LiveData<List<TaskEntity>>? = null

    init {
        appDatabase = AppDatabase.getInstance(ctx)
        taskDao = appDatabase?.taskDao()
        allTasks = taskDao?.getAll()
    }

    //fun getTasks(): LiveData<List<TaskRepoEntity>>? {
    fun getTasks(): LiveData<List<TaskEntity>>? {
        return allTasks
        /*val taskRepoEntities = ArrayList<TaskRepoEntity>()

        println("MAYBE NULL")
        val values = allTasks?.value

        if (values != null) {
            println("VALUES NOT NULL")

            for (taskEntity in values) {
                println("ADDING")
                taskRepoEntities.add(
                        TaskRepoEntity(
                                id = taskEntity.id,
                                name = taskEntity.name,
                                date = taskEntity.date,
                                status = taskEntity.status))
            }
        }

        val taskRepoLiveData = MutableLiveData<List<TaskRepoEntity>>()
        taskRepoLiveData.value = taskRepoEntities
        return taskRepoLiveData*/
    }

    //fun insertTask(taskRepoEntity: TaskRepoEntity) {
    fun insertTask(name: String) {
        val taskEntity = TaskEntity()
        taskEntity.name = name

        doAsync {
            taskDao?.insert(taskEntity)
        }

        /*val taskEntity = TaskEntity()
        taskEntity.name = taskRepoEntity.name
        taskEntity.date = taskRepoEntity.date
        taskEntity.status = taskRepoEntity.status

        doAsync {
            println("INSERTING: ${taskEntity.name}, ${taskEntity.date}, ${taskEntity.id}")
            taskDao?.insert(taskEntity)
        }*/
    }
}