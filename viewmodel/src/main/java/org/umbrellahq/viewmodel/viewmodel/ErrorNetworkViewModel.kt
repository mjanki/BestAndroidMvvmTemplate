package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import org.umbrellahq.repository.dataSource.ErrorRepository
import org.umbrellahq.viewmodel.mappers.ErrorNetworkViewModelRepoMapper
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity

class ErrorNetworkViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var errorRepository: ErrorRepository
    private var errorsNetwork = MutableLiveData<List<ErrorNetworkViewModelEntity>>()

    private var errorNetworkRepoViewModelMapper = ErrorNetworkViewModelRepoMapper()

    fun init() {
        init(testErrorRepository = null)
    }

    fun init(testErrorRepository: ErrorRepository? = null) {
        errorRepository = testErrorRepository ?: ErrorRepository(getApplication())
        errorRepository.init()

        errorRepository.getErrorsNetwork().subscribe { errorNetworkRepoEntityList ->
            errorsNetwork.postValue(
                    errorNetworkRepoEntityList.map { errorNetworkRepoEntity ->
                        errorNetworkRepoViewModelMapper.upstream(errorNetworkRepoEntity)
                    }
            )
        }.addTo(disposables)
    }

    fun getErrorsNetwork(): LiveData<List<ErrorNetworkViewModelEntity>> = errorsNetwork

    fun deleteErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity) {
        errorRepository.deleteErrorNetwork(
                errorNetworkRepoViewModelMapper.downstream(
                        errorNetworkViewModelEntity
                )
        )
    }
}