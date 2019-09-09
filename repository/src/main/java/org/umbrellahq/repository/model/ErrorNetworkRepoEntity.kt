package org.umbrellahq.repository.model

import org.umbrellahq.util.enums.ErrorNetworkTypes

data class ErrorNetworkRepoEntity(
        var id: Long? = null,
        var type: ErrorNetworkTypes = ErrorNetworkTypes.OTHER,
        var code: Int = 0,
        var message: String = ""
)