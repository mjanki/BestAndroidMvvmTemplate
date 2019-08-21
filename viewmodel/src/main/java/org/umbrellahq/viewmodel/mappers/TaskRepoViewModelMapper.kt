package org.umbrellahq.viewmodel.mappers

import org.umbrellahq.repository.model.TaskRepoEntity
import org.umbrellahq.viewmodel.interfaces.ViewModelMapperInterface
import org.umbrellahq.viewmodel.model.TaskViewModelEntity

class TaskRepoViewModelMapper : ViewModelMapperInterface<TaskViewModelEntity, TaskRepoEntity> {
    override fun downstream(currentLayerEntity: TaskViewModelEntity) = TaskRepoEntity(
            currentLayerEntity.id,
            currentLayerEntity.name,
            currentLayerEntity.date,
            currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskRepoEntity) = TaskViewModelEntity(
            nextLayerEntity.id,
            nextLayerEntity.name,
            nextLayerEntity.date,
            nextLayerEntity.status
    )
}