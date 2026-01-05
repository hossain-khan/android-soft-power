package dev.hossain.power.service

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.hossain.power.admin.LockAdminReceiver
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Implementation of [LockScreenManager] using Device Admin API.
 *
 * This manager uses [DevicePolicyManager.lockNow] to lock the device screen.
 * It requires the user to enable Device Admin for this app through the system settings.
 *
 * Uses Metro DI for dependency injection with singleton scope.
 *
 * @see LockScreenManager
 * @see DevicePolicyManager
 * @see LockAdminReceiver
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class LockScreenManagerImpl
    constructor(
        @ApplicationContext private val context: Context,
    ) : LockScreenManager {
        companion object {
            private const val TAG = "LockScreenManager"
        }

        private val devicePolicyManager: DevicePolicyManager by lazy {
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        }

        private val adminComponent: ComponentName by lazy {
            ComponentName(context, LockAdminReceiver::class.java)
        }

        override fun lockScreen(): Boolean {
            if (!isDeviceAdminEnabled()) {
                Log.w(TAG, "Cannot lock screen: Device Admin is not enabled")
                return false
            }

            return try {
                devicePolicyManager.lockNow()
                Log.d(TAG, "Device locked successfully via Device Admin")
                true
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception while locking device", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected exception while locking device", e)
                false
            }
        }

        override fun isDeviceAdminEnabled(): Boolean = devicePolicyManager.isAdminActive(adminComponent)

        override fun requestDeviceAdmin(activity: Activity) {
            if (isDeviceAdminEnabled()) {
                Log.d(TAG, "Device Admin is already enabled")
                return
            }

            val intent =
                Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Power Button Assist requires Device Administrator permission to reliably lock your device screen.",
                    )
                }

            try {
                activity.startActivity(intent)
                Log.d(TAG, "Device Admin request launched")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to launch Device Admin request", e)
            }
        }
    }
