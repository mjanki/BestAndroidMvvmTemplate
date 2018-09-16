package org.umbrellahq.viewmodel

import java.util.*

data class TaskViewModelEntity(var id: Long? = null,
                               var name: String,
                               var date: Date = Date(),
                               var status: Int = 0)