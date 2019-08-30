package org.umbrellahq.network.daos

import org.umbrellahq.network.clients.TaskClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TaskNetworkDao {
    private var requestInterface: TaskClient = Retrofit.Builder()
            .baseUrl("https://demo2500655.mockable.io")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build().create(TaskClient::class.java)

    suspend fun loadTasks() = requestInterface.getTasks()
}