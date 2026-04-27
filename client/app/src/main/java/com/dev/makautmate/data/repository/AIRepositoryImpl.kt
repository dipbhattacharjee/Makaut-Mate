package com.dev.makautmate.data.repository

import com.dev.makautmate.BuildConfig
import com.dev.makautmate.domain.repository.AIRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import com.dev.makautmate.ui.screens.ChatMessage

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AIRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AIRepository {

    private val client = OkHttpClient()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val FREE_DAILY_LIMIT = 5
    private val FREE_MONTHLY_LIMIT = 50

    override suspend fun getChatResponse(messages: List<ChatMessage>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonMessages = JSONArray()
            // Add system prompt for context
            jsonMessages.put(JSONObject().apply {
                put("role", "system")
                put("content", "You are MAKAUT Mate AI, a specialized assistant for students of MAKAUT (Maulana Abul Kalam Azad University of Technology). " +
                        "Provide detailed information about MAKAUT syllabus (BCA), previous year questions (PYQs), exam schedules, and academic guidance. " +
                        "Always be helpful, concise, and professional.")
            })
            messages.forEach { msg ->
                jsonMessages.put(JSONObject().apply {
                    put("role", if (msg.isUser) "user" else "assistant")
                    put("content", msg.text)
                })
            }

            val requestBody = JSONObject().apply {
                put("model", "google/gemini-2.0-flash-exp:free")
                put("messages", jsonMessages)
            }

            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .post(requestBody.toString().toRequestBody(mediaType))
                .addHeader("Authorization", "Bearer ${BuildConfig.OPEN_ROUTER_API_KEY}")
                .addHeader("HTTP-Referer", "https://makautmate.dev") // Required by OpenRouter
                .addHeader("X-Title", "MAKAUT Mate")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Unexpected code $response")
                
                val responseData = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseData)
                val aiMessage = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                
                Result.success(aiMessage)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkRateLimit(userId: String): Result<Boolean> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val isPremium = userDoc.getBoolean("isPremium") ?: false
            
            if (isPremium) return Result.success(true)

            val aiUsageToday = userDoc.getLong("aiUsageToday")?.toInt() ?: 0
            val aiUsageThisMonth = userDoc.getLong("aiUsageThisMonth")?.toInt() ?: 0
            val lastTimestamp = userDoc.getLong("lastAiUsageTimestamp") ?: 0L
            
            val now = Calendar.getInstance()
            val last = Calendar.getInstance().apply { timeInMillis = lastTimestamp }

            val dailyUsage = if (now.get(Calendar.DAY_OF_YEAR) != last.get(Calendar.DAY_OF_YEAR) || 
                now.get(Calendar.YEAR) != last.get(Calendar.YEAR)) 0 else aiUsageToday

            val monthlyUsage = if (now.get(Calendar.MONTH) != last.get(Calendar.MONTH) || 
                now.get(Calendar.YEAR) != last.get(Calendar.YEAR)) 0 else aiUsageThisMonth

            if (dailyUsage >= FREE_DAILY_LIMIT || monthlyUsage >= FREE_MONTHLY_LIMIT) {
                Result.success(false)
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun incrementUsage(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(
                "aiUsageToday", FieldValue.increment(1),
                "aiUsageThisMonth", FieldValue.increment(1),
                "lastAiUsageTimestamp", System.currentTimeMillis()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
