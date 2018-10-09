package org.umbrellahq.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "tasks")
data class TaskEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "date") var date: OffsetDateTime = OffsetDateTime.now(),
                      @ColumnInfo(name = "status") var status: Int = 0) {
    constructor(): this(null, "")
}