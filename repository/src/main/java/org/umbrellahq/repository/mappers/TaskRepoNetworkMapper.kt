package org.umbrellahq.repository.mappers

import org.threeten.bp.OffsetDateTime
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.repository.interfaces.RepoMapperInterface
import org.umbrellahq.repository.models.TaskRepoEntity

class TaskRepoNetworkMapper : RepoMapperInterface<TaskRepoEntity, TaskNetworkEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskNetworkEntity(
            uuid = currentLayerEntity.uuid,
            name = currentLayerEntity.name,
            date = "${currentLayerEntity.date}",
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskNetworkEntity) = TaskRepoEntity(
            uuid = nextLayerEntity.uuid,
            name = nextLayerEntity.name,
            date = OffsetDateTime.parse(nextLayerEntity.date),
            status = nextLayerEntity.status
    )
}