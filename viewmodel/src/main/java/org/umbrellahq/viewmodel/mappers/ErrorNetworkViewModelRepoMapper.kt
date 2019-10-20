package org.umbrellahq.viewmodel.mappers

import org.umbrellahq.repository.model.ErrorNetworkRepoEntity
import org.umbrellahq.viewmodel.interfaces.ViewModelMapperInterface
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity

class ErrorNetworkViewModelRepoMapper : ViewModelMapperInterface<ErrorNetworkViewModelEntity, ErrorNetworkRepoEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkViewModelEntity) = ErrorNetworkRepoEntity(
            id = currentLayerEntity.id,
            type = currentLayerEntity.type,
            shouldPersist = currentLayerEntity.shouldPersist,
            code = currentLayerEntity.code,
            message = currentLayerEntity.message,
            action = currentLayerEntity.action
    )

    override fun upstream(nextLayerEntity: ErrorNetworkRepoEntity) = ErrorNetworkViewModelEntity(
            id = nextLayerEntity.id,
            type = nextLayerEntity.type,
            shouldPersist = nextLayerEntity.shouldPersist,
            code = nextLayerEntity.code,
            message = nextLayerEntity.message,
            action = nextLayerEntity.action
    )
}