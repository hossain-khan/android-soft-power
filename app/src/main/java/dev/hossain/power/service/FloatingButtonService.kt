package dev.hossain.power.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.hossain.power.MainActivity
import dev.hossain.power.PowerApp
import dev.hossain.power.PowerPanelActivity
import dev.hossain.power.R
import dev.hossain.power.data.AppPreferences
import dev.hossain.power.service.HapticFeedbackManager
import dev.hossain.power.service.PowerActionExecutor
import dev.hossain.power.ui.overlay.FloatingPowerButton
import dev.hossain.power.ui.theme.PowerAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Foreground service that displays a draggable floating power button overlay.
 *
 * This service:
 * - Shows a persistent notification while running
 * - Displays a floating button that can be dragged across the screen
 * - Snaps to screen edges (left/right) when released
 * - Persists button position across restarts
 * - Handles tap and long-press events
 */
class FloatingButtonService :
    Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private var windowManager: WindowManager? = null
    private var floatingView: ComposeView? = null
    private var buttonPosition by mutableStateOf(Point(0, 0))
    private var isDragging = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private lateinit var appPreferences: AppPreferences
    private lateinit var hapticFeedbackManager: HapticFeedbackManager
    private lateinit var powerActionExecutor: PowerActionExecutor
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        // Get dependencies from app graph
        val app = application as PowerApp
        appPreferences = app.appGraph().appPreferences
        hapticFeedbackManager = app.appGraph().hapticFeedbackManager
        powerActionExecutor = app.appGraph().powerActionExecutor

        // Create notification channel
        createNotificationChannel()

        // Start foreground with notification
        startForeground(NOTIFICATION_ID, createNotification())

        // Show floating button
        showFloatingButton()

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        Log.d(TAG, "FloatingButtonService created and started")
    }

    override fun onDestroy() {
        removeFloatingButton()
        serviceScope.cancel()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        store.clear()
        Log.d(TAG, "FloatingButtonService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        // Intent to open the app when notification is tapped
        val openAppIntent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val openAppPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        // Intent to disable floating button
        val disableIntent =
            Intent(this, FloatingButtonService::class.java).apply {
                action = ACTION_DISABLE
            }
        val disablePendingIntent =
            PendingIntent.getService(
                this,
                1,
                disableIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val disableAction =
            Notification.Action
                .Builder(
                    null,
                    getString(R.string.notification_action_disable),
                    disablePendingIntent,
                ).build()

        return Notification
            .Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openAppPendingIntent)
            .addAction(disableAction)
            .build()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_DISABLE -> {
                appPreferences.setFloatingButtonEnabled(false)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun showFloatingButton() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Get screen dimensions
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Load saved position or use default
        val savedPosition = appPreferences.getFloatingButtonPosition()
        buttonPosition =
            savedPosition ?: Point(
                screenWidth - 100,
                screenHeight / 2,
            )

        // Create layout params for overlay
        val params =
            WindowManager
                .LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT,
                ).apply {
                    gravity = Gravity.TOP or Gravity.START
                    x = buttonPosition.x
                    y = buttonPosition.y
                }

        // Create ComposeView
        floatingView =
            ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@FloatingButtonService)
                setViewTreeViewModelStoreOwner(this@FloatingButtonService)
                setViewTreeSavedStateRegistryOwner(this@FloatingButtonService)

                setContent {
                    PowerAppTheme {
                        FloatingPowerButtonWithDrag()
                    }
                }

                // Handle touch events for dragging
                setOnTouchListener { view, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            isDragging = false
                            false
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val deltaX = event.rawX - initialTouchX
                            val deltaY = event.rawY - initialTouchY

                            // Check if moved enough to be considered dragging
                            if (abs(deltaX) > 10 || abs(deltaY) > 10) {
                                isDragging = true
                                params.x = initialX + deltaX.toInt()
                                params.y = initialY + deltaY.toInt()
                                windowManager?.updateViewLayout(view, params)
                            }
                            isDragging
                        }

                        MotionEvent.ACTION_UP -> {
                            if (isDragging) {
                                // Snap to edge
                                snapToEdge(params)
                                isDragging = false
                                true
                            } else {
                                false
                            }
                        }

                        else -> {
                            false
                        }
                    }
                }
            }

        // Add view to window manager
        windowManager?.addView(floatingView, params)
    }

    private fun snapToEdge(params: WindowManager.LayoutParams) {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val view = floatingView ?: return

        // Snap to nearest edge (left or right)
        val snapToLeft = params.x < screenWidth / 2
        params.x =
            if (snapToLeft) {
                16
            } else {
                screenWidth - view.width - 16
            }

        windowManager?.updateViewLayout(view, params)

        // Save position
        buttonPosition = Point(params.x, params.y)
        appPreferences.saveFloatingButtonPosition(buttonPosition)
    }

    @Composable
    private fun FloatingPowerButtonWithDrag() {
        FloatingPowerButton(
            onTap = {
                if (!isDragging) {
                    handleTap()
                }
            },
            onLongPress = {
                if (!isDragging) {
                    handleLongPress()
                }
            },
        )
    }

    private fun handleTap() {
        Log.d(TAG, "Floating button tapped - opening power panel")
        hapticFeedbackManager.performTapFeedback()

        // Launch PowerPanelActivity
        val intent =
            Intent(this, PowerPanelActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open power panel", e)
            Toast.makeText(this, "Failed to open power panel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLongPress() {
        Log.d(TAG, "Floating button long-pressed - performing lock screen")
        hapticFeedbackManager.performLongPressFeedback()

        // Lock screen via PowerActionExecutor
        serviceScope.launch {
            val result = powerActionExecutor.lockScreen()
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Screen locked successfully")
                    hapticFeedbackManager.performSuccessFeedback()
                },
                onFailure = { error ->
                    Log.w(TAG, "Failed to lock screen: ${error.message}")
                    hapticFeedbackManager.performErrorFeedback()
                    Toast
                        .makeText(
                            this@FloatingButtonService,
                            "Enable Accessibility Service or Device Admin to lock screen",
                            Toast.LENGTH_LONG,
                        ).show()
                },
            )
        }
    }

    private fun removeFloatingButton() {
        floatingView?.let { view ->
            windowManager?.removeView(view)
            floatingView = null
        }
    }

    companion object {
        private const val TAG = "FloatingButtonService"
        private const val CHANNEL_ID = "floating_button_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_DISABLE = "dev.hossain.power.ACTION_DISABLE_FLOATING_BUTTON"
    }
}
