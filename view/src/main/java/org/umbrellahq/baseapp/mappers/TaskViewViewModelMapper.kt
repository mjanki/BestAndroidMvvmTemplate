package org.umbrellahq.baseapp.mappers

import org.umbrellahq.baseapp.interfaces.ViewMapperInterface
import org.umbrellahq.baseapp.models.TaskViewEntity
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskViewViewModelMapper : ViewMapperInterface<TaskViewEntity, TaskViewModelEntity> {
    override fun downstream(currentLayerEntity: TaskViewEntity) = TaskViewModelEntity(
            id = currentLayerEntity.id,
            name = currentLayerEntity.name,
            date = currentLayerEntity.date,
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskViewModelEntity) = TaskViewEntity(
            id = nextLayerEntity.id,
            name = nextLayerEntity.name,
            date = nextLayerEntity.date,
            status = nextLayerEntity.status
    )
}