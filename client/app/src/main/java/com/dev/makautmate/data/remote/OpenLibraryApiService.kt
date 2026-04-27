package com.dev.makautmate.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}

data class OpenLibrarySearchResponse(
    val docs: List<OpenLibraryDoc>
)

data class OpenLibraryDoc(
    val key: String,
    val title: String,
    val author_name: List<String>? = null,
    val cover_i: Int? = null,
    val first_publish_year: Int? = null,
    val ia: List<String>? = null, // Internet Archive IDs for reading
    val seed: List<String>? = null
)
