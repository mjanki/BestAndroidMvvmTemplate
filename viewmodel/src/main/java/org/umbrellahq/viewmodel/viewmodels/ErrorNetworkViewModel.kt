package org.umbrellahq.viewmodel.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.umbrellahq.repository.repositories.ErrorRepository
import org.umbrellahq.viewmodel.mappers.ErrorNetworkViewModelRepoMapper
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity

class ErrorNetworkViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var errorRepository: ErrorRepository
    private var errorsNetwork = MutableLiveData<List<ErrorNetworkViewModelEntity>>()

    private var errorNetworkViewModelRepoMapper = ErrorNetworkViewModelRepoMapper()

    fun init() {
        init(testErrorRepository = null)
    }

    fun init(testErrorRepository: ErrorRepository? = null) {
        errorRepository = testErrorRepository ?: ErrorRepository(getApplication())
        errorRepository.init()

        viewModelScope.launch(Dispatchers.IO) {
            errorRepository.getErrorsNetwork().collect {
                errorsNetwork.postValue(it.map { error -> errorNetworkViewModelRepoMapper.upstream(error) })
            }
        }
    }

    fun getErrorsNetwork(): LiveData<List<ErrorNetworkViewModelEntity>> = errorsNetwork

    fun deleteErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            errorRepository.deleteErrorNetwork(
                    errorNetworkViewModelRepoMapper.downstream(
                            errorNetworkViewModelEntity
                    )
            )
        }
    }
}