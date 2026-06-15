package com.tradinganalytics.services.backup

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tradinganalytics.core.utils.PasswordHasher
import com.tradinganalytics.data.database.AppDatabase
import com.tradinganalytics.data.database.entities.BalanceEntity
import com.tradinganalytics.data.database.entities.BackupEntity
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.database.entities.GoalEntity
import com.tradinganalytics.data.database.entities.NoteEntity
import com.tradinganalytics.data.database.entities.PatternEntity
import com.tradinganalytics.data.database.entities.SessionEntity
import com.tradinganalytics.data.database.entities.UserEntity
import com.tradinganalytics.security.EncryptionManager
import com.tradinganalytics.services.export.ExportFormat
import com.tradinganalytics.storage.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageManager: StorageManager,
    private val database: AppDatabase
) {
    data class BackupResult(
        val backupId: Long,
        val fileName: String,
        val filePath: String,
        val fileSize: Long,
        val createdAt: Date
    )

    data class BackupInfo(
        val id: Int,
        val name: String,
        val fileName: String,
        val fileSize: Long,
        val createdAt: Date,
        val isEncrypted: Boolean
    )

    enum class AutoBackupFrequency {
        DAILY, WEEKLY, MONTHLY
    }

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)

    suspend fun createBackup(name: String): Result<BackupResult> = withContext(Dispatchers.IO) {
        try {
            val timestamp = dateFormat.format(Date())
            val safeName = name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "backup_${safeName}_$timestamp.json"
            val backupDir = storageManager.getDirectory(StorageManager.StorageDirectory.Backups)
            if (!backupDir.exists()) backupDir.mkdirs()
            val backupFile = File(backupDir, fileName)

            val backupData = collectBackupData()
            val jsonString = gson.toJson(backupData)
            val encryptedBytes = EncryptionManager.encrypt(jsonString)
            backupFile.writeText(encryptedBytes)

            val backupEntity = BackupEntity(
                fileName = fileName,
                filePath = backupFile.absolutePath,
                fileSize = backupFile.length(),
                backupType = "full",
                createdAt = Date(),
                isEncrypted = true
            )
            val backupId = database.backupDao().insert(backupEntity)

            Result.success(
                BackupResult(
                    backupId = backupId,
                    fileName = fileName,
                    filePath = backupFile.absolutePath,
                    fileSize = backupFile.length(),
                    createdAt = Date()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreBackup(backupId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val backup = database.backupDao().getByName("backup_$backupId") ?: return@withContext Result.failure(Exception("Backup not found"))
            val backupFile = File(backup.filePath)
            if (!backupFile.exists()) return@withContext Result.failure(Exception("Backup file not found"))

            val encryptedContent = backupFile.readText()
            val jsonString = EncryptionManager.decrypt(encryptedContent)
            val backupData = JSONObject(jsonString)

            restoreUsers(backupData.optJSONArray("users"))
            restoreSettings(backupData.optJSONObject("settings"))
            restoreGoals(backupData.optJSONArray("goals"))
            restoreBalances(backupData.optJSONArray("balances"))
            restoreSessions(backupData.optJSONArray("sessions"))
            restoreEntries(backupData.optJSONArray("entries"))
            restorePatterns(backupData.optJSONArray("patterns"))
            restoreNotes(backupData.optJSONArray("notes"))

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listBackups(): List<BackupInfo> = withContext(Dispatchers.IO) {
        val backupDir = storageManager.getDirectory(StorageManager.StorageDirectory.Backups)
        if (!backupDir.exists()) return@withContext emptyList()

        backupDir.listFiles()
            ?.filter { it.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?.mapIndexed { index, file ->
                BackupInfo(
                    id = index + 1,
                    name = file.nameWithoutExtension.removePrefix("backup_"),
                    fileName = file.name,
                    fileSize = file.length(),
                    createdAt = Date(file.lastModified()),
                    isEncrypted = true
                )
            } ?: emptyList()
    }

    suspend fun deleteBackup(backupId: Int) = withContext(Dispatchers.IO) {
        val backups = listBackups()
        val backup = backups.find { it.id == backupId } ?: return@withContext
        val file = File(storageManager.getDirectory(StorageManager.StorageDirectory.Backups), backup.fileName)
        if (file.exists()) file.delete()
        val entity = database.backupDao().getByName(backup.fileName)
        if (entity != null) {
            database.backupDao().delete(entity)
        }
    }

    suspend fun exportBackup(backupId: Int, format: ExportFormat): Result<File> = withContext(Dispatchers.IO) {
        try {
            val backups = listBackups()
            val backup = backups.find { it.id == backupId }
                ?: return@withContext Result.failure(Exception("Backup not found"))

            val srcFile = File(storageManager.getDirectory(StorageManager.StorageDirectory.Backups), backup.fileName)
            if (!srcFile.exists()) return@withContext Result.failure(Exception("Backup file not found"))

            val exportDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
            if (!exportDir.exists()) exportDir.mkdirs()

            val exportFile = when (format) {
                ExportFormat.JSON -> {
                    val dest = File(exportDir, backup.fileName)
                    srcFile.copyTo(dest, overwrite = true)
                    dest
                }
                ExportFormat.CSV -> {
                    val jsonContent = EncryptionManager.decrypt(srcFile.readText())
                    val csvFile = File(exportDir, backup.fileName.replace(".json", ".csv"))
                    val csvContent = convertJsonToCsv(JSONObject(jsonContent))
                    csvFile.writeText(csvContent)
                    csvFile
                }
                ExportFormat.PDF -> {
                    return@withContext Result.failure(Exception("PDF export not supported for backups"))
                }
            }

            Result.success(exportFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun collectBackupData(): Map<String, Any> {
        val users = database.userDao().getAll()
        val goals = database.goalDao().getAll()
        val balances = database.balanceDao().getAll()
        val sessions = database.sessionDao().getAll()
        val entries = database.entryDao().getAll()
        val patterns = database.patternDao().getAll()
        val notes = database.noteDao().getAll()

        return mapOf(
            "version" to "1.0",
            "createdAt" to System.currentTimeMillis(),
            "users" to users,
            "goals" to goals,
            "balances" to balances,
            "sessions" to sessions,
            "entries" to entries,
            "patterns" to patterns,
            "notes" to notes,
            "settings" to mapOf(
                "overlayConfig" to mapOf(
                    "theme" to "system",
                    "autoHide" to true
                )
            )
        )
    }

    private suspend fun restoreUsers(usersArray: JSONArray?) {
        if (usersArray == null) return
        for (i in 0 until usersArray.length()) {
            val obj = usersArray.getJSONObject(i)
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

    private suspend fun restoreSettings(settingsObj: JSONObject?) {
    }

    private suspend fun restoreGoals(goalsArray: JSONArray?) {
        if (goalsArray == null) return
        for (i in 0 until goalsArray.length()) {
            val obj = goalsArray.getJSONObject(i)
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

    private suspend fun restoreBalances(balancesArray: JSONArray?) {
        if (balancesArray == null) return
        for (i in 0 until balancesArray.length()) {
            val obj = balancesArray.getJSONObject(i)
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

    private suspend fun restoreSessions(sessionsArray: JSONArray?) {
        if (sessionsArray == null) return
        for (i in 0 until sessionsArray.length()) {
            val obj = sessionsArray.getJSONObject(i)
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

    private suspend fun restoreEntries(entriesArray: JSONArray?) {
        if (entriesArray == null) return
        for (i in 0 until entriesArray.length()) {
            val obj = entriesArray.getJSONObject(i)
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

    private suspend fun restorePatterns(patternsArray: JSONArray?) {
        if (patternsArray == null) return
        for (i in 0 until patternsArray.length()) {
            val obj = patternsArray.getJSONObject(i)
            val entity = PatternEntity(
                patternId = obj.getString("patternId"),
                name = obj.getString("name"),
                category = obj.optString("category", "Custom"),
                description = obj.optString("description", ""),
                detectionRules = obj.optString("detectionRules", "{}"),
                confidenceFormula = obj.optString("confidenceFormula", "none"),
                riskRating = obj.optString("riskRating", "MEDIUM"),
                timesDetected = obj.optInt("timesDetected", 0),
                timesSuccessful = obj.optInt("timesSuccessful", 0),
                historicalAccuracy = obj.optDouble("historicalAccuracy", 0.0),
                averageConfidence = obj.optDouble("averageConfidence", 0.0)
            )
            database.patternDao().insert(entity)
        }
    }

    private suspend fun restoreNotes(notesArray: JSONArray?) {
        if (notesArray == null) return
        for (i in 0 until notesArray.length()) {
            val obj = notesArray.getJSONObject(i)
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

    private fun convertJsonToCsv(json: JSONObject): String {
        val sb = StringBuilder()
        val keys = json.keySet()
        sb.appendLine(keys.joinToString(","))
        sb.appendLine(keys.joinToString(",") { key ->
            json.opt(key)?.toString()?.replace("\"", "\"\"")?.let { "\"$it\"" } ?: ""
        })
        val sections = listOf("users", "goals", "balances", "sessions", "entries", "patterns", "notes")
        for (section in sections) {
            val array = json.optJSONArray(section) ?: continue
            if (array.length() > 0) {
                sb.appendLine()
                sb.appendLine("--- $section ---")
                val first = array.getJSONObject(0)
                val headers = first.keySet().toList()
                sb.appendLine(headers.joinToString(",") { "\"$it\"" })
                for (i in 0 until array.length()) {
                    val row = array.getJSONObject(i)
                    sb.appendLine(headers.joinToString(",") { key ->
                        val value = row.opt(key)
                        when (value) {
                            null -> ""
                            is String -> "\"${value.replace("\"", "\"\"")}\""
                            else -> value.toString()
                        }
                    })
                }
            }
        }
        return sb.toString()
    }
}
