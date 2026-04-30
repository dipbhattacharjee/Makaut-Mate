package com.dev.makautmate.domain.repository

import android.net.Uri
import com.dev.makautmate.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(semester: String): Flow<List<Note>>
    suspend fun uploadNote(
        title: String,
        author: String,
        semester: String,
        subject: String,
        course: String = "General",
        type: String = "Notes",
        fileUri: Uri
    ): Result<Unit>
}
