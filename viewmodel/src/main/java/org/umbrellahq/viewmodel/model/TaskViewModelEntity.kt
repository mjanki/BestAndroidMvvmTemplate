package org.umbrellahq.viewmodel.model

import org.threeten.bp.OffsetDateTime
import org.umbrellahq.repository.model.TaskRepoEntity

data class TaskViewModelEntity(var id: Long? = null,
                               var name: String,
                               var date: OffsetDateTime = OffsetDateTime.now(),
                               var status: Int = 0) {
    constructor(): this(null, "")

    constructor(taskRepoEntity: TaskRepoEntity) : this(
            taskRepoEntity.id,
            taskRepoEntity.name,
            taskRepoEntity.date,
            taskRepoEntity.status
    )

    fun mapToRepo(): TaskRepoEntity {
        return TaskRepoEntity(
                id,
                name,
                date,
                status
        )
    }
}