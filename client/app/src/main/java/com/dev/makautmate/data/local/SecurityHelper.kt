package com.dev.makautmate.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityHelper @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "makaut_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(roll: String, pass: String) {
        sharedPreferences.edit()
            .putString("roll", roll)
            .putString("pass", pass)
            .apply()
    }

    fun getRoll(): String? = sharedPreferences.getString("roll", null)
    fun getPass(): String? = sharedPreferences.getString("pass", null)

    fun clearCredentials() {
        sharedPreferences.edit().clear().apply()
    }
}
