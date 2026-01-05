package dev.hossain.power.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * Accessibility service that provides power-related actions for the app.
 *
 * This service is used to perform global actions like locking the screen when the user
 * interacts with the app's floating button. It does NOT monitor or read any screen content
 * (Play Store compliance).
 *
 * ## Usage
 * The service must be enabled in Settings > Accessibility > Power Button Assist.
 * Once enabled, use [getInstance] to access the running service instance and call
 * [performLockScreen] to lock the device.
 *
 * ## Requirements
 * - API 28+ (Android P) for [GLOBAL_ACTION_LOCK_SCREEN]
 * - Accessibility permission must be granted by user in device settings
 */
class PowerAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "PowerAccessibilityService"

        /**
         * Reference to the currently running service instance.
         * Null if the service is not enabled or not running.
         */
        @Volatile
        private var instance: PowerAccessibilityService? = null

        /**
         * Returns the currently running service instance, or null if the service is not active.
         *
         * @return The service instance if running, null otherwise
         */
        fun getInstance(): PowerAccessibilityService? = instance
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used - we only need global actions, not event monitoring
        // This service does NOT read or monitor screen content
    }

    override fun onInterrupt() {
        // Required override - called when the service is interrupted
        Log.d(TAG, "PowerAccessibilityService interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "PowerAccessibilityService connected and ready")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "PowerAccessibilityService destroyed")
    }

    /**
     * Locks the device screen using the accessibility service global action.
     *
     * This method requires:
     * - API 28+ (Android P)
     * - Accessibility service to be enabled
     *
     * @return true if the lock action was performed successfully, false otherwise
     */
    fun performLockScreen(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val result = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            if (result) {
                Log.d(TAG, "Lock screen action performed successfully")
            } else {
                Log.w(TAG, "Failed to perform lock screen action")
            }
            result
        } else {
            Log.w(TAG, "Lock screen action requires API 28+, current: ${Build.VERSION.SDK_INT}")
            false
        }
}
