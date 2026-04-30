package com.dev.makautmate.data.repository

import android.content.Context
import android.net.Uri
import com.dev.makautmate.data.remote.MakautMateApiService
import com.dev.makautmate.domain.model.Note
import com.dev.makautmate.domain.repository.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: MakautMateApiService
) : NoteRepository {
    private val collectionPath = "notes"

    override fun getNotes(semester: String): Flow<List<Note>> = flow {
        try {
            val response = apiService.getResources(
                type = null,
                semester = if (semester == "All") null else semester,
                course = null,
                subject = null
            )
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun uploadNote(
        title: String,
        author: String,
        semester: String,
        subject: String,
        course: String,
        type: String,
        fileUri: Uri
    ): Result<Unit> = try {
        val file = getFileFromUri(fileUri) ?: throw Exception("Could not get file from Uri")
        
        val requestFile = file.asRequestBody(context.contentResolver.getType(fileUri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val authorBody = author.toRequestBody("text/plain".toMediaTypeOrNull())
        val semesterBody = semester.toRequestBody("text/plain".toMediaTypeOrNull())
        val subjectBody = subject.toRequestBody("text/plain".toMediaTypeOrNull())
        val courseBody = course.toRequestBody("text/plain".toMediaTypeOrNull())
        val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = apiService.uploadResource(
            titleBody, authorBody, semesterBody, subjectBody, courseBody, typeBody, body
        )

        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Upload failed: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun getFileFromUri(uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = System.currentTimeMillis().toString() + ".pdf" // Default to pdf for now or extract extension
        val file = File(context.cacheDir, fileName)
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            return null
        }
    }
}
