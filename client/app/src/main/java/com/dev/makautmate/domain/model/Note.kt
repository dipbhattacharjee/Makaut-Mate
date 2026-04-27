package com.dev.makautmate.domain.model

data class Note(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val semester: String = "",
    val subject: String = "",
    val fileUrl: String = "",
    val type: String = "Notes",
    val status: String = "pending",
    val thumbnailUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
