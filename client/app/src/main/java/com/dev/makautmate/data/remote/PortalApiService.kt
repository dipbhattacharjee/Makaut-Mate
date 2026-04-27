package com.dev.makautmate.data.remote

import com.dev.makautmate.domain.model.StudentProfile
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val roll: String,
    val pass: String
)

interface PortalApiService {
    @POST("scrape")
    suspend fun getStudentData(@Body request: LoginRequest): StudentProfile
}
