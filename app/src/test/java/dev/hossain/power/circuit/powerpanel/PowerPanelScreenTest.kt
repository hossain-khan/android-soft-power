package dev.hossain.power.circuit.powerpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PowerPanelScreen] state, events, and actions.
 */
class PowerPanelScreenTest {
    @Test
    fun `PowerPanelScreen State has actions list`() {
        val actions =
            listOf(
                PowerAction(
                    id = "lock_screen",
                    icon = Icons.Default.Lock,
                    title = "Lock screen",
                    subtitle = "Instantly lock your device",
                    enabled = true,
                ),
            )

        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = actions,
            ) { event ->
                eventReceived = event
            }

        assertEquals(1, state.actions.size)
        assertEquals("lock_screen", state.actions[0].id)
        assertTrue(state.actions[0].enabled)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives LockScreen event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.LockScreen)
        assertEquals(PowerPanelScreen.Event.LockScreen, eventReceived)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives TurnScreenOff event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.TurnScreenOff)
        assertEquals(PowerPanelScreen.Event.TurnScreenOff, eventReceived)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives OpenPowerSettings event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.OpenPowerSettings)
        assertEquals(PowerPanelScreen.Event.OpenPowerSettings, eventReceived)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives EmergencyCall event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.EmergencyCall)
        assertEquals(PowerPanelScreen.Event.EmergencyCall, eventReceived)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives OpenAbout event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.OpenAbout)
        assertEquals(PowerPanelScreen.Event.OpenAbout, eventReceived)
    }

    @Test
    fun `PowerPanelScreen State eventSink receives Dismiss event`() {
        var eventReceived: PowerPanelScreen.Event? = null
        val state =
            PowerPanelScreen.State(
                actions = emptyList(),
            ) { event ->
                eventReceived = event
            }

        state.eventSink(PowerPanelScreen.Event.Dismiss)
        assertEquals(PowerPanelScreen.Event.Dismiss, eventReceived)
    }

    @Test
    fun `PowerAction is created with all properties`() {
        val action =
            PowerAction(
                id = "test_action",
                icon = Icons.Default.Lock,
                title = "Test Action",
                subtitle = "Test subtitle",
                enabled = true,
            )

        assertEquals("test_action", action.id)
        assertEquals(Icons.Default.Lock, action.icon)
        assertEquals("Test Action", action.title)
        assertEquals("Test subtitle", action.subtitle)
        assertTrue(action.enabled)
    }

    @Test
    fun `PowerAction can be created without subtitle`() {
        val action =
            PowerAction(
                id = "test_action",
                icon = Icons.Default.Lock,
                title = "Test Action",
                enabled = false,
            )

        assertEquals("test_action", action.id)
        assertEquals(Icons.Default.Lock, action.icon)
        assertEquals("Test Action", action.title)
        assertEquals(null, action.subtitle)
        assertFalse(action.enabled)
    }

    @Test
    fun `PowerAction has correct default enabled value`() {
        val action =
            PowerAction(
                id = "test_action",
                icon = Icons.Default.Info,
                title = "Test Action",
            )

        assertTrue(action.enabled)
    }

    @Test
    fun `All power actions have unique IDs`() {
        val actions =
            listOf(
                PowerAction(
                    id = "lock_screen",
                    icon = Icons.Default.Lock,
                    title = "Lock screen",
                ),
                PowerAction(
                    id = "screen_off",
                    icon = Icons.Default.PowerSettingsNew,
                    title = "Turn screen off",
                ),
                PowerAction(
                    id = "power_settings",
                    icon = Icons.Default.Settings,
                    title = "Power settings",
                ),
                PowerAction(
                    id = "emergency_call",
                    icon = Icons.Default.Phone,
                    title = "Emergency call",
                ),
                PowerAction(
                    id = "about",
                    icon = Icons.Default.Info,
                    title = "About",
                ),
            )

        val ids = actions.map { it.id }
        val uniqueIds = ids.toSet()

        assertEquals(5, ids.size)
        assertEquals(5, uniqueIds.size)
        assertTrue(uniqueIds.contains("lock_screen"))
        assertTrue(uniqueIds.contains("screen_off"))
        assertTrue(uniqueIds.contains("power_settings"))
        assertTrue(uniqueIds.contains("emergency_call"))
        assertTrue(uniqueIds.contains("about"))
    }

    @Test
    fun `PowerAction icons are not null`() {
        val actions =
            listOf(
                PowerAction(
                    id = "lock_screen",
                    icon = Icons.Default.Lock,
                    title = "Lock screen",
                ),
                PowerAction(
                    id = "screen_off",
                    icon = Icons.Default.PowerSettingsNew,
                    title = "Turn screen off",
                ),
                PowerAction(
                    id = "power_settings",
                    icon = Icons.Default.Settings,
                    title = "Power settings",
                ),
                PowerAction(
                    id = "emergency_call",
                    icon = Icons.Default.Phone,
                    title = "Emergency call",
                ),
                PowerAction(
                    id = "about",
                    icon = Icons.Default.Info,
                    title = "About",
                ),
            )

        actions.forEach { action ->
            assertNotNull(action.icon)
        }
    }
}
