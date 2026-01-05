package dev.hossain.power.circuit.settings

import dev.hossain.power.data.ButtonSize
import dev.hossain.power.data.LongPressAction
import dev.hossain.power.data.PermissionState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [SettingsScreen] state and events.
 */
class SettingsScreenTest {
    @Test
    fun `SettingsScreen State has correct initial values`() {
        var eventReceived: SettingsScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )

        val state =
            SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = ButtonSize.MEDIUM,
                longPressAction = LongPressAction.OPEN_PANEL,
            ) { event ->
                eventReceived = event
            }

        assertEquals(permissionState, state.permissionState)
        assertEquals(ButtonSize.MEDIUM, state.buttonSize)
        assertEquals(LongPressAction.OPEN_PANEL, state.longPressAction)
    }

    @Test
    fun `SettingsScreen State eventSink receives NavigateBack event`() {
        var eventReceived: SettingsScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = ButtonSize.LARGE,
                longPressAction = LongPressAction.LOCK_SCREEN,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(SettingsScreen.Event.NavigateBack)
        assertEquals(SettingsScreen.Event.NavigateBack, eventReceived)
    }

    @Test
    fun `SettingsScreen State eventSink receives OpenAccessibilitySettings event`() {
        var eventReceived: SettingsScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = false,
                overlayEnabled = true,
                deviceAdminEnabled = false,
            )

        val state =
            SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = ButtonSize.SMALL,
                longPressAction = LongPressAction.SCREEN_OFF,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(SettingsScreen.Event.OpenAccessibilitySettings)
        assertEquals(SettingsScreen.Event.OpenAccessibilitySettings, eventReceived)
    }

    @Test
    fun `SettingsScreen State eventSink receives SetButtonSize event`() {
        var eventReceived: SettingsScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = ButtonSize.MEDIUM,
                longPressAction = LongPressAction.OPEN_PANEL,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(SettingsScreen.Event.SetButtonSize(ButtonSize.LARGE))
        assertEquals(SettingsScreen.Event.SetButtonSize(ButtonSize.LARGE), eventReceived)
    }

    @Test
    fun `SettingsScreen State eventSink receives SetLongPressAction event`() {
        var eventReceived: SettingsScreen.Event? = null
        val permissionState =
            PermissionState(
                accessibilityEnabled = true,
                overlayEnabled = true,
                deviceAdminEnabled = true,
            )

        val state =
            SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = ButtonSize.MEDIUM,
                longPressAction = LongPressAction.OPEN_PANEL,
            ) { event ->
                eventReceived = event
            }

        state.eventSink(SettingsScreen.Event.SetLongPressAction(LongPressAction.LOCK_SCREEN))
        assertEquals(SettingsScreen.Event.SetLongPressAction(LongPressAction.LOCK_SCREEN), eventReceived)
    }

    @Test
    fun `SettingsScreen State reflects permission state correctly`() {
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

        val stateFullyConfigured =
            SettingsScreen.State(
                permissionState = fullyConfiguredPermissionState,
                buttonSize = ButtonSize.MEDIUM,
                longPressAction = LongPressAction.OPEN_PANEL,
            ) {}

        val stateMinimallyConfigured =
            SettingsScreen.State(
                permissionState = minimallyConfiguredPermissionState,
                buttonSize = ButtonSize.SMALL,
                longPressAction = LongPressAction.SCREEN_OFF,
            ) {}

        assertEquals(true, stateFullyConfigured.permissionState.isFullyConfigured)
        assertEquals(true, stateMinimallyConfigured.permissionState.isMinimallyConfigured)
        assertEquals(false, stateMinimallyConfigured.permissionState.isFullyConfigured)
    }

    @Test
    fun `ButtonSize enum has correct values`() {
        assertEquals(3, ButtonSize.entries.size)
        assertEquals(ButtonSize.SMALL, ButtonSize.valueOf("SMALL"))
        assertEquals(ButtonSize.MEDIUM, ButtonSize.valueOf("MEDIUM"))
        assertEquals(ButtonSize.LARGE, ButtonSize.valueOf("LARGE"))
    }

    @Test
    fun `LongPressAction enum has correct values`() {
        assertEquals(3, LongPressAction.entries.size)
        assertEquals(LongPressAction.LOCK_SCREEN, LongPressAction.valueOf("LOCK_SCREEN"))
        assertEquals(LongPressAction.SCREEN_OFF, LongPressAction.valueOf("SCREEN_OFF"))
        assertEquals(LongPressAction.OPEN_PANEL, LongPressAction.valueOf("OPEN_PANEL"))
    }
}
