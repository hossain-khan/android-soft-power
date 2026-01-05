package dev.hossain.power.circuit.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [OnboardingStep] enum.
 */
class OnboardingStepTest {
    @Test
    fun `OnboardingStep has correct number of steps`() {
        val steps = OnboardingStep.values()
        assertEquals(5, steps.size)
    }

    @Test
    fun `OnboardingStep values are in correct order`() {
        val steps = OnboardingStep.values()
        assertEquals(OnboardingStep.WELCOME, steps[0])
        assertEquals(OnboardingStep.ACCESSIBILITY, steps[1])
        assertEquals(OnboardingStep.OVERLAY, steps[2])
        assertEquals(OnboardingStep.DEVICE_ADMIN, steps[3])
        assertEquals(OnboardingStep.COMPLETE, steps[4])
    }
}
