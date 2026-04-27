package com.dev.makautmate.data.model

data class Note(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val semester: String = "",
    val subject: String = "",
    val fileUrl: String = "",
    val thumbnailUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
