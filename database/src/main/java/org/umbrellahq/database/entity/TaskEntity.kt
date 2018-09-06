package org.umbrellahq.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class TaskEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "date") var date: Date = Date(),
                      @ColumnInfo(name = "status") var status: Int = 0)