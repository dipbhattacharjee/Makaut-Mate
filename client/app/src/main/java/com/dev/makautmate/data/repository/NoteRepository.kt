package com.dev.makautmate.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.dev.makautmate.data.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class NoteRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collectionPath = "notes"
    private val storagePath = "notes/"

    fun getNotes(semester: String): Flow<List<Note>> = flow {
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
                    fileUrl = doc.getString("fileUrl") ?: ""
                )
            }
            emit(notes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun uploadNote(
        title: String,
        author: String,
        semester: String,
        subject: String,
        fileUri: Uri,
        type: String = "Notes"
    ): Result<Unit> = try {
        // 1. Upload to Cloudinary
        val cloudinaryUrl = suspendCancellableCoroutine<String> { continuation ->
            MediaManager.get().upload(fileUri)
                .option("resource_type", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: resultData["url"] as? String ?: ""
                        continuation.resume(url)
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
            "type" to type,
            "status" to "pending",
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        
        firestore.collection(collectionPath).add(noteData).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
