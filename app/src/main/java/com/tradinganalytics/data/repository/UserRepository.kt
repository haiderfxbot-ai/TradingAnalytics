package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.BackupDao
import com.tradinganalytics.data.database.dao.BalanceDao
import com.tradinganalytics.data.database.dao.EntryDao
import com.tradinganalytics.data.database.dao.GoalDao
import com.tradinganalytics.data.database.dao.LoginHistoryDao
import com.tradinganalytics.data.database.dao.NoteDao
import com.tradinganalytics.data.database.dao.PatternDao
import com.tradinganalytics.data.database.dao.PatternMatchDao
import com.tradinganalytics.data.database.dao.RiskReportDao
import com.tradinganalytics.data.database.dao.SessionDao
import com.tradinganalytics.data.database.dao.UserDao
import com.tradinganalytics.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val loginHistoryDao: LoginHistoryDao,
    private val balanceDao: BalanceDao,
    private val goalDao: GoalDao,
    private val sessionDao: SessionDao,
    private val entryDao: EntryDao,
    private val patternDao: PatternDao,
    private val patternMatchDao: PatternMatchDao,
    private val riskReportDao: RiskReportDao,
    private val noteDao: NoteDao,
    private val backupDao: BackupDao
) {

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAll()

    suspend fun getUserById(id: Long): UserEntity? = userDao.getById(id)

    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getByUsername(username)

    suspend fun createUser(
        username: String,
        password: String,
        displayName: String,
        role: String = "User",
        status: String = "Active"
    ): Result<UserEntity> {
        return try {
            val existing = userDao.getByUsername(username)
            if (existing != null) {
                return Result.failure(IllegalArgumentException("Username already exists"))
            }

            val salt = generateSalt()
            val hash = hashPassword(password, salt)

            val user = UserEntity(
                username = username,
                passwordHash = hash,
                passwordSalt = salt,
                displayName = displayName,
                role = role,
                status = status,
                createdAt = Date()
            )
            val id = userDao.insert(user)
            Result.success(user.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticateUser(username: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getByUsername(username)
                ?: return Result.failure(IllegalArgumentException("User not found"))

            if (user.status != "Active") {
                return Result.failure(IllegalStateException("Account is ${user.status.lowercase()}"))
            }

            val hash = hashPassword(password, user.passwordSalt)
            if (hash != user.passwordHash) {
                return Result.failure(IllegalArgumentException("Invalid password"))
            }

            val updatedUser = user.copy(
                lastLogin = Date(),
                isLoggedIn = true
            )
            userDao.update(updatedUser)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: UserEntity): Result<UserEntity> {
        return try {
            userDao.update(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.delete(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateSalt(): String {
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray())
        val hash = digest.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}
