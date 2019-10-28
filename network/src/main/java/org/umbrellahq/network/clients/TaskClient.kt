package org.umbrellahq.network.clients

import io.reactivex.Observable
import org.umbrellahq.network.models.TaskNetworkEntity
import retrofit2.Response
import retrofit2.http.GET

interface TaskClient {

    @GET("tasks")
    fun getTasks(): Observable<Response<List<TaskNetworkEntity>>>
}