package org.umbrellahq.viewmodel.mappers

import org.umbrellahq.repository.model.ErrorNetworkRepoEntity
import org.umbrellahq.viewmodel.interfaces.ViewModelMapperInterface
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity

class ErrorNetworkRepoViewModelMapper : ViewModelMapperInterface<ErrorNetworkViewModelEntity, ErrorNetworkRepoEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkViewModelEntity) = ErrorNetworkRepoEntity(
            currentLayerEntity.id,
            currentLayerEntity.type,
            currentLayerEntity.code,
            currentLayerEntity.message
    )

    override fun upstream(nextLayerEntity: ErrorNetworkRepoEntity) = ErrorNetworkViewModelEntity(
            nextLayerEntity.id,
            nextLayerEntity.type,
            nextLayerEntity.code,
            nextLayerEntity.message
    )
}