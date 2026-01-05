package dev.hossain.power.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing and tracking permission states across the app.
 * This is the foundation for the onboarding flow and status dashboard.
 */
interface PermissionRepository {
    /**
     * Gets the current permission state synchronously.
     *
     * @return Current [PermissionState] with all permission statuses
     */
    fun getPermissionState(): PermissionState

    /**
     * Observes permission state changes reactively.
     * This flow emits whenever any permission state changes.
     *
     * @return Flow of [PermissionState] for reactive UI updates
     */
    fun observePermissionState(): Flow<PermissionState>

    /**
     * Opens the system accessibility settings screen.
     * Users can enable the accessibility service from this screen.
     */
    fun openAccessibilitySettings()

    /**
     * Opens the system overlay permission settings screen.
     * Users can grant overlay permission from this screen.
     */
    fun openOverlaySettings()

    /**
     * Requests device admin activation.
     * This will show a system dialog to activate device admin.
     */
    fun requestDeviceAdmin()
}
