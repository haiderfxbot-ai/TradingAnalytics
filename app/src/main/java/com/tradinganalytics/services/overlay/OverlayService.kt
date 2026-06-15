package com.tradinganalytics.services.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.app.NotificationCompat
import com.tradinganalytics.TradingAnalyticsApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var prefs: SharedPreferences
    private lateinit var bubbleView: View
    private lateinit var panelView: View

    private var overlayMode = OverlayMode.BUBBLE
    private var config = OverlayConfig()
    private var isPinned = false
    private var isAutoHideEnabled = true
    private var isFaded = false
    private var isDragging = false
    private var wasDragged = false

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var lastTapTime = 0L
    private var tapCount = 0

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var autoHideJob: Job? = null

    companion object {
        private const val SNAP_THRESHOLD = OverlayConstants.EDGE_SNAP_THRESHOLD
        private const val EDGE_MARGIN = OverlayConstants.EDGE_MARGIN
        private const val FADE_ALPHA = 0.2f
        private const val FULL_ALPHA = 1.0f

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, OverlayService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        prefs = getSharedPreferences(OverlayConstants.PREFS_NAME, Context.MODE_PRIVATE)
        createNotificationChannel()
        loadState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(OverlayConstants.NOTIFICATION_ID, notification)
        showOverlay()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        autoHideJob?.cancel()
        removeOverlayViews()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                OverlayConstants.NOTIFICATION_CHANNEL_ID,
                OverlayConstants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Floating overlay service notification"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, OverlayConstants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Floating Overlay Active")
            .setContentText("Tap to expand or drag to move")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun showOverlay() {
        removeOverlayViews()

        when (overlayMode) {
            OverlayMode.HIDDEN -> return
            OverlayMode.BUBBLE -> showBubble()
            OverlayMode.PANEL -> showPanel()
        }
    }

    private fun showBubble() {
        bubbleView = View(this).apply {
            setBackgroundResource(android.R.drawable.ic_menu_compass)
            setOnTouchListener(OverlayTouchListener())
            alpha = if (isFaded) FADE_ALPHA else FULL_ALPHA
        }

        val params = WindowManager.LayoutParams(
            config.bubbleSize,
            config.bubbleSize,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = config.positionX
            y = config.positionY
            alpha = config.opacity
        }

        try {
            windowManager.addView(bubbleView, params)
        } catch (e: Exception) {
            stopSelf()
        }

        scheduleAutoHide()
    }

    private fun showPanel() {
        panelView = View(this).apply {
            setBackgroundResource(android.R.drawable.ic_dialog_info)
            setOnTouchListener(OverlayTouchListener())
            alpha = if (isFaded) FADE_ALPHA else FULL_ALPHA
        }

        val params = WindowManager.LayoutParams(
            config.panelWidth,
            config.panelHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = config.positionX
            y = config.positionY
            alpha = config.opacity
        }

        try {
            windowManager.addView(panelView, params)
        } catch (e: Exception) {
            stopSelf()
        }

        scheduleAutoHide()
    }

    private fun removeOverlayViews() {
        try {
            if (::bubbleView.isInitialized && bubbleView.isAttachedToWindow) {
                windowManager.removeView(bubbleView)
            }
        } catch (_: Exception) {
        }
        try {
            if (::panelView.isInitialized && panelView.isAttachedToWindow) {
                windowManager.removeView(panelView)
            }
        } catch (_: Exception) {
        }
    }

    fun switchMode(mode: OverlayMode) {
        overlayMode = mode
        config = config.copy()
        saveState()
        showOverlay()
    }

    fun updateConfig(newConfig: OverlayConfig) {
        config = newConfig
        saveState()
        showOverlay()
    }

    fun togglePin() {
        isPinned = !isPinned
        saveState()
    }

    fun setAutoHideEnabled(enabled: Boolean) {
        isAutoHideEnabled = enabled
        if (!enabled) {
            autoHideJob?.cancel()
            fadeIn()
        }
        saveState()
    }

    private fun snapToEdge() {
        try {
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val viewWidth: Int
            val params: WindowManager.LayoutParams?

            if (overlayMode == OverlayMode.BUBBLE && ::bubbleView.isInitialized) {
                viewWidth = config.bubbleSize
                params = bubbleView.layoutParams as WindowManager.LayoutParams
            } else if (overlayMode == OverlayMode.PANEL && ::panelView.isInitialized) {
                viewWidth = config.panelWidth
                params = panelView.layoutParams as WindowManager.LayoutParams
            } else {
                return
            }

            val centerX = params.x + viewWidth / 2
            val snapToLeft = centerX < screenWidth / 2

            val targetX = if (snapToLeft) {
                EDGE_MARGIN
            } else {
                screenWidth - viewWidth - EDGE_MARGIN
            }

            params.x = targetX
            params.y = params.y.coerceIn(
                0,
                displayMetrics.heightPixels - viewWidth
            )

            config = config.copy(positionX = params.x, positionY = params.y)

            animatePosition(params, targetX, params.y)
            saveState()
        } catch (_: Exception) {
        }
    }

    private fun animatePosition(params: WindowManager.LayoutParams, targetX: Int, targetY: Int) {
        try {
            val view = if (overlayMode == OverlayMode.BUBBLE) bubbleView else panelView
            view?.let {
                it.animate()
                    .x(targetX.toFloat())
                    .y(targetY.toFloat())
                    .setDuration(OverlayConstants.SNAP_ANIM_DURATION_MS)
                    .start()
            }
        } catch (_: Exception) {
        }
        params.x = targetX
        params.y = targetY
        try {
            if (overlayMode == OverlayMode.BUBBLE) {
                windowManager.updateViewLayout(bubbleView, params)
            } else {
                windowManager.updateViewLayout(panelView, params)
            }
        } catch (_: Exception) {
        }
    }

    private fun scheduleAutoHide() {
        autoHideJob?.cancel()
        if (!isAutoHideEnabled || isPinned) return

        autoHideJob = serviceScope.launch {
            delay(OverlayConstants.FADE_DELAY_MS)
            fadeOut()
        }
    }

    private fun fadeOut() {
        val view = currentView() ?: return
        val fadeOutAnim = AlphaAnimation(FULL_ALPHA, FADE_ALPHA).apply {
            duration = OverlayConstants.FADE_DURATION_MS
            fillAfter = true
        }
        view.startAnimation(fadeOutAnim)
        isFaded = true
    }

    private fun fadeIn() {
        val view = currentView() ?: return
        val fadeInAnim = AlphaAnimation(view.alpha, FULL_ALPHA).apply {
            duration = OverlayConstants.FADE_DURATION_MS
            fillAfter = true
        }
        view.startAnimation(fadeInAnim)
        isFaded = false
        scheduleAutoHide()
    }

    private fun currentView(): View? {
        return when (overlayMode) {
            OverlayMode.BUBBLE -> if (::bubbleView.isInitialized) bubbleView else null
            OverlayMode.PANEL -> if (::panelView.isInitialized) panelView else null
            OverlayMode.HIDDEN -> null
        }
    }

    private fun currentParams(): WindowManager.LayoutParams? {
        return try {
            currentView()?.layoutParams as? WindowManager.LayoutParams
        } catch (_: Exception) {
            null
        }
    }

    private fun saveState() {
        prefs.edit()
            .putString(OverlayConstants.PREFS_KEY_MODE, overlayMode.name)
            .putInt(OverlayConstants.PREFS_KEY_POSITION_X, config.positionX)
            .putInt(OverlayConstants.PREFS_KEY_POSITION_Y, config.positionY)
            .putInt(OverlayConstants.PREFS_KEY_BUBBLE_SIZE, config.bubbleSize)
            .putInt(OverlayConstants.PREFS_KEY_PANEL_WIDTH, config.panelWidth)
            .putInt(OverlayConstants.PREFS_KEY_PANEL_HEIGHT, config.panelHeight)
            .putFloat(OverlayConstants.PREFS_KEY_OPACITY, config.opacity)
            .putBoolean(OverlayConstants.PREFS_KEY_IS_PINNED, isPinned)
            .putBoolean(OverlayConstants.PREFS_KEY_AUTO_HIDE, isAutoHideEnabled)
            .putString(OverlayConstants.PREFS_KEY_THEME, config.theme)
            .apply()
    }

    private fun loadState() {
        overlayMode = try {
            OverlayMode.valueOf(
                prefs.getString(OverlayConstants.PREFS_KEY_MODE, OverlayMode.BUBBLE.name) ?: OverlayMode.BUBBLE.name
            )
        } catch (_: Exception) {
            OverlayMode.BUBBLE
        }
        isPinned = prefs.getBoolean(OverlayConstants.PREFS_KEY_IS_PINNED, false)
        isAutoHideEnabled = prefs.getBoolean(OverlayConstants.PREFS_KEY_AUTO_HIDE, true)
        config = OverlayConfig(
            bubbleSize = prefs.getInt(OverlayConstants.PREFS_KEY_BUBBLE_SIZE, OverlayConstants.DEFAULT_BUBBLE_SIZE),
            panelWidth = prefs.getInt(OverlayConstants.PREFS_KEY_PANEL_WIDTH, OverlayConstants.DEFAULT_PANEL_WIDTH),
            panelHeight = prefs.getInt(OverlayConstants.PREFS_KEY_PANEL_HEIGHT, OverlayConstants.DEFAULT_PANEL_HEIGHT),
            opacity = prefs.getFloat(OverlayConstants.PREFS_KEY_OPACITY, OverlayConstants.DEFAULT_OPACITY),
            positionX = prefs.getInt(OverlayConstants.PREFS_KEY_POSITION_X, 0),
            positionY = prefs.getInt(OverlayConstants.PREFS_KEY_POSITION_Y, 0),
            isPinned = isPinned,
            autoHide = isAutoHideEnabled,
            theme = prefs.getString(OverlayConstants.PREFS_KEY_THEME, "system") ?: "system"
        )
    }

    private inner class OverlayTouchListener : View.OnTouchListener {
        private var longPressTriggered = false
        private var longPressRunnable: Runnable = Runnable {
            longPressTriggered = true
            handleLongPress()
        }

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (isPinned) return false

            val params = currentParams() ?: return false

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = false
                    wasDragged = false
                    longPressTriggered = false
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    view.postDelayed(longPressRunnable, OverlayConstants.LONG_PRESS_DURATION_MS)

                    if (isFaded) fadeIn()

                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()

                    if (!isDragging && (Math.abs(dx) > OverlayConstants.DRAG_THRESHOLD || Math.abs(dy) > OverlayConstants.DRAG_THRESHOLD)) {
                        isDragging = true
                        wasDragged = true
                        view.removeCallbacks(longPressRunnable)
                    }

                    if (isDragging) {
                        params.x = initialX + dx
                        params.y = initialY + dy
                        try {
                            windowManager.updateViewLayout(view, params)
                        } catch (_: Exception) {
                        }
                    }

                    return true
                }

                MotionEvent.ACTION_UP -> {
                    view.removeCallbacks(longPressRunnable)

                    if (longPressTriggered) {
                        longPressTriggered = false
                        return true
                    }

                    if (!wasDragged) {
                        handleTap()
                    } else {
                        snapToEdge()
                        isDragging = false
                    }

                    return true
                }

                MotionEvent.ACTION_OUTSIDE -> {
                    view.removeCallbacks(longPressRunnable)
                    if (isAutoHideEnabled && !isPinned) {
                        scheduleAutoHide()
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun handleTap() {
        val now = System.currentTimeMillis()
        if (now - lastTapTime < OverlayConstants.DOUBLE_TAP_TIMEOUT_MS) {
            tapCount++
        } else {
            tapCount = 1
        }
        lastTapTime = now

        when (tapCount) {
            1 -> {
                if (overlayMode == OverlayMode.BUBBLE) {
                    serviceScope.launch {
                        delay(OverlayConstants.TAP_TIMEOUT_MS)
                        if (tapCount == 1) {
                            switchMode(OverlayMode.PANEL)
                        }
                    }
                }
            }
            2 -> {
                tapCount = 0
                if (overlayMode == OverlayMode.PANEL) {
                    switchMode(OverlayMode.BUBBLE)
                }
            }
        }
    }

    private fun handleLongPress() {
        togglePin()
    }
}
