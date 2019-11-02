package org.umbrellahq.repository.mappers

import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.interfaces.Mapper

class ErrorNetworkRepoDatabaseMapper : Mapper<ErrorNetworkRepoEntity, ErrorNetworkDatabaseEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkRepoEntity) = ErrorNetworkDatabaseEntity(
            id = currentLayerEntity.id,
            type = currentLayerEntity.type,
            shouldPersist = currentLayerEntity.shouldPersist,
            code = currentLayerEntity.code,
            message = currentLayerEntity.message,
            action = currentLayerEntity.action
    )

    override fun upstream(nextLayerEntity: ErrorNetworkDatabaseEntity) = ErrorNetworkRepoEntity(
            id = nextLayerEntity.id,
            type = nextLayerEntity.type,
            shouldPersist = nextLayerEntity.shouldPersist,
            code = nextLayerEntity.code,
            message = nextLayerEntity.message,
            action = nextLayerEntity.action
    )
}