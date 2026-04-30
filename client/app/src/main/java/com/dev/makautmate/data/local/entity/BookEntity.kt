package com.dev.makautmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_books")
data class BookEntity(
    @PrimaryKey
    val id: String, // Open Library key or similar unique ID
    val title: String,
    val author: String,
    val imageUrl: String?,
    val localFilePath: String,
    val downloadTimestamp: Long = System.currentTimeMillis()
)
