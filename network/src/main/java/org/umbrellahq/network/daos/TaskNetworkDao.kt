package org.umbrellahq.network.daos

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.umbrellahq.network.clients.TaskClient
import org.umbrellahq.network.models.TaskNetworkEntity
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TaskNetworkDao : BaseNetworkDao() {
    private var requestInterface: TaskClient = Retrofit.Builder()
            .baseUrl("https://demo2500655.mockable.io")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build().create(TaskClient::class.java)

    fun setRequestInterface(taskClient: TaskClient) {
        requestInterface = taskClient
    }

    private val isRetrievingTasksFlow = MutableStateFlow(false)
    fun getIsRetrievingTasksFlow(): StateFlow<Boolean> = isRetrievingTasksFlow

    private val retrievedTasksSharedFlow = MutableSharedFlow<List<TaskNetworkEntity>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun getRetrievedTasksFlow() = retrievedTasksSharedFlow.distinctUntilChanged()

    suspend fun retrieveTasks() {
        isRetrievingTasksFlow.value = true

        val tasks = executeNetworkCall(
                request = { requestInterface.getTasks() },
                action = "Fetching tasks from the cloud"
        )

        tasks?.let {
            retrievedTasksSharedFlow.emit(it)
        }

        isRetrievingTasksFlow.value = false
    }
}