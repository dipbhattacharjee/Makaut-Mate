package com.dev.makautmate.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val college: String = "",
    val department: String = "",
    val semester: String = "",
    val profilePicUrl: String = "",
    val role: String = "student",
    val isPremium: Boolean = false,
    val aiUsageToday: Int = 0,
    val aiUsageThisMonth: Int = 0,
    val lastAiUsageTimestamp: Long = 0
)
