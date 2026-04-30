package com.dev.makautmate.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.domain.model.Note
import com.dev.makautmate.domain.model.User
import com.dev.makautmate.domain.repository.AuthRepository
import com.dev.makautmate.domain.usecase.GetNotesUseCase
import com.dev.makautmate.domain.usecase.UploadNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val uploadNoteUseCase: UploadNoteUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedSemester = MutableStateFlow("All")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadStatus = MutableSharedFlow<Result<Unit>>()
    val uploadStatus: SharedFlow<Result<Unit>> = _uploadStatus.asSharedFlow()

    val userProfile: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cloudNotes: StateFlow<List<Note>> = _selectedSemester
        .flatMapLatest { semester ->
            getNotesUseCase(semester)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSemester(semester: String) {
        _selectedSemester.value = semester
    }

    fun uploadNote(
        title: String,
        author: String,
        semester: String,
        subject: String,
        course: String,
        type: String,
        fileUri: Uri
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = uploadNoteUseCase(title, author, semester, subject, course, type, fileUri)
            _uploadStatus.emit(result)
            _isLoading.value = false
        }
    }
}
