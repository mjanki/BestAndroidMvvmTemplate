package org.umbrellahq.viewmodel.models

import org.threeten.bp.OffsetDateTime

data class TaskViewModelEntity(
        var id: Long? = null,
        var name: String,
        var date: OffsetDateTime = OffsetDateTime.now(),
        var status: Int = 0
)