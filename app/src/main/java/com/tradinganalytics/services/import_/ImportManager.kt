package com.tradinganalytics.services.import_

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradinganalytics.core.utils.PasswordHasher
import com.tradinganalytics.data.database.AppDatabase
import com.tradinganalytics.data.database.entities.BalanceEntity
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.database.entities.GoalEntity
import com.tradinganalytics.data.database.entities.NoteEntity
import com.tradinganalytics.data.database.entities.PatternEntity
import com.tradinganalytics.data.database.entities.SessionEntity
import com.tradinganalytics.data.database.entities.UserEntity
import com.tradinganalytics.security.AuditLogger
import com.tradinganalytics.security.EncryptionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val auditLogger: AuditLogger
) {
    private val gson = Gson()

    data class ImportValidationResult(
        val isValid: Boolean,
        val warnings: List<String> = emptyList(),
        val errors: List<String> = emptyList(),
        val itemCounts: Map<String, Int> = emptyMap()
    )

    suspend fun importBackup(file: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val content = readUriContent(file)
            val validation = validateBackupContent(content)
            if (!validation.isValid) {
                return@withContext Result.failure(ImportException(validation.errors.joinToString("; ")))
            }

            val json = JSONObject(content)
            val data = JSONObject(EncryptionManager.decrypt(json.optString("data", json.toString())))

            if (data.has("users")) importUsersArray(data.getJSONArray("users"))
            if (data.has("goals")) importGoalsArray(data.getJSONArray("goals"))
            if (data.has("balances")) importBalancesArray(data.getJSONArray("balances"))
            if (data.has("sessions")) importSessionsArray(data.getJSONArray("sessions"))
            if (data.has("entries")) importEntriesArray(data.getJSONArray("entries"))
            if (data.has("patterns")) importPatternsArray(data.getJSONArray("patterns"))
            if (data.has("notes")) importNotesArray(data.getJSONArray("notes"))

            auditLogger.writeLog(
                AuditLogger.EventType.IMPORT,
                "Backup imported successfully. Items: ${validation.itemCounts}",
                "system"
            )

            Result.success(true)
        } catch (e: Exception) {
            auditLogger.writeLog(
                AuditLogger.EventType.IMPORT,
                "Backup import failed: ${e.message}",
                "system"
            )
            Result.failure(e)
        }
    }

    suspend fun importSettings(file: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val content = readUriContent(file)
            val json = JSONObject(content)

            if (json.has("theme")) {
                val themePrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                themePrefs.edit().putString("theme", json.getString("theme")).apply()
            }

            auditLogger.writeLog(
                AuditLogger.EventType.IMPORT,
                "Settings imported successfully",
                "system"
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importPatterns(file: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val content = readUriContent(file)
            val json = JSONObject(content)
            val patternsArray = json.optJSONArray("patterns") ?: JSONArray().apply { put(json) }

            for (i in 0 until patternsArray.length()) {
                val obj = patternsArray.getJSONObject(i)
                val entity = PatternEntity(
                    patternId = obj.optString("patternId", obj.optString("id", "")),
                    name = obj.getString("name"),
                    category = obj.optString("category", "Custom"),
                    description = obj.optString("description", ""),
                    detectionRules = obj.optString("detectionRules", "{}"),
                    confidenceFormula = obj.optString("confidenceFormula", "none"),
                    riskRating = obj.optString("riskRating", "MEDIUM")
                )
                database.patternDao().insert(entity)
            }

            auditLogger.writeLog(
                AuditLogger.EventType.IMPORT,
                "${patternsArray.length()} patterns imported",
                "system"
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateBackupContent(content: String): ImportValidationResult {
        val warnings = mutableListOf<String>()
        val errors = mutableListOf<String>()
        val itemCounts = mutableMapOf<String, Int>()

        return try {
            val json = JSONObject(content)

            if (json.has("encrypted") && json.getBoolean("encrypted")) {
                val encryptedData = json.optString("data", "")
                if (encryptedData.isBlank()) {
                    errors.add("Encrypted backup data is empty")
                    return ImportValidationResult(false, errors = errors)
                }
                return ImportValidationResult(true, itemCounts = itemCounts)
            }

            if (json.has("version")) {
                val version = json.getString("version")
                if (version.startsWith("0.")) {
                    warnings.add("Backup was created with an older version: $version")
                }
            }

            val checksum = json.optString("checksum", "")
            if (checksum.isNotBlank()) {
                val dataStr = json.optString("data", json.toString())
                val computed = PasswordHasher.hashPasswordSimple(dataStr)
                if (computed != checksum) {
                    errors.add("Data integrity check failed: checksum mismatch")
                }
            }

            val sections = listOf("users", "goals", "balances", "sessions", "entries", "patterns", "notes")
            for (section in sections) {
                val array = json.optJSONArray(section)
                if (array != null) {
                    itemCounts[section] = array.length()
                }
            }

            if (itemCounts.isEmpty()) {
                warnings.add("No recognizable data sections found in backup")
            }

            ImportValidationResult(
                isValid = errors.isEmpty(),
                warnings = warnings,
                errors = errors,
                itemCounts = itemCounts
            )
        } catch (e: Exception) {
            ImportValidationResult(
                isValid = false,
                errors = listOf("Invalid JSON format: ${e.message}")
            )
        }
    }

    private fun readUriContent(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).readText()
        } ?: throw ImportException("Unable to read file from URI")
    }

    private suspend fun importUsersArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = UserEntity(
                username = obj.getString("username"),
                passwordHash = obj.optString("passwordHash", ""),
                passwordSalt = obj.optString("passwordSalt", ""),
                displayName = obj.optString("displayName", obj.getString("username")),
                role = obj.optString("role", "USER"),
                status = obj.optString("status", "ACTIVE"),
                createdAt = Date(obj.optLong("createdAt", System.currentTimeMillis())),
                lastLogin = if (obj.has("lastLogin") && !obj.isNull("lastLogin"))
                    Date(obj.getLong("lastLogin")) else null
            )
            database.userDao().insert(entity)
        }
    }

    private suspend fun importGoalsArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = GoalEntity(
                userId = obj.getLong("userId"),
                targetAmount = obj.getDouble("targetAmount"),
                currentAmount = obj.optDouble("currentAmount", 0.0),
                goalType = obj.optString("goalType", "profit"),
                date = Date(obj.optLong("date", System.currentTimeMillis())),
                isCompleted = obj.optBoolean("isCompleted", false),
                completedAt = if (obj.has("completedAt") && !obj.isNull("completedAt"))
                    Date(obj.getLong("completedAt")) else null
            )
            database.goalDao().insert(entity)
        }
    }

    private suspend fun importBalancesArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = BalanceEntity(
                userId = obj.getLong("userId"),
                balance = obj.getDouble("balance"),
                previousBalance = obj.optDouble("previousBalance", 0.0),
                changeAmount = obj.optDouble("changeAmount", 0.0),
                changeType = obj.optString("changeType", "adjustment"),
                reason = obj.optString("reason", ""),
                date = Date(obj.optLong("date", System.currentTimeMillis()))
            )
            database.balanceDao().insert(entity)
        }
    }

    private suspend fun importSessionsArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = SessionEntity(
                userId = obj.getLong("userId"),
                startTime = Date(obj.getLong("startTime")),
                endTime = if (obj.has("endTime") && !obj.isNull("endTime"))
                    Date(obj.getLong("endTime")) else null,
                profitLoss = obj.optDouble("profitLoss", 0.0),
                successRate = obj.optDouble("successRate", 0.0),
                entryCount = obj.optInt("entryCount", 0),
                status = obj.optString("status", "completed")
            )
            database.sessionDao().insert(entity)
        }
    }

    private suspend fun importEntriesArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = EntryEntity(
                userId = obj.getLong("userId"),
                sessionId = if (obj.has("sessionId") && !obj.isNull("sessionId"))
                    obj.getLong("sessionId") else null,
                date = Date(obj.optLong("date", System.currentTimeMillis())),
                result = obj.getString("result"),
                amount = obj.getDouble("amount"),
                notes = obj.optString("notes", null),
                patternId = obj.optString("patternId", null)
            )
            database.entryDao().insert(entity)
        }
    }

    private suspend fun importPatternsArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = PatternEntity(
                patternId = obj.getString("patternId"),
                name = obj.getString("name"),
                category = obj.optString("category", "Custom"),
                description = obj.optString("description", ""),
                detectionRules = obj.optString("detectionRules", "{}"),
                confidenceFormula = obj.optString("confidenceFormula", "none"),
                riskRating = obj.optString("riskRating", "MEDIUM")
            )
            database.patternDao().insert(entity)
        }
    }

    private suspend fun importNotesArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val entity = NoteEntity(
                userId = obj.getLong("userId"),
                relatedEntityType = obj.optString("relatedEntityType", "general"),
                relatedEntityId = obj.optString("relatedEntityId", null),
                content = obj.getString("content"),
                date = Date(obj.optLong("date", System.currentTimeMillis()))
            )
            database.noteDao().insert(entity)
        }
    }

    class ImportException(message: String) : Exception(message)
}
