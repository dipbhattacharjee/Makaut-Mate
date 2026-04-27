package com.dev.makautmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.makautmate.domain.model.StudentProfile

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val roll: String,
    val name: String,
    val cgpa: Double,
    val caMarks: Map<String, Double>,
    val attendance: String
)

fun StudentProfileEntity.toDomain() = StudentProfile(
    name = name,
    roll = roll,
    cgpa = cgpa,
    caMarks = caMarks,
    attendance = attendance
)

fun StudentProfile.toEntity() = StudentProfileEntity(
    roll = roll,
    name = name,
    cgpa = cgpa,
    caMarks = caMarks,
    attendance = attendance
)
