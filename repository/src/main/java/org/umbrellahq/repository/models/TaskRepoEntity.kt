package org.umbrellahq.repository.models

import org.threeten.bp.OffsetDateTime
import java.util.*

data class TaskRepoEntity(
        var id: Long? = null,
        var uuid: String = UUID.randomUUID().toString(),
        var name: String,
        var date: OffsetDateTime = OffsetDateTime.now(),
        var status: Int = 0
)