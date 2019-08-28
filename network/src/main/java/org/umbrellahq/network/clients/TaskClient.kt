package org.umbrellahq.network.clients

import io.reactivex.Observable
import org.umbrellahq.network.model.TaskNetworkEntity
import retrofit2.http.GET

interface TaskClient {

    @GET("tasks")
    fun getTasks(): Observable<List<TaskNetworkEntity>>
}