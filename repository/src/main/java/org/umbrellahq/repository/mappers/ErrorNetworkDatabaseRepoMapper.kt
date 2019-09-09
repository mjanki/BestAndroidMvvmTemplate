package org.umbrellahq.repository.mappers

import org.umbrellahq.database.model.ErrorNetworkDatabaseEntity
import org.umbrellahq.repository.interfaces.RepoMapperInterface
import org.umbrellahq.repository.model.ErrorNetworkRepoEntity

class ErrorNetworkDatabaseRepoMapper : RepoMapperInterface<ErrorNetworkRepoEntity, ErrorNetworkDatabaseEntity> {
    override fun downstream(currentLayerEntity: ErrorNetworkRepoEntity) = ErrorNetworkDatabaseEntity(
            currentLayerEntity.id,
            currentLayerEntity.type,
            currentLayerEntity.code,
            currentLayerEntity.message
    )

    override fun upstream(nextLayerEntity: ErrorNetworkDatabaseEntity) = ErrorNetworkRepoEntity(
            nextLayerEntity.id,
            nextLayerEntity.type,
            nextLayerEntity.code,
            nextLayerEntity.message
    )
}