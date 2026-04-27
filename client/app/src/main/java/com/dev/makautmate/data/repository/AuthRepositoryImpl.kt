package com.dev.makautmate.data.repository

import com.dev.makautmate.data.local.SessionManager
import com.dev.makautmate.domain.model.User
import com.dev.makautmate.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val usersCollection = "users"

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection(usersCollection).document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val user = User(
                                uid = firebaseUser.uid,
                                fullName = snapshot.getString("fullName") ?: "",
                                email = snapshot.getString("email") ?: "",
                                phoneNumber = snapshot.getString("phoneNumber") ?: "",
                                college = snapshot.getString("college") ?: "",
                                department = snapshot.getString("department") ?: "",
                                semester = snapshot.getString("semester") ?: "",
                                profilePicUrl = snapshot.getString("profilePicUrl") ?: "",
                                isPremium = snapshot.getBoolean("isPremium") ?: false,
                                aiUsageToday = snapshot.getLong("aiUsageToday")?.toInt() ?: 0,
                                aiUsageThisMonth = snapshot.getLong("aiUsageThisMonth")?.toInt() ?: 0,
                                lastAiUsageTimestamp = snapshot.getLong("lastAiUsageTimestamp") ?: 0L
                            )
                            trySend(user)
                        } else {
                            trySend(User(uid = firebaseUser.uid, email = firebaseUser.email ?: ""))
                        }
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { sessionManager.saveUserId(it.uid) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(user: User, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")
            
            val profileData = mapOf(
                "fullName" to user.fullName,
                "email" to user.email,
                "phoneNumber" to user.phoneNumber,
                "college" to user.college,
                "department" to user.department,
                "semester" to user.semester
            )
            
            firestore.collection(usersCollection).document(uid).set(profileData).await()
            sessionManager.saveUserId(uid)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        sessionManager.clearSession()
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                val document = firestore.collection(usersCollection).document(user.uid).get().await()
                if (!document.exists()) {
                    val profileData = mapOf(
                        "fullName" to (user.displayName ?: ""),
                        "email" to (user.email ?: ""),
                        "phoneNumber" to (user.phoneNumber ?: ""),
                        "profilePicUrl" to (user.photoUrl?.toString() ?: ""),
                        "college" to "",
                        "department" to "",
                        "semester" to ""
                    )
                    firestore.collection(usersCollection).document(user.uid).set(profileData).await()
                }
                sessionManager.saveUserId(user.uid)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Google Sign-In failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null || sessionManager.isGuestMode()
    }

    override fun isGuestMode(): Boolean = sessionManager.isGuestMode()

    override fun setGuestMode(isGuest: Boolean) {
        sessionManager.setGuestMode(isGuest)
    }
}
