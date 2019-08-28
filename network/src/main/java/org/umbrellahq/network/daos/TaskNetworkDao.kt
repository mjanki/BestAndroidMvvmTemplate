package org.umbrellahq.network.daos

import com.google.gson.GsonBuilder
import org.umbrellahq.network.clients.TaskClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TaskNetworkDao {
    var requestInterface: TaskClient

    init {
        val gson = GsonBuilder().setLenient().create()

        requestInterface = Retrofit.Builder()
                .baseUrl("https://demo2500655.mockable.io")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(TaskClient::class.java)
    }

    fun loadTasks() = requestInterface.getTasks()
}