package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.data.model.BookItem
import com.dev.makautmate.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _books = MutableStateFlow<List<BookDisplayItem>>(emptyList())
    val books: StateFlow<List<BookDisplayItem>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // Initial search for Computer Science books
        performSearch("Computer Science Programming Database")

        @OptIn(FlowPreview::class)
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.length > 2) {
                    performSearch(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchBooks(query: String) {
        _searchQuery.value = query
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val results = repository.searchBooksWithOpenLibrary(query)
            _books.value = results
            _isLoading.value = false
        }
    }
}

data class BookDisplayItem(
    val title: String,
    val author: String,
    val imageUrl: String?,
    val readUrl: String?,
    val isFree: Boolean = true
)
