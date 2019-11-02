package org.umbrellahq.repository.mappers

import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.interfaces.Mapper

class ErrorNetworkRepoNetworkMapper : Mapper<ErrorNetworkRepoEntity, ErrorNetworkEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkRepoEntity) = ErrorNetworkEntity(
            type = currentLayerEntity.type,
            shouldPersist = currentLayerEntity.shouldPersist,
            code = currentLayerEntity.code,
            message = currentLayerEntity.message,
            action = currentLayerEntity.action
    )

    override fun upstream(nextLayerEntity: ErrorNetworkEntity) = ErrorNetworkRepoEntity(
            type = nextLayerEntity.type,
            shouldPersist = nextLayerEntity.shouldPersist,
            code = nextLayerEntity.code,
            message = nextLayerEntity.message,
            action = nextLayerEntity.action
    )
}