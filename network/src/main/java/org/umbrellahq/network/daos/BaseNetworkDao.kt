package org.umbrellahq.network.daos

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.receiveAsFlow
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseNetworkDao {
    // TODO: replace with retrievedTasksChannel.asFlow() when it's out of preview
    private val errorNetworkChannel = ConflatedBroadcastChannel<ErrorNetworkEntity>()
    fun getErrorNetworkChannel() = errorNetworkChannel.openSubscription().receiveAsFlow()

    protected suspend fun <T> executeNetworkCall(
            request: (suspend () -> Response<T>),
            action: String = "",
            shouldPersist: Boolean = false,
            onFailure: ((throwable: Throwable) -> Unit)? = null
    ): T? {
        var response: Response<T>? = null
        try {
            response = request.invoke()
            if (!response.isSuccessful) {
                val exception = HttpException(response)
                handleFailure(exception, action, shouldPersist)
                onFailure?.invoke(exception)

                response = null
            }
        } catch (exception: Exception) {
            handleFailure(exception, action, shouldPersist)
            onFailure?.invoke(exception)
        }

        return response?.body()
    }

    private suspend fun handleFailure(throwable: Throwable, action: String, shouldPersist: Boolean) {
        val errorNetworkEntity = ErrorNetworkEntity()

        throwable.message?.let {
            errorNetworkEntity.message = it
        }

        when (throwable) {
            is SocketTimeoutException -> {
                errorNetworkEntity.type = ErrorNetworkTypes.TIMEOUT
            }

            is IOException -> {
                errorNetworkEntity.type = ErrorNetworkTypes.IO
            }

            is HttpException -> {
                errorNetworkEntity.type = ErrorNetworkTypes.HTTP
                errorNetworkEntity.code = throwable.code()
            }

            else -> {
                errorNetworkEntity.type = ErrorNetworkTypes.OTHER
            }
        }

        errorNetworkEntity.shouldPersist = shouldPersist
        errorNetworkEntity.action = action

        errorNetworkChannel.send(errorNetworkEntity)
    }
}