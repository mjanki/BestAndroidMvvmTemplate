package org.umbrellahq.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "name") var name: String)