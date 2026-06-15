package com.tradinganalytics

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TradingAnalyticsApp : Application() {

    companion object {
        const val CHANNEL_ID_SYNC = "sync_channel"
        const val CHANNEL_ID_BACKUP = "backup_channel"
        const val CHANNEL_ID_ALERTS = "alerts_channel"

        lateinit var instance: TradingAnalyticsApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_SYNC,
                    "Data Sync",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications for background data synchronization"
                },
                NotificationChannel(
                    CHANNEL_ID_BACKUP,
                    "Backups",
                    NotificationManager.IMPORTANCE_MIN
                ).apply {
                    description = "Notifications for backup and restore operations"
                },
                NotificationChannel(
                    CHANNEL_ID_ALERTS,
                    "Trading Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Critical trading alerts and notifications"
                }
            )

            val manager = getSystemService(NotificationManager::class.java)
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
}
