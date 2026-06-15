package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.BackupDao
import com.tradinganalytics.data.database.entities.BackupEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    private val backupDao: BackupDao
) {
    fun getAll(): Flow<List<BackupEntity>> = backupDao.getAll()

    suspend fun insert(backup: BackupEntity): Long = backupDao.insert(backup)

    suspend fun delete(backup: BackupEntity) = backupDao.delete(backup)

    suspend fun getByName(fileName: String): BackupEntity? = backupDao.getByName(fileName)
}
