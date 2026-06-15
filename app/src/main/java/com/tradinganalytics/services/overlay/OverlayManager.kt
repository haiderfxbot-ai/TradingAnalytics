package com.tradinganalytics.services.overlay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _overlayState = MutableStateFlow(OverlayMode.HIDDEN)
    val overlayState: StateFlow<OverlayMode> = _overlayState.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun requestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            activity.startActivity(intent)
        }
    }

    fun startOverlayService() {
        if (!hasOverlayPermission()) return
        OverlayService.start(context)
        _isRunning.value = true
        _overlayState.value = OverlayMode.BUBBLE
    }

    fun stopOverlayService() {
        OverlayService.stop(context)
        _isRunning.value = false
        _overlayState.value = OverlayMode.HIDDEN
    }

    fun isOverlayRunning(): Boolean = _isRunning.value

    fun switchMode(mode: OverlayMode) {
        _overlayState.value = mode
    }
}
