package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.domain.model.GradeCardResponse
import com.dev.makautmate.domain.model.Notice
import com.dev.makautmate.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: StudentRepository,
) : ViewModel() {

    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val _marksSubmitStatus = MutableSharedFlow<Result<Unit>>()
    val marksSubmitStatus: SharedFlow<Result<Unit>> = _marksSubmitStatus.asSharedFlow()

    private val _gradeCard = MutableStateFlow<GradeCardResponse?>(null)
    val gradeCard: StateFlow<GradeCardResponse?> = _gradeCard.asStateFlow()

    init {
        fetchNotices()
    }

    fun fetchNotices() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getNotices()
                .catch { e -> _error.emit(e.message ?: "Unknown error") }
                .collect { 
                    _notices.value = it
                    _isLoading.value = false
                }
        }
    }

    fun submitMarks(subject: String, marks: Int, semester: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.submitMarks(subject, marks, semester)
            _marksSubmitStatus.emit(result)
            _isLoading.value = false
        }
    }

    fun trackActivity(type: String) {
        viewModelScope.launch {
            repository.trackActivity(type)
        }
    }

    fun generateGradeCard(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.generateGradeCard(studentId)
            result.onSuccess {
                _gradeCard.value = it
            }.onFailure {
                _error.emit(it.message ?: "Failed to generate grade card")
            }
            _isLoading.value = false
        }
    }
}
