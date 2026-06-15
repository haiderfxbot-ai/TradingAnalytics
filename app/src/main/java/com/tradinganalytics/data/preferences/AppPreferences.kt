package com.tradinganalytics.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val OVERLAY_POSITION_X = floatPreferencesKey("overlay_position_x")
        val OVERLAY_POSITION_Y = floatPreferencesKey("overlay_position_y")
        val OVERLAY_WIDTH = intPreferencesKey("overlay_width")
        val OVERLAY_HEIGHT = intPreferencesKey("overlay_height")
        val OVERLAY_OPACITY = floatPreferencesKey("overlay_opacity")
        val OVERLAY_ENABLED = booleanPreferencesKey("overlay_enabled")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val FAVORITE_PATTERN_IDS = stringPreferencesKey("favorite_pattern_ids")
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val BACKUP_SCHEDULE = stringPreferencesKey("backup_schedule")
    }

    val theme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME] ?: "system"
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme
        }
    }

    val overlayPositionX: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_POSITION_X] ?: 0f
    }

    val overlayPositionY: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_POSITION_Y] ?: 0f
    }

    suspend fun setOverlayPosition(x: Float, y: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OVERLAY_POSITION_X] = x
            prefs[Keys.OVERLAY_POSITION_Y] = y
        }
    }

    val overlayWidth: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_WIDTH] ?: 300
    }

    val overlayHeight: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_HEIGHT] ?: 200
    }

    suspend fun setOverlaySize(width: Int, height: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OVERLAY_WIDTH] = width
            prefs[Keys.OVERLAY_HEIGHT] = height
        }
    }

    val overlayOpacity: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_OPACITY] ?: 0.8f
    }

    suspend fun setOverlayOpacity(opacity: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OVERLAY_OPACITY] = opacity
        }
    }

    val isOverlayEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_ENABLED] ?: true
    }

    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OVERLAY_ENABLED] = enabled
        }
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_FIRST_LAUNCH] = false
        }
    }

    fun getFavoritePatternIds(): Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.FAVORITE_PATTERN_IDS]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun setFavoritePatternIds(ids: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FAVORITE_PATTERN_IDS] = ids.joinToString(",")
        }
    }

    fun isAutoBackupEnabled(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AUTO_BACKUP_ENABLED] ?: false
    }

    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTO_BACKUP_ENABLED] = enabled
        }
    }

    fun getBackupSchedule(): Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.BACKUP_SCHEDULE] ?: "daily"
    }

    suspend fun setBackupSchedule(schedule: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BACKUP_SCHEDULE] = schedule
        }
    }
}
