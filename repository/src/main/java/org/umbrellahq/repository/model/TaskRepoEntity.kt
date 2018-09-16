package org.umbrellahq.repository.model

import org.threeten.bp.OffsetDateTime

data class TaskRepoEntity(var id: Long? = null,
                          var name: String,
                          var date: OffsetDateTime = OffsetDateTime.now(),
                          var status: Int = 0)