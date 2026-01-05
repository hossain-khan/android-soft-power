package dev.hossain.power.circuit.home

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
import dev.hossain.power.circuit.onboarding.OnboardingScreen
import dev.hossain.power.data.AppPreferences
import dev.hossain.power.data.PermissionRepository
import dev.hossain.power.data.PermissionState
import dev.hossain.power.service.FloatingButtonController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.parcelize.Parcelize

/**
 * Circuit screen for the Home/Dashboard that shows permission status
 * and provides quick access to app features.
 */
@Parcelize
data object HomeScreen : Screen {
    data class State(
        val permissionState: PermissionState,
        val isFloatingButtonEnabled: Boolean,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object ToggleFloatingButton : Event

        data object OpenSettings : Event

        data object OpenOnboarding : Event

        data object OpenPowerPanel : Event
    }
}

/**
 * Presenter for the Home screen.
 * Manages permission state observation and floating button service state.
 */
@AssistedInject
class HomePresenter
    constructor(
        @Assisted private val navigator: Navigator,
        private val permissionRepository: PermissionRepository,
        private val appPreferences: AppPreferences,
        private val floatingButtonController: FloatingButtonController,
    ) : Presenter<HomeScreen.State> {
        @Composable
        override fun present(): HomeScreen.State {
            var permissionState by remember { mutableStateOf(permissionRepository.getPermissionState()) }
            var isFloatingButtonEnabled by remember { mutableStateOf(appPreferences.isFloatingButtonEnabled()) }

            // Observe permission state changes
            LaunchedEffect(Unit) {
                permissionRepository.observePermissionState().collect { newState ->
                    permissionState = newState
                }
            }

            // Observe floating button enabled state
            LaunchedEffect(Unit) {
                appPreferences.observeFloatingButtonEnabled().collect { enabled ->
                    isFloatingButtonEnabled = enabled
                }
            }

            return HomeScreen.State(
                permissionState = permissionState,
                isFloatingButtonEnabled = isFloatingButtonEnabled,
            ) { event ->
                when (event) {
                    HomeScreen.Event.ToggleFloatingButton -> {
                        val newState = !isFloatingButtonEnabled
                        appPreferences.setFloatingButtonEnabled(newState)
                        if (newState) {
                            floatingButtonController.startService()
                        } else {
                            floatingButtonController.stopService()
                        }
                    }

                    HomeScreen.Event.OpenSettings -> {
                        // TODO: Navigate to settings screen when implemented
                        // navigator.goTo(SettingsScreen)
                    }

                    HomeScreen.Event.OpenOnboarding -> {
                        navigator.goTo(OnboardingScreen)
                    }

                    HomeScreen.Event.OpenPowerPanel -> {
                        // TODO: Open power panel bottom sheet when implemented
                        // This will be handled via overlay
                    }
                }
            }
        }

        @CircuitInject(HomeScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): HomePresenter
        }
    }
