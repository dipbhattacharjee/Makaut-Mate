package com.dev.makautmate.data.repository

import android.content.Context
import com.dev.makautmate.data.local.dao.BookDao
import com.dev.makautmate.data.local.entity.BookEntity
import com.dev.makautmate.ui.viewmodel.BookDisplayItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDownloadRepository @Inject constructor(
    private val bookDao: BookDao,
    @ApplicationContext private val context: Context
) {
    val downloadedBooks: Flow<List<BookEntity>> = bookDao.getAllDownloadedBooks()

    suspend fun downloadBook(book: BookDisplayItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = book.readUrl ?: return@withContext Result.failure(Exception("No URL available"))
            
            // For simplicity, we'll only support downloading PDFs or clear web links.
            // In a real app, you might need more complex logic for different types of "read" links.
            
            val fileName = "${book.title.replace(" ", "_")}.pdf" // Assuming PDF for now
            val file = File(context.filesDir, "downloads/$fileName")
            file.parentFile?.let {
                if (!it.exists()) it.mkdirs()
            }

            URL(url).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val entity = BookEntity(
                id = book.title + book.author, // Simple ID for now
                title = book.title,
                author = book.author,
                imageUrl = book.imageUrl,
                localFilePath = file.absolutePath
            )
            bookDao.insertBook(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBook(book: BookEntity) {
        withContext(Dispatchers.IO) {
            val file = File(book.localFilePath)
            if (file.exists()) file.delete()
            bookDao.deleteBook(book)
        }
    }
    
    suspend fun isBookDownloaded(bookId: String): Boolean {
        return bookDao.getBookById(bookId) != null
    }
}
