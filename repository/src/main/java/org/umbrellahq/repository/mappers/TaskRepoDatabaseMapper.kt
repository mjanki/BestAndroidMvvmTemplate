package org.umbrellahq.repository.mappers

import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.repository.interfaces.RepoMapperInterface
import org.umbrellahq.repository.models.TaskRepoEntity

class TaskRepoDatabaseMapper : RepoMapperInterface<TaskRepoEntity, TaskDatabaseEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskDatabaseEntity(
            id = currentLayerEntity.id,
            uuid = currentLayerEntity.uuid,
            name = currentLayerEntity.name,
            date = currentLayerEntity.date,
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskDatabaseEntity) = TaskRepoEntity(
            id = nextLayerEntity.id,
            uuid = nextLayerEntity.uuid,
            name = nextLayerEntity.name,
            date = nextLayerEntity.date,
            status = nextLayerEntity.status
    )
}