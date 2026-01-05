package dev.hossain.power.circuit.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.power.data.PermissionRepository
import dev.hossain.power.data.PermissionState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.parcelize.Parcelize

/**
 * Circuit screen for the onboarding wizard that guides users through permission setup.
 */
@Parcelize
data object OnboardingScreen : Screen {
    data class State(
        val currentStep: OnboardingStep,
        val permissionState: PermissionState,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object NextStep : Event

        data object EnableAccessibility : Event

        data object EnableOverlay : Event

        data object EnableDeviceAdmin : Event

        data object SkipDeviceAdmin : Event

        data object Complete : Event
    }
}

/**
 * Represents the current step in the onboarding wizard.
 */
enum class OnboardingStep {
    WELCOME,
    ACCESSIBILITY,
    OVERLAY,
    DEVICE_ADMIN,
    COMPLETE,
}

/**
 * Presenter for the onboarding screen.
 * Manages permission state observation and auto-advances through wizard steps.
 */
@AssistedInject
class OnboardingPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        private val permissionRepository: PermissionRepository,
    ) : Presenter<OnboardingScreen.State> {
        @Composable
        override fun present(): OnboardingScreen.State {
            var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
            var permissionState by remember { mutableStateOf(permissionRepository.getPermissionState()) }

            // Observe permission state changes and auto-advance
            LaunchedEffect(Unit) {
                permissionRepository.observePermissionState().collect { newState ->
                    permissionState = newState

                    // Auto-advance logic based on permission state
                    when (currentStep) {
                        OnboardingStep.ACCESSIBILITY -> {
                            if (newState.accessibilityEnabled) {
                                currentStep = OnboardingStep.OVERLAY
                            }
                        }

                        OnboardingStep.OVERLAY -> {
                            if (newState.overlayEnabled) {
                                currentStep = OnboardingStep.DEVICE_ADMIN
                            }
                        }

                        OnboardingStep.DEVICE_ADMIN -> {
                            if (newState.deviceAdminEnabled) {
                                currentStep = OnboardingStep.COMPLETE
                            }
                        }

                        else -> {
                            // No auto-advance for WELCOME and COMPLETE
                        }
                    }
                }
            }

            return OnboardingScreen.State(
                currentStep = currentStep,
                permissionState = permissionState,
            ) { event ->
                when (event) {
                    OnboardingScreen.Event.NextStep -> {
                        currentStep =
                            when (currentStep) {
                                OnboardingStep.WELCOME -> OnboardingStep.ACCESSIBILITY
                                OnboardingStep.ACCESSIBILITY -> OnboardingStep.OVERLAY
                                OnboardingStep.OVERLAY -> OnboardingStep.DEVICE_ADMIN
                                OnboardingStep.DEVICE_ADMIN -> OnboardingStep.COMPLETE
                                OnboardingStep.COMPLETE -> OnboardingStep.COMPLETE
                            }
                    }

                    OnboardingScreen.Event.EnableAccessibility -> {
                        permissionRepository.openAccessibilitySettings()
                    }

                    OnboardingScreen.Event.EnableOverlay -> {
                        permissionRepository.openOverlaySettings()
                    }

                    OnboardingScreen.Event.EnableDeviceAdmin -> {
                        permissionRepository.requestDeviceAdmin()
                    }

                    OnboardingScreen.Event.SkipDeviceAdmin -> {
                        currentStep = OnboardingStep.COMPLETE
                    }

                    OnboardingScreen.Event.Complete -> {
                        // Navigate back or to home screen
                        navigator.pop()
                    }
                }
            }
        }

        @CircuitInject(OnboardingScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): OnboardingPresenter
        }
    }
