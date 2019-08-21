package org.umbrellahq.repository.mappers

import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.repository.interfaces.MapperInterface
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepoDatabaseMapper : MapperInterface<TaskRepoEntity, TaskDatabaseEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskDatabaseEntity(
            currentLayerEntity.id,
            currentLayerEntity.name,
            currentLayerEntity.date,
            currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskDatabaseEntity) = TaskRepoEntity(
            nextLayerEntity.id,
            nextLayerEntity.name,
            nextLayerEntity.date,
            nextLayerEntity.status
    )
}