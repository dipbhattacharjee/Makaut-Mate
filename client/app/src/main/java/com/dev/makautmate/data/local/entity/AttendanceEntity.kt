package com.dev.makautmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectName: String,
    val presentCount: Int = 0,
    val totalCount: Int = 0
) {
    val percentage: Float
        get() = if (totalCount > 0) (presentCount.toFloat() / totalCount.toFloat()) * 100f else 0f
}
