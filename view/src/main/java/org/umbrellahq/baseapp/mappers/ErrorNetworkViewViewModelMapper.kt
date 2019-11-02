package org.umbrellahq.baseapp.mappers

import org.umbrellahq.baseapp.models.ErrorNetworkViewEntity
import org.umbrellahq.util.interfaces.Mapper
import org.umbrellahq.viewmodel.models.ErrorNetworkViewModelEntity

class ErrorNetworkViewViewModelMapper : Mapper<ErrorNetworkViewEntity, ErrorNetworkViewModelEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkViewEntity) = ErrorNetworkViewModelEntity(
            id = currentLayerEntity.id,
            type = currentLayerEntity.type,
            shouldPersist = currentLayerEntity.shouldPersist,
            code = currentLayerEntity.code,
            message = currentLayerEntity.message,
            action = currentLayerEntity.action
    )

    override fun upstream(nextLayerEntity: ErrorNetworkViewModelEntity) = ErrorNetworkViewEntity(
            id = nextLayerEntity.id,
            type = nextLayerEntity.type,
            shouldPersist = nextLayerEntity.shouldPersist,
            code = nextLayerEntity.code,
            message = nextLayerEntity.message,
            action = nextLayerEntity.action
    )
}