package com.tradinganalytics.services.overlay

enum class OverlayMode {
    HIDDEN,
    BUBBLE,
    PANEL
}

data class OverlayConfig(
    val bubbleSize: Int = OverlayConstants.DEFAULT_BUBBLE_SIZE,
    val panelWidth: Int = OverlayConstants.DEFAULT_PANEL_WIDTH,
    val panelHeight: Int = OverlayConstants.DEFAULT_PANEL_HEIGHT,
    val opacity: Float = OverlayConstants.DEFAULT_OPACITY,
    val positionX: Int = 0,
    val positionY: Int = 0,
    val isPinned: Boolean = false,
    val autoHide: Boolean = true,
    val theme: String = "system"
)

object OverlayConstants {
    const val DEFAULT_BUBBLE_SIZE = 64
    const val DEFAULT_PANEL_WIDTH = 360
    const val DEFAULT_PANEL_HEIGHT = 640

    const val MIN_BUBBLE_SIZE = 40
    const val MAX_BUBBLE_SIZE = 120
    const val MIN_PANEL_WIDTH = 240
    const val MAX_PANEL_WIDTH = 600
    const val MIN_PANEL_HEIGHT = 320
    const val MAX_PANEL_HEIGHT = 1080

    const val DEFAULT_OPACITY = 0.85f
    const val MIN_OPACITY = 0.2f
    const val MAX_OPACITY = 1.0f

    const val EDGE_SNAP_THRESHOLD = 80
    const val EDGE_MARGIN = 16

    const val FADE_DURATION_MS = 300L
    const val FADE_DELAY_MS = 4000L
    const val EXPAND_ANIM_DURATION_MS = 250L
    const val SNAP_ANIM_DURATION_MS = 200L

    const val DRAG_THRESHOLD = 10
    const val TAP_TIMEOUT_MS = 250L
    const val DOUBLE_TAP_TIMEOUT_MS = 400L
    const val LONG_PRESS_DURATION_MS = 600L

    const val PREFS_NAME = "overlay_prefs"
    const val PREFS_KEY_MODE = "overlay_mode"
    const val PREFS_KEY_POSITION_X = "overlay_pos_x"
    const val PREFS_KEY_POSITION_Y = "overlay_pos_y"
    const val PREFS_KEY_BUBBLE_SIZE = "overlay_bubble_size"
    const val PREFS_KEY_PANEL_WIDTH = "overlay_panel_width"
    const val PREFS_KEY_PANEL_HEIGHT = "overlay_panel_height"
    const val PREFS_KEY_OPACITY = "overlay_opacity"
    const val PREFS_KEY_IS_PINNED = "overlay_is_pinned"
    const val PREFS_KEY_AUTO_HIDE = "overlay_auto_hide"
    const val PREFS_KEY_THEME = "overlay_theme"

    const val NOTIFICATION_CHANNEL_ID = "overlay_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Floating Overlay"
    const val NOTIFICATION_ID = 9001
}
