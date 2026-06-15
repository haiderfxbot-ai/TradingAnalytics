package com.tradinganalytics.security

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    enum class EventType {
        LOGIN,
        LOGOUT,
        SETTINGS_CHANGE,
        BACKUP,
        IMPORT,
        EXPORT,
        ADMIN_ACTION,
        SECURITY_EVENT
    }

    data class AuditLog(
        val id: Long,
        val timestamp: Long,
        val eventType: EventType,
        val details: String,
        val username: String? = null
    )

    private val gson = Gson()
    private val logFile: File = File(context.filesDir, "audit_logs.json")

    fun writeLog(eventType: EventType, details: String, username: String? = null) {
        val logs = getLogsInternal().toMutableList()
        val entry = AuditLog(
            id = System.nanoTime(),
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            details = details,
            username = username
        )
        logs.add(entry)
        val maxLogs = 10000
        if (logs.size > maxLogs) {
            val trimmed = logs.drop(logs.size - maxLogs)
            writeLogsToFile(trimmed)
        } else {
            writeLogsToFile(logs)
        }
    }

    fun getLogs(): List<AuditLog> {
        return getLogsInternal().sortedByDescending { it.timestamp }
    }

    fun getLogs(eventType: EventType): List<AuditLog> {
        return getLogs().filter { it.eventType == eventType }
    }

    fun getLogs(
        eventType: EventType? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        username: String? = null,
        limit: Int = 100
    ): List<AuditLog> {
        return getLogs().filter { log ->
            (eventType == null || log.eventType == eventType) &&
                (startTime == null || log.timestamp >= startTime) &&
                (endTime == null || log.timestamp <= endTime) &&
                (username == null || log.username == username)
        }.take(limit)
    }

    fun clearLogs() {
        if (logFile.exists()) {
            logFile.delete()
        }
        writeLogsToFile(emptyList())
    }

    fun getLogCount(): Int = getLogsInternal().size

    fun exportLogsToFile(outputFile: File): Boolean {
        return try {
            val logs = getLogs()
            outputFile.writeText(gson.toJson(logs))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getLogsInternal(): List<AuditLog> {
        if (!logFile.exists()) return emptyList()
        return try {
            val content = logFile.readText()
            if (content.isBlank()) return emptyList()
            val type = object : TypeToken<List<AuditLog>>() {}.type
            gson.fromJson(content, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun writeLogsToFile(logs: List<AuditLog>) {
        try {
            logFile.writeText(gson.toJson(logs))
        } catch (_: Exception) {
        }
    }
}
