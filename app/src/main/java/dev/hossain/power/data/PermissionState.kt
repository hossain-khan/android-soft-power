package dev.hossain.power.data

/**
 * Represents the current state of all permissions required by the app.
 *
 * @property accessibilityEnabled Whether the accessibility service is enabled
 * @property overlayEnabled Whether the overlay (draw over other apps) permission is granted
 * @property deviceAdminEnabled Whether the device admin is activated
 */
data class PermissionState(
    val accessibilityEnabled: Boolean,
    val overlayEnabled: Boolean,
    val deviceAdminEnabled: Boolean,
) {
    /**
     * Returns true if all permissions are granted (fully configured).
     */
    val isFullyConfigured: Boolean
        get() = accessibilityEnabled && overlayEnabled && deviceAdminEnabled

    /**
     * Returns true if the minimal required permissions are granted
     * (accessibility and overlay, but device admin is optional).
     */
    val isMinimallyConfigured: Boolean
        get() = accessibilityEnabled && overlayEnabled
}
