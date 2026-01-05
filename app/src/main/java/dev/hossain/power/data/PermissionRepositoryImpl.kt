package dev.hossain.power.data

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of [PermissionRepository] that checks system permissions and provides
 * navigation to settings screens.
 *
 * Uses Metro DI for dependency injection with singleton scope.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class PermissionRepositoryImpl
    constructor(
        @ApplicationContext private val context: Context,
    ) : PermissionRepository {
        private val accessibilityManager: AccessibilityManager by lazy {
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        }

        private val devicePolicyManager: DevicePolicyManager by lazy {
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        }

        companion object {
            private const val TAG = "PermissionRepository"

            // Service class names using short format (relative to package name)
            // Android's AccessibilityServiceInfo.id uses format: "packageName/.className"
            private const val ACCESSIBILITY_SERVICE_CLASS = ".service.PowerAccessibilityService"
            private const val DEVICE_ADMIN_CLASS = ".admin.LockAdminReceiver"
        }

        override fun getPermissionState(): PermissionState =
            PermissionState(
                accessibilityEnabled = isAccessibilityServiceEnabled(),
                overlayEnabled = isOverlayPermissionGranted(),
                deviceAdminEnabled = isDeviceAdminActive(),
            )

        override fun observePermissionState(): Flow<PermissionState> =
            callbackFlow {
                // Send initial state
                trySend(getPermissionState())

                // TODO: Currently only listens to accessibility state changes.
                // Future improvement: Add listeners for overlay permission and device admin changes.
                // This could be done via BroadcastReceiver or periodic polling.
                val listener =
                    AccessibilityManager.AccessibilityStateChangeListener { enabled ->
                        trySend(getPermissionState())
                    }

                accessibilityManager.addAccessibilityStateChangeListener(listener)

                awaitClose {
                    accessibilityManager.removeAccessibilityStateChangeListener(listener)
                }
            }

        override fun openAccessibilitySettings() {
            val intent =
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(intent)
        }

        override fun openOverlaySettings() {
            val intent =
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}"),
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(intent)
        }

        override fun requestDeviceAdmin() {
            val componentName = ComponentName(context.packageName, DEVICE_ADMIN_CLASS)
            val intent =
                Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Power Button Assist requires Device Administrator permission to reliably lock your device screen.",
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(intent)
        }

        /**
         * Checks if the accessibility service is enabled for this app.
         */
        private fun isAccessibilityServiceEnabled(): Boolean {
            val enabledServices =
                accessibilityManager.getEnabledAccessibilityServiceList(
                    AccessibilityServiceInfo.FEEDBACK_ALL_MASK,
                )

            val serviceId = "${context.packageName}/$ACCESSIBILITY_SERVICE_CLASS"
            Log.d(TAG, "Looking for serviceId: $serviceId")
            Log.d(TAG, "Enabled services count: ${enabledServices.size}")
            enabledServices.forEach { service ->
                Log.d(TAG, "Found enabled service: ${service.id}")
            }

            return enabledServices.any { service ->
                service.id == serviceId
            }
        }

        /**
         * Checks if overlay permission is granted.
         */
        private fun isOverlayPermissionGranted(): Boolean = Settings.canDrawOverlays(context)

        /**
         * Checks if device admin is active for this app.
         */
        private fun isDeviceAdminActive(): Boolean {
            val componentName = ComponentName(context.packageName, DEVICE_ADMIN_CLASS)
            return devicePolicyManager.isAdminActive(componentName)
        }
    }
