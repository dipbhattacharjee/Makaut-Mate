package com.dev.makautmate.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.dev.makautmate.domain.model.Note
import com.dev.makautmate.domain.repository.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NoteRepository {
    private val collectionPath = "notes"

    override fun getNotes(semester: String): Flow<List<Note>> = flow {
        try {
            val query = if (semester == "All") {
                firestore.collection(collectionPath)
            } else {
                firestore.collection(collectionPath).whereEqualTo("semester", semester)
            }
            
            val snapshot = query.get().await()
            val notes = snapshot.documents.mapNotNull { doc ->
                Note(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    author = doc.getString("author") ?: "",
                    semester = doc.getString("semester") ?: "",
                    subject = doc.getString("subject") ?: "",
                    fileUrl = doc.getString("fileUrl") ?: "",
                    timestamp = doc.getTimestamp("createdAt")?.seconds?.times(1000) ?: System.currentTimeMillis()
                )
            }
            emit(notes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun uploadNote(
        title: String,
        author: String,
        semester: String,
        subject: String,
        fileUri: Uri
    ): Result<Unit> = try {
        // 1. Upload to Cloudinary
        val cloudinaryUrl = suspendCancellableCoroutine<String> { continuation ->
            MediaManager.get().upload(fileUri)
                .option("resource_type", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        val url = resultData["url"] as? String
                        
                        var finalUrl = secureUrl ?: url ?: ""
                        
                        // Force HTTPS if it's a Cloudinary URL and starts with http://
                        if (finalUrl.startsWith("http://res.cloudinary.com/")) {
                            finalUrl = finalUrl.replaceFirst("http://", "https://")
                        }
                        
                        continuation.resume(finalUrl)
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(Exception(error.description))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
        
        // 2. Save to Firestore
        val noteData = mapOf(
            "title" to title,
            "author" to author,
            "semester" to semester,
            "subject" to subject,
            "fileUrl" to cloudinaryUrl,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        
        firestore.collection(collectionPath).add(noteData).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
