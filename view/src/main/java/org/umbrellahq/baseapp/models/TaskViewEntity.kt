package org.umbrellahq.baseapp.models

import org.threeten.bp.OffsetDateTime

data class TaskViewEntity(
        var id: Long? = null,
        var name: String,
        var date: OffsetDateTime = OffsetDateTime.now(),
        var status: Int = 0
)