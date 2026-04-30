package com.dev.makautmate.data.remote

import com.dev.makautmate.domain.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MakautMateApiService {
    
    @GET("api/dashboard/summary") // Updated to match /api prefix if applicable, keeping previous as base
    suspend fun getDashboardSummary(): Response<DashboardSummary>

    @GET("api/resources")
    suspend fun getResources(
        @Query("type") type: String?,
        @Query("semester") semester: String?,
        @Query("course") course: String?,
        @Query("subject") subject: String?
    ): Response<List<Note>>

    @GET("api/notices")
    suspend fun getNotices(): Response<List<Notice>>

    @POST("api/student/marks")
    suspend fun submitMarks(@Body request: MarkRequest): Response<Unit>

    @POST("api/student/activity")
    suspend fun trackActivity(@Body request: ActivityRequest): Response<Unit>

    @POST("api/student/grade-card")
    suspend fun generateGradeCard(@Body request: GradeCardRequest): Response<GradeCardResponse>

    @Multipart
    @POST("api/upload")
    suspend fun uploadResource(
        @Part("title") title: RequestBody,
        @Part("author") author: RequestBody,
        @Part("semester") semester: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("course") course: RequestBody,
        @Part("type") type: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Unit>
}
