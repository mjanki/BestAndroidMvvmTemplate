package org.umbrellahq.viewmodel.model

import org.umbrellahq.util.enums.ErrorNetworkTypes

data class ErrorNetworkViewModelEntity(
        var id: Long? = null,
        var type: ErrorNetworkTypes = ErrorNetworkTypes.OTHER,
        var code: Int = 0,
        var message: String = ""
)