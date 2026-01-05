package dev.hossain.power.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PermissionState] data class.
 */
class PermissionStateTest {
    @Test
    fun `isFullyConfigured returns true when all permissions are enabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )
        assertTrue(state.isFullyConfigured)
    }

    @Test
    fun `isFullyConfigured returns false when accessibility is disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )
        assertFalse(state.isFullyConfigured)
    }

    @Test
    fun `isFullyConfigured returns false when overlay is disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = false,
                deviceAdminEnabled = true,
            )
        assertFalse(state.isFullyConfigured)
    }

    @Test
    fun `isFullyConfigured returns false when device admin is disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )
        assertFalse(state.isFullyConfigured)
    }

    @Test
    fun `isFullyConfigured returns false when all permissions are disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = false,
                deviceAdminEnabled = false,
            )
        assertFalse(state.isFullyConfigured)
    }

    @Test
    fun `isMinimallyConfigured returns true when accessibility and overlay are enabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )
        assertTrue(state.isMinimallyConfigured)
    }

    @Test
    fun `isMinimallyConfigured returns true when all permissions are enabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )
        assertTrue(state.isMinimallyConfigured)
    }

    @Test
    fun `isMinimallyConfigured returns false when accessibility is disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )
        assertFalse(state.isMinimallyConfigured)
    }

    @Test
    fun `isMinimallyConfigured returns false when overlay is disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = false,
                deviceAdminEnabled = false,
            )
        assertFalse(state.isMinimallyConfigured)
    }

    @Test
    fun `isMinimallyConfigured returns false when both accessibility and overlay are disabled`() {
        val state =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = false,
                deviceAdminEnabled = true,
            )
        assertFalse(state.isMinimallyConfigured)
    }
}
