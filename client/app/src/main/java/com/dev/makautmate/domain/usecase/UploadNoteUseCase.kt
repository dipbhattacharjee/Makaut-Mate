package com.dev.makautmate.domain.usecase

import android.net.Uri
import com.dev.makautmate.domain.repository.NoteRepository
import javax.inject.Inject

class UploadNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        title: String,
        author: String,
        semester: String,
        subject: String,
        fileUri: Uri
    ): Result<Unit> {
        if (title.isBlank() || semester.isBlank() || subject.isBlank()) {
            return Result.failure(Exception("Required fields are missing"))
        }
        return repository.uploadNote(title, author, semester, subject, fileUri)
    }
}
