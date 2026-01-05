package dev.hossain.power.service

import android.content.Context
import android.content.Intent
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [PowerActionExecutor].
 *
 * Tests verify the executor correctly delegates to accessibility service
 * and device admin, with proper fallback behavior.
 */
class PowerActionExecutorTest {
    private lateinit var context: Context
    private lateinit var lockScreenManager: LockScreenManager
    private lateinit var powerActionExecutor: PowerActionExecutorImpl

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        lockScreenManager = mockk(relaxed = true)
        powerActionExecutor = PowerActionExecutorImpl(context, lockScreenManager)

        // Mock PowerAccessibilityService static getInstance
        mockkObject(PowerAccessibilityService)
        mockkStatic(Intent::class)
        mockkStatic(android.util.Log::class)
        
        // Mock Log methods
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        every { android.util.Log.e(any(), any<String>()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkObject(PowerAccessibilityService)
        unmockkStatic(Intent::class)
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `PowerActionExecutor interface exists`() {
        // Given: PowerActionExecutor interface
        // When: Checking interface structure
        // Then: Interface should be accessible
        assertThat(PowerActionExecutor::class.java.isInterface).isTrue()
    }

    @Test
    fun `PowerActionExecutorImpl class exists and implements PowerActionExecutor`() {
        // Given: PowerActionExecutorImpl class
        // When: Checking class structure
        val interfaces = PowerActionExecutorImpl::class.java.interfaces

        // Then: Should implement PowerActionExecutor interface
        assertThat(interfaces.any { it == PowerActionExecutor::class.java }).isTrue()
    }

    @Test
    fun `lockScreen uses accessibility when available`() =
        runTest {
            // Given: Accessibility service is available and working
            val mockService = mockk<PowerAccessibilityService>(relaxed = true)
            every { PowerAccessibilityService.getInstance() } returns mockService
            every { mockService.performLockScreen() } returns true

            // When: Locking screen
            val result = powerActionExecutor.lockScreen()

            // Then: Should succeed using accessibility service
            assertThat(result.isSuccess).isTrue()
            verify { mockService.performLockScreen() }
        }

    @Test
    fun `lockScreen falls back to device admin`() =
        runTest {
            // Given: Accessibility service is not available
            every { PowerAccessibilityService.getInstance() } returns null
            every { lockScreenManager.lockScreen() } returns true

            // When: Locking screen
            val result = powerActionExecutor.lockScreen()

            // Then: Should succeed using device admin
            assertThat(result.isSuccess).isTrue()
            verify { lockScreenManager.lockScreen() }
        }

    @Test
    fun `lockScreen falls back when accessibility fails`() =
        runTest {
            // Given: Accessibility service fails
            val mockService = mockk<PowerAccessibilityService>(relaxed = true)
            every { PowerAccessibilityService.getInstance() } returns mockService
            every { mockService.performLockScreen() } returns false
            every { lockScreenManager.lockScreen() } returns true

            // When: Locking screen
            val result = powerActionExecutor.lockScreen()

            // Then: Should succeed using device admin fallback
            assertThat(result.isSuccess).isTrue()
            verify { mockService.performLockScreen() }
            verify { lockScreenManager.lockScreen() }
        }

    @Test
    fun `returns failure when no lock method available`() =
        runTest {
            // Given: Neither accessibility nor device admin are available
            every { PowerAccessibilityService.getInstance() } returns null
            every { lockScreenManager.lockScreen() } returns false

            // When: Locking screen
            val result = powerActionExecutor.lockScreen()

            // Then: Should fail with appropriate error
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
            assertThat(result.exceptionOrNull()?.message).contains("Cannot lock screen")
        }

    @Test
    fun `turnScreenOff requires accessibility service`() =
        runTest {
            // Given: Accessibility service is available
            val mockService = mockk<PowerAccessibilityService>(relaxed = true)
            every { PowerAccessibilityService.getInstance() } returns mockService
            every { mockService.performLockScreen() } returns true

            // When: Turning screen off
            val result = powerActionExecutor.turnScreenOff()

            // Then: Should succeed
            assertThat(result.isSuccess).isTrue()
            verify { mockService.performLockScreen() }
        }

    @Test
    fun `turnScreenOff fails when accessibility not available`() =
        runTest {
            // Given: Accessibility service is not available
            every { PowerAccessibilityService.getInstance() } returns null

            // When: Turning screen off
            val result = powerActionExecutor.turnScreenOff()

            // Then: Should fail
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
            assertThat(result.exceptionOrNull()?.message).contains("Accessibility service is not running")
        }

    @Test
    fun `turnScreenOff fails when accessibility action fails`() =
        runTest {
            // Given: Accessibility service is available but action fails
            val mockService = mockk<PowerAccessibilityService>(relaxed = true)
            every { PowerAccessibilityService.getInstance() } returns mockService
            every { mockService.performLockScreen() } returns false

            // When: Turning screen off
            val result = powerActionExecutor.turnScreenOff()

            // Then: Should fail
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("Failed to turn screen off")
        }
}
