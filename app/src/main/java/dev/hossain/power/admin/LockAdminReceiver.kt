package dev.hossain.power.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Device Admin receiver for lock screen functionality.
 *
 * This receiver provides a reliable fallback for locking the screen on older Android versions
 * or when Accessibility Service is not available. Once enabled by the user, it grants the app
 * permission to use [android.app.admin.DevicePolicyManager.lockNow] to lock the device.
 *
 * ## Usage
 * The Device Admin must be enabled by the user through Settings > Security > Device Admin.
 * The app can request activation via [android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN].
 *
 * ## Requirements
 * - User must explicitly enable this app as a Device Administrator
 * - Requires `android.permission.BIND_DEVICE_ADMIN` permission
 * - Must be registered in AndroidManifest.xml with proper metadata
 *
 * @see android.app.admin.DevicePolicyManager
 */
class LockAdminReceiver : DeviceAdminReceiver() {
    companion object {
        private const val TAG = "LockAdminReceiver"
    }

    /**
     * Called when the user enables this Device Administrator.
     * At this point, the app can use Device Admin APIs like lockNow().
     */
    override fun onEnabled(
        context: Context,
        intent: Intent,
    ) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Admin enabled for Power Button Assist")
    }

    /**
     * Called when the user disables this Device Administrator.
     * After this, the app can no longer use Device Admin APIs.
     */
    override fun onDisabled(
        context: Context,
        intent: Intent,
    ) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device Admin disabled for Power Button Assist")
    }
}
