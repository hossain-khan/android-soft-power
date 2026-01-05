package dev.hossain.power.service

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [PowerAccessibilityService].
 *
 * Note: These are basic structural tests for the service skeleton.
 * More comprehensive tests will be added when full functionality is implemented.
 */
class PowerAccessibilityServiceTest {
    @Test
    fun `getInstance returns null when service is not connected`() {
        // Given: No service instance is set
        // When: Getting the instance
        val instance = PowerAccessibilityService.getInstance()

        // Then: Should return null
        assertNull(instance)
    }

    @Test
    fun `service has companion object with getInstance method`() {
        // Given: PowerAccessibilityService class
        // When: Accessing getInstance method via companion object
        // Then: Method should be accessible and not throw exceptions
        assertNotNull(PowerAccessibilityService.Companion)

        // Verify getInstance can be called (even if it returns null)
        val instance = PowerAccessibilityService.getInstance()
        assertNull(instance) // Should be null since service is not running in test
    }
}
