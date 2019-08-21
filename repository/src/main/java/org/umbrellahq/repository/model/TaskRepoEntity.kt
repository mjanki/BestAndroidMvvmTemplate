package org.umbrellahq.repository.model

import org.threeten.bp.OffsetDateTime
import org.umbrellahq.database.model.TaskDatabaseEntity

data class TaskRepoEntity(
        var id: Long? = null,
        var name: String,
        var date: OffsetDateTime = OffsetDateTime.now(),
        var status: Int = 0) {
    
    constructor(taskDatabaseEntity: TaskDatabaseEntity) : this(
            taskDatabaseEntity.id,
            taskDatabaseEntity.name,
            taskDatabaseEntity.date,
            taskDatabaseEntity.status
    )

    fun mapToDatabase() = TaskDatabaseEntity(
            id,
            name,
            date,
            status
    )
}