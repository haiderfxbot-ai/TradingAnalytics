package com.tradinganalytics.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
import com.tradinganalytics.data.database.entities.BackupEntity
import com.tradinganalytics.data.database.entities.BalanceEntity
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.database.entities.GoalEntity
import com.tradinganalytics.data.database.entities.LoginHistoryEntity
import com.tradinganalytics.data.database.entities.NoteEntity
import com.tradinganalytics.data.database.entities.PatternEntity
import com.tradinganalytics.data.database.entities.PatternMatchEntity
import com.tradinganalytics.data.database.entities.RiskReportEntity
import com.tradinganalytics.data.database.entities.SessionEntity
import com.tradinganalytics.data.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        LoginHistoryEntity::class,
        BalanceEntity::class,
        GoalEntity::class,
        SessionEntity::class,
        EntryEntity::class,
        PatternEntity::class,
        PatternMatchEntity::class,
        RiskReportEntity::class,
        NoteEntity::class,
        BackupEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun loginHistoryDao(): LoginHistoryDao
    abstract fun balanceDao(): BalanceDao
    abstract fun goalDao(): GoalDao
    abstract fun sessionDao(): SessionDao
    abstract fun entryDao(): EntryDao
    abstract fun patternDao(): PatternDao
    abstract fun patternMatchDao(): PatternMatchDao
    abstract fun riskReportDao(): RiskReportDao
    abstract fun noteDao(): NoteDao
    abstract fun backupDao(): BackupDao
}
