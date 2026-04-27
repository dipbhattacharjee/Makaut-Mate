package com.dev.makautmate.data.repository

import android.util.Log
import com.dev.makautmate.data.model.BookItem
import com.dev.makautmate.data.remote.BookApiService
import com.dev.makautmate.data.remote.OpenLibraryApiService
import com.dev.makautmate.ui.viewmodel.BookDisplayItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val apiService: BookApiService,
    private val openLibraryApi: OpenLibraryApiService
) {
    suspend fun searchBooks(query: String): List<BookItem> {
        return try {
            val response = apiService.searchBooks(query)
            response.items ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchBooksWithOpenLibrary(query: String): List<BookDisplayItem> {
        return try {
            val response = openLibraryApi.searchBooks(query)
            response.docs.map { doc ->
                val coverUrl = if (doc.cover_i != null) {
                    "https://covers.openlibrary.org/b/id/${doc.cover_i}-L.jpg"
                } else {
                    null
                }
                
                // Construct a read URL if it's available in Internet Archive
                val readUrl = if (!doc.ia.isNullOrEmpty()) {
                    "https://archive.org/details/${doc.ia.first()}"
                } else {
                    "https://openlibrary.org${doc.key}"
                }

                BookDisplayItem(
                    title = doc.title,
                    author = doc.author_name?.joinToString(", ") ?: "Unknown Author",
                    imageUrl = coverUrl,
                    readUrl = readUrl,
                    isFree = true
                )
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "OpenLibrary search failed", e)
            emptyList()
        }
    }
}
