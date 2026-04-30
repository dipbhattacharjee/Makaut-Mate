package com.dev.makautmate.data.local.dao

import androidx.room.*
import com.dev.makautmate.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM downloaded_books ORDER BY downloadTimestamp DESC")
    fun getAllDownloadedBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM downloaded_books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?
}
