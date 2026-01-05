package dev.hossain.power.service

import android.app.Activity

/**
 * Manager for lock screen functionality using Device Admin.
 *
 * This interface provides methods to lock the device screen using the Device Admin API
 * ([android.app.admin.DevicePolicyManager.lockNow]). It serves as a reliable alternative
 * to the Accessibility Service for locking the screen, especially on older Android versions.
 *
 * ## Usage
 * 1. Check if Device Admin is enabled with [isDeviceAdminEnabled]
 * 2. If not enabled, request activation with [requestDeviceAdmin]
 * 3. Once enabled, use [lockScreen] to lock the device
 *
 * ## Requirements
 * - User must explicitly enable Device Admin for this app
 * - [dev.hossain.power.admin.LockAdminReceiver] must be registered in AndroidManifest.xml
 *
 * @see android.app.admin.DevicePolicyManager
 * @see dev.hossain.power.admin.LockAdminReceiver
 */
interface LockScreenManager {
    /**
     * Locks the device screen immediately using Device Admin API.
     *
     * This method requires Device Admin to be enabled. Check [isDeviceAdminEnabled] first.
     * If Device Admin is not enabled, this method will return false instead of throwing an exception.
     *
     * @return true if the lock action was performed successfully, false otherwise
     */
    fun lockScreen(): Boolean

    /**
     * Checks if Device Admin is currently enabled for this app.
     *
     * @return true if Device Admin is active, false otherwise
     */
    fun isDeviceAdminEnabled(): Boolean

    /**
     * Requests Device Admin activation from the user.
     *
     * This opens a system dialog where the user can choose to enable Device Admin for this app.
     * The dialog will show the explanation text configured in AndroidManifest.xml.
     *
     * @param activity The activity context from which to launch the Device Admin request
     */
    fun requestDeviceAdmin(activity: Activity)
}
