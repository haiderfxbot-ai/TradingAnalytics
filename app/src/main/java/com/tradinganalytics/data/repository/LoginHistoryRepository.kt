package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.LoginHistoryDao
import com.tradinganalytics.data.database.entities.LoginHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginHistoryRepository @Inject constructor(
    private val loginHistoryDao: LoginHistoryDao
) {
    fun getAllByUser(username: String): Flow<List<LoginHistoryEntity>> =
        loginHistoryDao.getAllByUser(username)

    suspend fun getLatest(username: String): LoginHistoryEntity? =
        loginHistoryDao.getLatest(username)

    suspend fun insert(loginHistory: LoginHistoryEntity): Long =
        loginHistoryDao.insert(loginHistory)

    suspend fun delete(loginHistory: LoginHistoryEntity) =
        loginHistoryDao.delete(loginHistory)
}
