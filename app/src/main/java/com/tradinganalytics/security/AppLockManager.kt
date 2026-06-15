package com.tradinganalytics.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tradinganalytics.core.utils.PasswordHasher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "app_lock_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_PIN_HASH = "app_lock_pin_hash"
        private const val KEY_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_MAX_ATTEMPTS = "app_lock_max_attempts"
        private const val KEY_LOCKOUT_UNTIL = "app_lock_lockout_until"
        private const val KEY_CURRENT_ATTEMPTS = "app_lock_current_attempts"
        private const val DEFAULT_MAX_ATTEMPTS = 5
        private const val LOCKOUT_DURATION_MS = 30_000L
    }

    fun setPin(pin: String) {
        val hash = PasswordHasher.hashPassword(pin)
        securePrefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putBoolean(KEY_LOCK_ENABLED, true)
            .putInt(KEY_CURRENT_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        if (isLockedOut()) return false

        val storedHash = securePrefs.getString(KEY_PIN_HASH, null) ?: return false

        val valid = PasswordHasher.verifyPassword(pin, storedHash)
        if (valid) {
            securePrefs.edit().putInt(KEY_CURRENT_ATTEMPTS, 0).apply()
        } else {
            val attempts = securePrefs.getInt(KEY_CURRENT_ATTEMPTS, 0) + 1
            securePrefs.edit().putInt(KEY_CURRENT_ATTEMPTS, attempts).apply()
            if (attempts >= getMaxAttempts()) {
                val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS
                securePrefs.edit().putLong(KEY_LOCKOUT_UNTIL, lockoutUntil).apply()
            }
        }
        return valid
    }

    fun isLockEnabled(): Boolean {
        return securePrefs.getBoolean(KEY_LOCK_ENABLED, false)
    }

    fun disableLock() {
        securePrefs.edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .putInt(KEY_CURRENT_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)
            .apply()
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (!verifyPin(oldPin)) return false
        setPin(newPin)
        return true
    }

    fun isLockedOut(): Boolean {
        val lockoutUntil = securePrefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        if (lockoutUntil == 0L) return false
        if (System.currentTimeMillis() >= lockoutUntil) {
            securePrefs.edit()
                .putInt(KEY_CURRENT_ATTEMPTS, 0)
                .putLong(KEY_LOCKOUT_UNTIL, 0L)
                .apply()
            return false
        }
        return true
    }

    fun getRemainingLockoutTime(): Long {
        val lockoutUntil = securePrefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        return (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun getRemainingAttempts(): Int {
        if (isLockedOut()) return 0
        return getMaxAttempts() - securePrefs.getInt(KEY_CURRENT_ATTEMPTS, 0)
    }

    private fun getMaxAttempts(): Int {
        return securePrefs.getInt(KEY_MAX_ATTEMPTS, DEFAULT_MAX_ATTEMPTS)
    }

    fun setMaxAttempts(max: Int) {
        securePrefs.edit().putInt(KEY_MAX_ATTEMPTS, max.coerceIn(1, 10)).apply()
    }

    fun resetAttempts() {
        securePrefs.edit()
            .putInt(KEY_CURRENT_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)
            .apply()
    }
}
