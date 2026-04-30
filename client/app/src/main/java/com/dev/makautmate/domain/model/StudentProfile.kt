package com.dev.makautmate.domain.model

data class StudentProfile(
    val name: String,
    val roll: String,
    val cgpa: Double,
    val sgpa: Map<String, Double> = emptyMap(),
    val caMarks: Map<String, Double>,
    val attendance: String
)
