package org.umbrellahq.network.clients

import org.umbrellahq.network.models.TaskNetworkEntity
import retrofit2.Response
import retrofit2.http.GET

interface TaskClient {

    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskNetworkEntity>>
}