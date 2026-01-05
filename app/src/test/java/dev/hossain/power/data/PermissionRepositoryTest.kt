package dev.hossain.power.data

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [PermissionRepositoryImpl].
 *
 * Tests verify that the repository correctly checks permission states
 * and provides accurate permission information.
 */
class PermissionRepositoryTest {
    private lateinit var context: Context
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var repository: PermissionRepositoryImpl

    private val testPackageName = "dev.hossain.power"

    @Before
    fun setup() {
        // Mock Android system services
        context = mockk(relaxed = true)
        accessibilityManager = mockk(relaxed = true)
        devicePolicyManager = mockk(relaxed = true)

        // Setup context to return our mocked services
        every { context.packageName } returns testPackageName
        every { context.getSystemService(Context.ACCESSIBILITY_SERVICE) } returns accessibilityManager
        every { context.getSystemService(Context.DEVICE_POLICY_SERVICE) } returns devicePolicyManager

        // Mock static methods for Android framework
        mockkStatic(Settings::class)
        mockkStatic(Log::class)
        mockkStatic(Uri::class)

        // Mock Log methods to avoid RuntimeException
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0

        repository = PermissionRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(Settings::class)
        unmockkStatic(Log::class)
        unmockkStatic(Uri::class)
    }

    @Test
    fun `permission state reflects all permissions disabled`() {
        // Given: All permissions are disabled
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns emptyList()
        every { Settings.canDrawOverlays(context) } returns false
        every { devicePolicyManager.isAdminActive(any()) } returns false

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: All permissions should be disabled
        assertThat(state.accessibilityEnabled).isFalse()
        assertThat(state.overlayEnabled).isFalse()
        assertThat(state.deviceAdminEnabled).isFalse()
        assertThat(state.isFullyConfigured).isFalse()
        assertThat(state.isMinimallyConfigured).isFalse()
    }

    @Test
    fun `permission state reflects all permissions enabled`() {
        // Given: All permissions are enabled
        val serviceInfo = mockk<AccessibilityServiceInfo>()
        every { serviceInfo.id } returns "$testPackageName/.service.PowerAccessibilityService"
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)
        every { Settings.canDrawOverlays(context) } returns true
        every { devicePolicyManager.isAdminActive(any()) } returns true

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: All permissions should be enabled
        assertThat(state.accessibilityEnabled).isTrue()
        assertThat(state.overlayEnabled).isTrue()
        assertThat(state.deviceAdminEnabled).isTrue()
        assertThat(state.isFullyConfigured).isTrue()
        assertThat(state.isMinimallyConfigured).isTrue()
    }

    @Test
    fun `isFullyConfigured returns true when all enabled`() {
        // Given: All permissions are enabled
        val serviceInfo = mockk<AccessibilityServiceInfo>()
        every { serviceInfo.id } returns "$testPackageName/.service.PowerAccessibilityService"
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)
        every { Settings.canDrawOverlays(context) } returns true
        every { devicePolicyManager.isAdminActive(any()) } returns true

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: Should be fully configured
        assertThat(state.isFullyConfigured).isTrue()
    }

    @Test
    fun `isMinimallyConfigured returns true without device admin`() {
        // Given: Accessibility and overlay are enabled, but not device admin
        val serviceInfo = mockk<AccessibilityServiceInfo>()
        every { serviceInfo.id } returns "$testPackageName/.service.PowerAccessibilityService"
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)
        every { Settings.canDrawOverlays(context) } returns true
        every { devicePolicyManager.isAdminActive(any()) } returns false

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: Should be minimally configured
        assertThat(state.isMinimallyConfigured).isTrue()
        assertThat(state.isFullyConfigured).isFalse()
        assertThat(state.accessibilityEnabled).isTrue()
        assertThat(state.overlayEnabled).isTrue()
        assertThat(state.deviceAdminEnabled).isFalse()
    }

    @Test
    fun `observePermissionState emits initial state`() =
        runTest {
            // Given: All permissions are disabled
            every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns emptyList()
            every { Settings.canDrawOverlays(context) } returns false
            every { devicePolicyManager.isAdminActive(any()) } returns false

            // When: Observing permission state
            repository.observePermissionState().test {
                // Then: Should emit initial state
                val state = awaitItem()
                assertThat(state.accessibilityEnabled).isFalse()
                assertThat(state.overlayEnabled).isFalse()
                assertThat(state.deviceAdminEnabled).isFalse()

                cancel()
            }
        }

    @Test
    fun `observePermissionState emits updates when accessibility changes`() =
        runTest {
            // Given: Initial state with all disabled
            every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns emptyList()
            every { Settings.canDrawOverlays(context) } returns false
            every { devicePolicyManager.isAdminActive(any()) } returns false

            // Capture the listener
            val listenerSlot = slot<AccessibilityManager.AccessibilityStateChangeListener>()
            every {
                accessibilityManager.addAccessibilityStateChangeListener(capture(listenerSlot))
            } returns true

            // When: Observing permission state
            repository.observePermissionState().test {
                // Then: Should emit initial state
                val initialState = awaitItem()
                assertThat(initialState.accessibilityEnabled).isFalse()

                // Simulate accessibility service being enabled
                val serviceInfo = mockk<AccessibilityServiceInfo>()
                every { serviceInfo.id } returns "$testPackageName/.service.PowerAccessibilityService"
                every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)

                // Trigger listener callback
                listenerSlot.captured.onAccessibilityStateChanged(true)

                // Should emit updated state
                val updatedState = awaitItem()
                assertThat(updatedState.accessibilityEnabled).isTrue()

                cancel()
            }
        }

    @Test
    fun `isAccessibilityServiceEnabled returns false when service not in list`() {
        // Given: Different service is enabled
        val serviceInfo = mockk<AccessibilityServiceInfo>()
        every { serviceInfo.id } returns "com.other.app/.OtherService"
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)
        every { Settings.canDrawOverlays(context) } returns false
        every { devicePolicyManager.isAdminActive(any()) } returns false

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: Accessibility should be disabled
        assertThat(state.accessibilityEnabled).isFalse()
    }

    @Test
    fun `isAccessibilityServiceEnabled returns true when service is in list`() {
        // Given: Our service is enabled
        val serviceInfo = mockk<AccessibilityServiceInfo>()
        every { serviceInfo.id } returns "$testPackageName/.service.PowerAccessibilityService"
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns listOf(serviceInfo)
        every { Settings.canDrawOverlays(context) } returns false
        every { devicePolicyManager.isAdminActive(any()) } returns false

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: Accessibility should be enabled
        assertThat(state.accessibilityEnabled).isTrue()
    }

    @Test
    fun `isDeviceAdminActive checks correct component`() {
        // Given: Device admin is being checked
        every { accessibilityManager.getEnabledAccessibilityServiceList(any()) } returns emptyList()
        every { Settings.canDrawOverlays(context) } returns false
        every { devicePolicyManager.isAdminActive(any()) } returns true

        // When: Getting permission state
        val state = repository.getPermissionState()

        // Then: Should have checked device admin status
        assertThat(state.deviceAdminEnabled).isTrue()
        verify { devicePolicyManager.isAdminActive(any()) }
    }
}
