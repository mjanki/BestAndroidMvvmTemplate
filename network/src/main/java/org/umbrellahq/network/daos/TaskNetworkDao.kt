package org.umbrellahq.network.daos

import org.umbrellahq.network.clients.TaskClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class TaskNetworkDao {
    private var requestInterface: TaskClient = Retrofit.Builder()
            .baseUrl("https://demo2500655.mockable.io")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(TaskClient::class.java)

    fun loadTasks() = requestInterface.getTasks()
}