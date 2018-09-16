package org.umbrellahq.viewmodel.model

import java.util.*

data class TaskViewModelEntity(var id: Long? = null,
                               var name: String,
                               var date: Date = Date(),
                               var status: Int = 0)