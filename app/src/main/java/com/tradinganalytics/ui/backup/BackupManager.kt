package com.tradinganalytics.ui.backup

import com.tradinganalytics.data.database.AppDatabase
import com.tradinganalytics.data.database.entities.BackupEntity
import com.tradinganalytics.storage.StorageManager
import java.io.File
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val database: AppDatabase,
    private val storageManager: StorageManager
) {

    suspend fun createBackup(fileName: String, backupType: String): Result<BackupEntity> {
        return try {
            val dbFile = database.openHelper.writableDatabase.path
            val source = File(dbFile)
            if (!source.exists()) return Result.failure(Exception("Database file not found"))

            val backupFile = storageManager.writeFile(
                StorageManager.StorageDirectory.Backups,
                fileName,
                source.readBytes()
            ).getOrThrow()

            val entity = BackupEntity(
                fileName = fileName,
                filePath = backupFile.absolutePath,
                fileSize = backupFile.length(),
                backupType = backupType,
                createdAt = Date(),
                isEncrypted = false
            )
            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreBackup(backup: BackupEntity): Result<Unit> {
        return try {
            val backupBytes = storageManager.readFile(
                StorageManager.StorageDirectory.Backups,
                backup.fileName
            ).getOrThrow()

            val dbPath = database.openHelper.writableDatabase.path
            val dbFile = File(dbPath)
            dbFile.parentFile?.mkdirs()
            dbFile.writeBytes(backupBytes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteBackupFile(backup: BackupEntity): Boolean {
        return storageManager.deleteFile(
            StorageManager.StorageDirectory.Backups,
            backup.fileName
        )
    }
}
