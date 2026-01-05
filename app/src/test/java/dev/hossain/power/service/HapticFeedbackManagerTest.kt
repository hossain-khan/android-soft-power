package dev.hossain.power.service

import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for [HapticFeedbackManager].
 *
 * Note: These are basic structural tests for the manager interface.
 * Full functionality tests require Android framework mocking and will be added
 * as instrumented tests or with more comprehensive test infrastructure.
 */
class HapticFeedbackManagerTest {
    @Test
    fun `HapticFeedbackManager interface has required methods`() {
        // Given: HapticFeedbackManager interface
        // When: Checking interface methods
        val methods = HapticFeedbackManager::class.java.methods

        // Then: Should have all required feedback methods
        assertNotNull(methods.find { it.name == "performTapFeedback" })
        assertNotNull(methods.find { it.name == "performLongPressFeedback" })
        assertNotNull(methods.find { it.name == "performSuccessFeedback" })
        assertNotNull(methods.find { it.name == "performErrorFeedback" })
    }

    @Test
    fun `HapticFeedbackManagerImpl class exists and implements HapticFeedbackManager`() {
        // Given: HapticFeedbackManagerImpl class
        // When: Checking class structure
        val interfaces = HapticFeedbackManagerImpl::class.java.interfaces

        // Then: Should implement HapticFeedbackManager interface
        assertNotNull(interfaces.find { it == HapticFeedbackManager::class.java })
    }
}
