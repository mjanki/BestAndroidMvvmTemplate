package org.umbrellahq.network.models

import org.umbrellahq.util.enums.ErrorNetworkTypes

data class ErrorNetworkEntity (
        var type: ErrorNetworkTypes = ErrorNetworkTypes.OTHER,
        var shouldPersist: Boolean = false,
        var code: Int = 0,
        var message: String = "",
        var action: String = ""
)