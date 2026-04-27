package com.dev.makautmate.data.remote

import com.dev.makautmate.data.model.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("printType") printType: String = "books"
    ): GoogleBooksResponse

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
    }
}
