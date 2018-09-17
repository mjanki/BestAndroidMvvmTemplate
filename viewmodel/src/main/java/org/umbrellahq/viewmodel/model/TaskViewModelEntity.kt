package org.umbrellahq.viewmodel.model

import org.threeten.bp.OffsetDateTime
import java.util.*

data class TaskViewModelEntity(var id: Long? = null,
                               var name: String,
                               var date: OffsetDateTime = OffsetDateTime.now(),
                               var status: Int = 0)