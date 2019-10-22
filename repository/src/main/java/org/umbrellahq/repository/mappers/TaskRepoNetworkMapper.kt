package org.umbrellahq.repository.mappers

import org.threeten.bp.OffsetDateTime
import org.umbrellahq.network.model.TaskNetworkEntity
import org.umbrellahq.repository.interfaces.RepoMapperInterface
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepoNetworkMapper : RepoMapperInterface<TaskRepoEntity, TaskNetworkEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskNetworkEntity(
            uuid = "${currentLayerEntity.id}",
            name = currentLayerEntity.name,
            date = "${currentLayerEntity.date}",
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskNetworkEntity) = TaskRepoEntity(
            id = nextLayerEntity.uuid.toLong(),
            name = nextLayerEntity.name,
            date = OffsetDateTime.parse(nextLayerEntity.date),
            status = nextLayerEntity.status
    )
}