package com.tradinganalytics.core.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {

    private const val SALT_LENGTH = 16
    private const val HASH_ALGORITHM = "SHA-256"
    private const val ITERATIONS = 10000
    private const val SEPARATOR = ":"

    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hashWithIterations(password, salt, ITERATIONS)
        return "${ITERATIONS}$SEPARATOR${Base64.getEncoder().encodeToString(salt)}$SEPARATOR${Base64.getEncoder().encodeToString(hash)}"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(SEPARATOR)
            if (parts.size != 3) return false

            val iterations = parts[0].toIntOrNull() ?: return false
            val salt = Base64.getDecoder().decode(parts[1])
            val expectedHash = Base64.getDecoder().decode(parts[2])

            val computedHash = hashWithIterations(password, salt, iterations)
            MessageDigest.isEqual(expectedHash, computedHash)
        } catch (e: Exception) {
            false
        }
    }

    fun hashPasswordSimple(password: String): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    fun verifyPasswordSimple(password: String, storedHash: String): Boolean {
        return hashPasswordSimple(password) == storedHash
    }

    fun generateToken(length: Int = 32): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    private fun hashWithIterations(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        digest.update(salt)
        var hash = digest.digest(password.toByteArray(Charsets.UTF_8))

        repeat(iterations - 1) {
            digest.reset()
            hash = digest.digest(hash)
        }

        return hash
    }

    fun isHashFormatValid(hash: String): Boolean {
        return try {
            val parts = hash.split(SEPARATOR)
            if (parts.size != 3) return false
            parts[0].toIntOrNull() ?: return false
            Base64.getDecoder().decode(parts[1])
            Base64.getDecoder().decode(parts[2])
            true
        } catch (e: Exception) {
            false
        }
    }
}
