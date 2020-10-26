package org.umbrellahq.network.daos

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
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

    // TODO: replace with retrievedTasksChannel.asFlow() when it's out of preview
    private val retrievedTasksChannel = ConflatedBroadcastChannel<List<TaskNetworkEntity>>()
    fun getRetrievedTasksFlow(): Flow<List<TaskNetworkEntity>> = retrievedTasksChannel.openSubscription().receiveAsFlow()

    suspend fun retrieveTasks() {
        isRetrievingTasksFlow.value = true

        val tasks = executeNetworkCall(
                request = { requestInterface.getTasks() },
                action = "Fetching tasks from the cloud"
        )

        tasks?.let {
            retrievedTasksChannel.send(it)
        }

        isRetrievingTasksFlow.value = false
    }
}