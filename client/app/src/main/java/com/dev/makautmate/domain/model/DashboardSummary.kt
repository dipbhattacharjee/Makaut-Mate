package com.dev.makautmate.domain.model

data class DashboardSummary(
    val greeting: String,
    val notifications: List<String>,
    val cgpa: Double,
    val currentSemester: Int
)
