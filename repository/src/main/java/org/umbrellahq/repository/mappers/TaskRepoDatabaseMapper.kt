package org.umbrellahq.repository.mappers

import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.repository.interfaces.RepoMapperInterface
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepoDatabaseMapper : RepoMapperInterface<TaskRepoEntity, TaskDatabaseEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskDatabaseEntity(
            id = currentLayerEntity.id,
            name = currentLayerEntity.name,
            date = currentLayerEntity.date,
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskDatabaseEntity) = TaskRepoEntity(
            id = nextLayerEntity.id,
            name = nextLayerEntity.name,
            date = nextLayerEntity.date,
            status = nextLayerEntity.status
    )
}