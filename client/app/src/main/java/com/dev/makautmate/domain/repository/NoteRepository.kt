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
        fileUri: Uri
    ): Result<Unit>
}
