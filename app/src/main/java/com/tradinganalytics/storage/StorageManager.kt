package com.tradinganalytics.storage

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class StorageManager(private val context: Context) {

    data class StorageInfo(
        val totalSpace: Long,
        val usedSpace: Long,
        val freeSpace: Long,
        val usagePercentage: Double
    )

    data class DirectoryInfo(
        val path: String,
        val size: Long,
        val fileCount: Int,
        val lastModified: Long
    )

    sealed class StorageDirectory(val relativePath: String) {
        data object Backups : StorageDirectory("backups")
        data object Exports : StorageDirectory("exports")
        data object Reports : StorageDirectory("reports")
        data object Patterns : StorageDirectory("patterns")
        data object Logs : StorageDirectory("logs")
        data object Settings : StorageDirectory("settings")
        data object Database : StorageDirectory("database")
        data object Cache : StorageDirectory("cache")
        data object Temp : StorageDirectory("temp")
        data object Audio : StorageDirectory("audio")

        companion object {
            val all = listOf(Backups, Exports, Reports, Patterns, Logs, Settings, Database, Cache, Temp, Audio)
        }
    }

    private val baseDir: File
        get() = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "TradingAnalytics"
        ) ?: File(context.filesDir, "TradingAnalytics")

    fun initializeDirectories(): Result<Map<StorageDirectory, File>> {
        return try {
            val results = mutableMapOf<StorageDirectory, File>()
            StorageDirectory.all.forEach { dir ->
                val directory = getDirectory(dir)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                results[dir] = directory
            }
            createNomediaFiles()
            createReadmeFiles()
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDirectory(directory: StorageDirectory): File {
        return File(baseDir, directory.relativePath)
    }

    fun getDirectoryPath(directory: StorageDirectory): String {
        return getDirectory(directory).absolutePath
    }

    fun fileExists(directory: StorageDirectory, fileName: String): Boolean {
        return File(getDirectory(directory), fileName).exists()
    }

    fun writeFile(directory: StorageDirectory, fileName: String, data: ByteArray): Result<File> {
        return try {
            ensureDirectoryExists(directory)
            val file = File(getDirectory(directory), fileName)
            FileOutputStream(file).use { it.write(data) }
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun readFile(directory: StorageDirectory, fileName: String): Result<ByteArray> {
        return try {
            val file = File(getDirectory(directory), fileName)
            if (!file.exists()) {
                return Result.failure(Exception("File not found: $fileName"))
            }
            Result.success(file.readBytes())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteFile(directory: StorageDirectory, fileName: String): Boolean {
        val file = File(getDirectory(directory), fileName)
        return if (file.exists()) file.delete() else false
    }

    fun listFiles(directory: StorageDirectory, extension: String? = null): List<File> {
        val dir = getDirectory(directory)
        if (!dir.exists()) return emptyList()
        return if (extension != null) {
            dir.listFiles { file -> file.extension.equals(extension, ignoreCase = true) }?.toList()
                ?: emptyList()
        } else {
            dir.listFiles()?.toList() ?: emptyList()
        }
    }

    fun getStorageInfo(directory: StorageDirectory): DirectoryInfo {
        val dir = getDirectory(directory)
        if (!dir.exists()) return DirectoryInfo(dir.absolutePath, 0L, 0, 0L)
        val files = dir.walkTopDown().toList()
        val size = files.filter { it.isFile }.sumOf { it.length() }
        val fileCount = files.filter { it.isFile }.count()
        val lastModified = files.maxOfOrNull { it.lastModified() } ?: 0L
        return DirectoryInfo(dir.absolutePath, size, fileCount, lastModified)
    }

    fun getTotalStorageInfo(): StorageInfo {
        val allFiles = baseDir.walkTopDown().toList()
        val totalSize = allFiles.filter { it.isFile }.sumOf { it.length() }
        return StorageInfo(
            totalSpace = baseDir.totalSpace,
            usedSpace = totalSize,
            freeSpace = baseDir.freeSpace,
            usagePercentage = if (baseDir.totalSpace > 0) {
                (totalSize.toDouble() / baseDir.totalSpace) * 100.0
            } else 0.0
        )
    }

    fun cleanupDirectory(directory: StorageDirectory, maxAgeMs: Long = 7 * 24 * 60 * 60 * 1000L): Int {
        val dir = getDirectory(directory)
        if (!dir.exists()) return 0
        val cutoff = System.currentTimeMillis() - maxAgeMs
        var deleted = 0
        dir.walkTopDown().forEach { file ->
            if (file.isFile && file.lastModified() < cutoff) {
                if (file.delete()) deleted++
            }
        }
        return deleted
    }

    fun cleanupAll(maxAgeMs: Long = 30 * 24 * 60 * 60 * 1000L): Map<StorageDirectory, Int> {
        val results = mutableMapOf<StorageDirectory, Int>()
        StorageDirectory.all.forEach { dir ->
            results[dir] = cleanupDirectory(dir, maxAgeMs)
        }
        return results
    }

    fun cleanupCache(): Int {
        val cacheDir = getDirectory(StorageDirectory.Cache)
        val tempDir = getDirectory(StorageDirectory.Temp)
        var deleted = 0
        if (cacheDir.exists()) {
            deleted += cacheDir.walkTopDown().filter { it.isFile }.count { it.delete() }
        }
        if (tempDir.exists()) {
            deleted += tempDir.walkTopDown().filter { it.isFile }.count { it.delete() }
        }
        return deleted
    }

    fun clearAllData(): Boolean {
        return try {
            baseDir.walkTopDown().sortedByDescending { it.absolutePath }.forEach { it.delete() }
            initializeDirectories()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDirectorySize(directory: StorageDirectory): Long {
        val dir = getDirectory(directory)
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.2f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.2f MB".format(bytes / 1_048_576.0)
            bytes >= 1_024 -> "%.2f KB".format(bytes / 1_024.0)
            else -> "$bytes B"
        }
    }

    fun exportFile(directory: StorageDirectory, fileName: String, targetFile: File): Result<File> {
        return try {
            val source = File(getDirectory(directory), fileName)
            if (!source.exists()) {
                return Result.failure(Exception("Source file not found: $fileName"))
            }
            source.copyTo(targetFile, overwrite = true)
            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun importFile(sourceFile: File, directory: StorageDirectory, fileName: String? = null): Result<File> {
        return try {
            if (!sourceFile.exists()) {
                return Result.failure(Exception("Source file not found: ${sourceFile.absolutePath}"))
            }
            val targetName = fileName ?: sourceFile.name
            val destDir = getDirectory(directory)
            ensureDirectoryExists(directory)
            val destFile = File(destDir, targetName)
            sourceFile.copyTo(destFile, overwrite = true)
            Result.success(destFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ensureDirectoryExists(directory: StorageDirectory) {
        getDirectory(directory).mkdirs()
    }

    private fun createNomediaFiles() {
        StorageDirectory.all.forEach { dir ->
            val nomedia = File(getDirectory(dir), ".nomedia")
            if (!nomedia.exists()) {
                try {
                    nomedia.createNewFile()
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun createReadmeFiles() {
        val readmeContent = mapOf(
            StorageDirectory.Backups to "Database backups and restore points",
            StorageDirectory.Exports to "Exported trading data and reports",
            StorageDirectory.Reports to "Generated PDF and CSV report files",
            StorageDirectory.Patterns to "Custom pattern definitions and libraries",
            StorageDirectory.Logs to "Application log files",
            StorageDirectory.Settings to "User settings and configuration exports",
            StorageDirectory.Database to "Database export files",
            StorageDirectory.Cache to "Temporary cached data files",
            StorageDirectory.Temp to "Temporary working files"
        )

        readmeContent.forEach { (dir, description) ->
            val readmeFile = File(getDirectory(dir), "README.txt")
            if (!readmeFile.exists()) {
                try {
                    readmeFile.writeText(
                        """
                        |TradingAnalytics - ${dir.relativePath}
                        |${"=".repeat(40)}
                        |$description
                        |Created: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}
                        |
                        |WARNING: Do not manually modify files in this directory
                        |unless you understand the impact on application functionality.
                        """.trimMargin()
                    )
                } catch (_: Exception) {
                }
            }
        }
    }
}
