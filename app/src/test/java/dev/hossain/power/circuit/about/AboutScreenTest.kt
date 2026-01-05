package dev.hossain.power.circuit.about

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AboutScreen] state and events.
 */
class AboutScreenTest {
    @Test
    fun `AboutScreen State has correct initial values`() {
        var eventReceived: AboutScreen.Event? = null
        val state =
            AboutScreen.State(
                appVersion = "1.0.0",
            ) { event ->
                eventReceived = event
            }

        assertEquals("1.0.0", state.appVersion)
    }

    @Test
    fun `AboutScreen State eventSink receives NavigateBack event`() {
        var eventReceived: AboutScreen.Event? = null
        val state =
            AboutScreen.State(
                appVersion = "1.0.0",
            ) { event ->
                eventReceived = event
            }

        state.eventSink(AboutScreen.Event.NavigateBack)
        assertEquals(AboutScreen.Event.NavigateBack, eventReceived)
    }

    @Test
    fun `AboutScreen State eventSink receives OpenPrivacyPolicy event`() {
        var eventReceived: AboutScreen.Event? = null
        val state =
            AboutScreen.State(
                appVersion = "1.0.0",
            ) { event ->
                eventReceived = event
            }

        state.eventSink(AboutScreen.Event.OpenPrivacyPolicy)
        assertEquals(AboutScreen.Event.OpenPrivacyPolicy, eventReceived)
    }

    @Test
    fun `AboutScreen State eventSink receives OpenSourceCode event`() {
        var eventReceived: AboutScreen.Event? = null
        val state =
            AboutScreen.State(
                appVersion = "1.0.0",
            ) { event ->
                eventReceived = event
            }

        state.eventSink(AboutScreen.Event.OpenSourceCode)
        assertEquals(AboutScreen.Event.OpenSourceCode, eventReceived)
    }

    @Test
    fun `AboutScreen State eventSink receives ContactSupport event`() {
        var eventReceived: AboutScreen.Event? = null
        val state =
            AboutScreen.State(
                appVersion = "2.0.0",
            ) { event ->
                eventReceived = event
            }

        state.eventSink(AboutScreen.Event.ContactSupport)
        assertEquals(AboutScreen.Event.ContactSupport, eventReceived)
    }

    @Test
    fun `AboutScreen State reflects app version correctly`() {
        val state1 =
            AboutScreen.State(
                appVersion = "1.0.0",
            ) {}

        val state2 =
            AboutScreen.State(
                appVersion = "2.5.3",
            ) {}

        assertEquals("1.0.0", state1.appVersion)
        assertEquals("2.5.3", state2.appVersion)
    }
}
