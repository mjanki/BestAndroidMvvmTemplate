package org.umbrellahq.viewmodel.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.umbrellahq.repository.dataSource.Repository
import org.umbrellahq.util.extensions.execute
import org.umbrellahq.viewmodel.mappers.ErrorNetworkRepoViewModelMapper
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity

class ErrorNetworkViewModel(application: Application) : BaseViewModel(application) {
    private var errorRepository = Repository(application)
    private var errorsNetwork = MutableLiveData<List<ErrorNetworkViewModelEntity>>()

    private var errorNetworkRepoViewModelMapper = ErrorNetworkRepoViewModelMapper()

    init {
        disposables.add(
                errorRepository.getErrorsNetwork().subscribe { errorNetworkRepoEntityList ->
                    errorsNetwork.postValue(
                            errorNetworkRepoEntityList.map { errorNetworkRepoEntity ->
                                errorNetworkRepoViewModelMapper.upstream(errorNetworkRepoEntity)
                            }
                    )
                }
        )
    }

    fun getErrorsNetwork(): LiveData<List<ErrorNetworkViewModelEntity>> = errorsNetwork

    fun deleteErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity) {
        disposables.add(
                errorRepository.deleteErrorNetwork(
                        errorNetworkRepoViewModelMapper.downstream(
                                errorNetworkViewModelEntity
                        )
                ).execute()
        )
    }
}