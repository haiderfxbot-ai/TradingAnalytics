package com.tradinganalytics.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tradinganalytics.core.constants.AppConstants

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        AppConstants.Preferences.ENCRYPTED_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val regularPrefs: SharedPreferences =
        context.getSharedPreferences(AppConstants.Preferences.NAME, Context.MODE_PRIVATE)

    fun saveSession(username: String, role: String, userId: Long = -1L) {
        val token = PasswordHasher.generateToken()
        val timestamp = System.currentTimeMillis()

        securePrefs.edit()
            .putString(AppConstants.Session.PREF_KEY_USERNAME, username)
            .putString(AppConstants.Session.PREF_KEY_USER_ROLE, role)
            .putString(AppConstants.Session.PREF_KEY_SESSION_TOKEN, token)
            .putLong(AppConstants.Session.PREF_KEY_LOGIN_TIMESTAMP, timestamp)
            .putLong(AppConstants.Session.PREF_KEY_USER_ID, userId)
            .putBoolean(AppConstants.Session.PREF_KEY_IS_LOGGED_IN, true)
            .apply()

        if (isRememberLoginEnabled()) {
            regularPrefs.edit()
                .putBoolean(AppConstants.Session.PREF_KEY_REMEMBER_LOGIN, true)
                .apply()
        }
    }

    fun getUsername(): String? {
        return securePrefs.getString(AppConstants.Session.PREF_KEY_USERNAME, null)
    }

    fun getUserRole(): String? {
        return securePrefs.getString(AppConstants.Session.PREF_KEY_USER_ROLE, null)
    }

    fun getUserId(): Long {
        return securePrefs.getLong(AppConstants.Session.PREF_KEY_USER_ID, -1L)
    }

    fun getSessionToken(): String? {
        return securePrefs.getString(AppConstants.Session.PREF_KEY_SESSION_TOKEN, null)
    }

    fun getLoginTimestamp(): Long {
        return securePrefs.getLong(AppConstants.Session.PREF_KEY_LOGIN_TIMESTAMP, 0L)
    }

    fun isLoggedIn(): Boolean {
        return securePrefs.getBoolean(AppConstants.Session.PREF_KEY_IS_LOGGED_IN, false)
    }

    fun isSessionValid(): Boolean {
        if (!isLoggedIn()) return false

        val timestamp = getLoginTimestamp()
        if (timestamp == 0L) return false

        val elapsed = System.currentTimeMillis() - timestamp
        val timeout = if (isRememberLoginEnabled()) {
            AppConstants.Session.EXTENDED_TIMEOUT_DURATION_MS
        } else {
            AppConstants.Session.TIMEOUT_DURATION_MS
        }

        return elapsed < timeout
    }

    fun clearSession() {
        securePrefs.edit()
            .remove(AppConstants.Session.PREF_KEY_USERNAME)
            .remove(AppConstants.Session.PREF_KEY_USER_ROLE)
            .remove(AppConstants.Session.PREF_KEY_SESSION_TOKEN)
            .remove(AppConstants.Session.PREF_KEY_LOGIN_TIMESTAMP)
            .remove(AppConstants.Session.PREF_KEY_USER_ID)
            .putBoolean(AppConstants.Session.PREF_KEY_IS_LOGGED_IN, false)
            .apply()
    }

    fun setRememberLogin(enabled: Boolean) {
        regularPrefs.edit()
            .putBoolean(AppConstants.Session.PREF_KEY_REMEMBER_LOGIN, enabled)
            .apply()

        if (!enabled) {
            val username = getUsername()
            val role = getUserRole()
            val userId = getUserId()

            if (username != null && role != null) {
                val token = PasswordHasher.generateToken()
                val timestamp = System.currentTimeMillis()
                securePrefs.edit()
                    .putString(AppConstants.Session.PREF_KEY_USERNAME, username)
                    .putString(AppConstants.Session.PREF_KEY_USER_ROLE, role)
                    .putString(AppConstants.Session.PREF_KEY_SESSION_TOKEN, token)
                    .putLong(AppConstants.Session.PREF_KEY_LOGIN_TIMESTAMP, timestamp)
                    .putLong(AppConstants.Session.PREF_KEY_USER_ID, userId)
                    .putBoolean(AppConstants.Session.PREF_KEY_IS_LOGGED_IN, true)
                    .apply()
            }
        }
    }

    fun isRememberLoginEnabled(): Boolean {
        return regularPrefs.getBoolean(AppConstants.Session.PREF_KEY_REMEMBER_LOGIN, false)
    }

    fun canAutoLogin(): Boolean {
        return isRememberLoginEnabled() && isSessionValid()
    }

    fun isAdmin(): Boolean {
        return getUserRole() == AppConstants.UserRoles.ADMIN
    }

    fun updateSessionTimestamp() {
        securePrefs.edit()
            .putLong(AppConstants.Session.PREF_KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun updateUsername(username: String) {
        securePrefs.edit()
            .putString(AppConstants.Session.PREF_KEY_USERNAME, username)
            .apply()
    }

    fun updateUserRole(role: String) {
        securePrefs.edit()
            .putString(AppConstants.Session.PREF_KEY_USER_ROLE, role)
            .apply()
    }

    fun getSessionDuration(): Long {
        val timestamp = getLoginTimestamp()
        if (timestamp == 0L) return 0L
        return System.currentTimeMillis() - timestamp
    }

    fun getRemainingSessionTime(): Long {
        val timestamp = getLoginTimestamp()
        if (timestamp == 0L) return 0L

        val timeout = if (isRememberLoginEnabled()) {
            AppConstants.Session.EXTENDED_TIMEOUT_DURATION_MS
        } else {
            AppConstants.Session.TIMEOUT_DURATION_MS
        }

        val elapsed = System.currentTimeMillis() - timestamp
        return (timeout - elapsed).coerceAtLeast(0L)
    }
}
