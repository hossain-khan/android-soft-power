package dev.hossain.power.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Executor for power-related actions such as locking screen, turning off screen, etc.
 *
 * This interface provides a unified API for executing power actions across the app,
 * abstracting away the underlying implementation details (accessibility service, device admin).
 */
interface PowerActionExecutor {
    /**
     * Locks the device screen.
     *
     * Attempts to lock screen using:
     * 1. Accessibility service (if available)
     * 2. Device admin (if enabled)
     *
     * @return Result.success if locked successfully, Result.failure with exception otherwise
     */
    suspend fun lockScreen(): Result<Unit>

    /**
     * Turns the screen off (which locks the device if security is enabled).
     *
     * Uses accessibility service global action. Same as [lockScreen] on modern Android versions.
     *
     * @return Result.success if action performed, Result.failure with exception otherwise
     */
    suspend fun turnScreenOff(): Result<Unit>

    /**
     * Opens the system power settings screen.
     *
     * @return Result.success if settings opened, Result.failure with exception otherwise
     */
    suspend fun openPowerSettings(): Result<Unit>

    /**
     * Opens the emergency dialer.
     *
     * @return Result.success if dialer opened, Result.failure with exception otherwise
     */
    suspend fun openEmergencyDialer(): Result<Unit>
}

/**
 * Implementation of [PowerActionExecutor] that delegates to accessibility service and device admin.
 *
 * Uses Metro DI for singleton injection with [SingleIn] and [ContributesBinding].
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class PowerActionExecutorImpl
    constructor(
        @ApplicationContext private val context: Context,
        private val lockScreenManager: LockScreenManager,
    ) : PowerActionExecutor {
        companion object {
            private const val TAG = "PowerActionExecutor"
        }

        override suspend fun lockScreen(): Result<Unit> {
            // Try accessibility service first
            val accessibilityService = PowerAccessibilityService.getInstance()
            if (accessibilityService != null) {
                val success = accessibilityService.performLockScreen()
                if (success) {
                    Log.d(TAG, "Screen locked via accessibility service")
                    return Result.success(Unit)
                }
                Log.w(TAG, "Accessibility service lock failed, trying device admin")
            }

            // Fallback to device admin
            val adminSuccess = lockScreenManager.lockScreen()
            return if (adminSuccess) {
                Log.d(TAG, "Screen locked via device admin")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to lock screen: no method available")
                Result.failure(
                    IllegalStateException(
                        "Cannot lock screen: accessibility service not running and device admin not enabled",
                    ),
                )
            }
        }

        override suspend fun turnScreenOff(): Result<Unit> {
            // Screen off uses the same accessibility action as lock screen on modern Android
            val accessibilityService = PowerAccessibilityService.getInstance()
            if (accessibilityService == null) {
                Log.e(TAG, "Cannot turn screen off: accessibility service not running")
                return Result.failure(
                    IllegalStateException("Accessibility service is not running"),
                )
            }

            val success = accessibilityService.performLockScreen()
            return if (success) {
                Log.d(TAG, "Screen turned off via accessibility service")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to turn screen off")
                Result.failure(IllegalStateException("Failed to turn screen off"))
            }
        }

        override suspend fun openPowerSettings(): Result<Unit> =
            try {
                val intent =
                    Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(intent)
                Log.d(TAG, "Opened power settings")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open power settings", e)
                Result.failure(e)
            }

        override suspend fun openEmergencyDialer(): Result<Unit> =
            try {
                val intent =
                    Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(intent)
                Log.d(TAG, "Opened emergency dialer")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open emergency dialer", e)
                Result.failure(e)
            }
    }
