package org.umbrellahq.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.umbrellahq.util.enums.ErrorNetworkTypes

@Entity(tableName = "network_errors")
data class ErrorNetworkDatabaseEntity(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "type") var type: ErrorNetworkTypes = ErrorNetworkTypes.OTHER,
        @ColumnInfo(name = "shouldPersist") var shouldPersist: Boolean = false,
        @ColumnInfo(name = "code") var code: Int = 0,
        @ColumnInfo(name = "message") var message: String = "",
        @ColumnInfo(name = "action") var action: String = ""
)