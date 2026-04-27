package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.model.Note
import com.dev.makautmate.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(semester: String): Flow<List<Note>> {
        return repository.getNotes(semester)
    }
}
