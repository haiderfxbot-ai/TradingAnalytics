package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.BalanceDao
import com.tradinganalytics.data.database.entities.BalanceEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao
) {

    suspend fun getBalance(userId: Long): Result<Double> {
        return try {
            val balance = balanceDao.getCurrentBalance(userId) ?: 0.0
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getHistory(userId: Long): Flow<List<BalanceEntity>> = balanceDao.getBalanceHistory(userId)

    suspend fun updateBalance(
        userId: Long,
        amount: Double,
        changeType: String,
        reason: String
    ): Result<BalanceEntity> {
        return try {
            val currentBalance = balanceDao.getCurrentBalance(userId) ?: 0.0
            val newBalance = when (changeType) {
                "CREDIT" -> currentBalance + amount
                "DEBIT" -> currentBalance - amount
                else -> amount
            }

            val balanceEntry = BalanceEntity(
                userId = userId,
                balance = newBalance,
                previousBalance = currentBalance,
                changeAmount = amount,
                changeType = changeType,
                reason = reason,
                date = Date()
            )
            val id = balanceDao.insert(balanceEntry)
            Result.success(balanceEntry.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetBalance(userId: Long, reason: String = "Manual reset"): Result<BalanceEntity> {
        return try {
            val currentBalance = balanceDao.getCurrentBalance(userId) ?: 0.0
            val balanceEntry = BalanceEntity(
                userId = userId,
                balance = 0.0,
                previousBalance = currentBalance,
                changeAmount = currentBalance,
                changeType = "RESET",
                reason = reason,
                date = Date()
            )
            val id = balanceDao.insert(balanceEntry)
            Result.success(balanceEntry.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calculatePnL(userId: Long): Result<Double> {
        return try {
            val currentBalance = balanceDao.getCurrentBalance(userId) ?: 0.0
            val startingBalance = getStartingBalance(userId)
            Result.success(currentBalance - startingBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getStartingBalance(userId: Long): Double {
        val history = balanceDao.getBalanceHistory(userId)
        return 0.0
    }
}
