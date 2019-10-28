package org.umbrellahq.network.daos

import io.reactivex.subjects.PublishSubject
import org.umbrellahq.network.clients.TaskClient
import org.umbrellahq.network.models.TaskNetworkEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class TaskNetworkDao : BaseNetworkDao() {
    private var requestInterface: TaskClient = Retrofit.Builder()
            .baseUrl("https://demo2500655.mockable.io")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(TaskClient::class.java)

    fun setRequestInterface(taskClient: TaskClient) {
        requestInterface = taskClient
    }

    val isRetrievingTasks = PublishSubject.create<Boolean>()
    val retrievedTasks = PublishSubject.create<Response<List<TaskNetworkEntity>>>()
    fun retrieveTasks() {
        isRetrievingTasks.onNext(true)
        executeNetworkCall(
                observable = requestInterface.getTasks(),
                action = "Fetching tasks from the cloud",
                onSuccess = {
                    retrievedTasks.onNext(it)
                },
                onComplete = {
                    isRetrievingTasks.onNext(false)
                }
        )
    }
}