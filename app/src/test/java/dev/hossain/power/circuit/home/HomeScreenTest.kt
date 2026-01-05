package dev.hossain.power.circuit.home

import dev.hossain.power.data.PermissionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [HomeScreen] state and events.
 */
class HomeScreenTest {
    @Test
    fun `HomeScreen State has correct initial values`() {
        var eventReceived: HomeScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )

        val state =
            HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = false,
            ) { event ->
                eventReceived = event
            }

        assertEquals(permissionState, state.permissionState)
        assertFalse(state.isFloatingButtonEnabled)
    }

    @Test
    fun `HomeScreen State eventSink receives ToggleFloatingButton event`() {
        var eventReceived: HomeScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = true,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(HomeScreen.Event.ToggleFloatingButton)
        assertEquals(HomeScreen.Event.ToggleFloatingButton, eventReceived)
    }

    @Test
    fun `HomeScreen State eventSink receives OpenSettings event`() {
        var eventReceived: HomeScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = false,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(HomeScreen.Event.OpenSettings)
        assertEquals(HomeScreen.Event.OpenSettings, eventReceived)
    }

    @Test
    fun `HomeScreen State eventSink receives OpenOnboarding event`() {
        var eventReceived: HomeScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = false,
                deviceAdminEnabled = false,
            )

        val state =
            HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = false,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(HomeScreen.Event.OpenOnboarding)
        assertEquals(HomeScreen.Event.OpenOnboarding, eventReceived)
    }

    @Test
    fun `HomeScreen State eventSink receives OpenPowerPanel event`() {
        var eventReceived: HomeScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = true,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(HomeScreen.Event.OpenPowerPanel)
        assertEquals(HomeScreen.Event.OpenPowerPanel, eventReceived)
    }

    @Test
    fun `HomeScreen State reflects permission state correctly`() {
        val fullyConfiguredPermissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val minimallyConfiguredPermissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )

        val notConfiguredPermissionState =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = false,
                deviceAdminEnabled = false,
            )

        val stateFullyConfigured =
            HomeScreen.State(
                permissionState = fullyConfiguredPermissionState,
                isFloatingButtonEnabled = true,
            ) {}

        val stateMinimallyConfigured =
            HomeScreen.State(
                permissionState = minimallyConfiguredPermissionState,
                isFloatingButtonEnabled = false,
            ) {}

        val stateNotConfigured =
            HomeScreen.State(
                permissionState = notConfiguredPermissionState,
                isFloatingButtonEnabled = false,
            ) {}

        assertTrue(stateFullyConfigured.permissionState.isFullyConfigured)
        assertTrue(stateMinimallyConfigured.permissionState.isMinimallyConfigured)
        assertFalse(stateNotConfigured.permissionState.isMinimallyConfigured)
    }
}
