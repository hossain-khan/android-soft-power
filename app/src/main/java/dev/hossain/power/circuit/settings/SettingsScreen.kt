package dev.hossain.power.circuit.settings

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
import dev.hossain.power.data.AppPreferences
import dev.hossain.power.data.ButtonSize
import dev.hossain.power.data.LongPressAction
import dev.hossain.power.data.PermissionRepository
import dev.hossain.power.data.PermissionState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Circuit screen for app settings and customization.
 */
@Parcelize
data object SettingsScreen : Screen {
    data class State(
        val permissionState: PermissionState,
        val buttonSize: ButtonSize,
        val longPressAction: LongPressAction,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object NavigateBack : Event

        data object OpenAccessibilitySettings : Event

        data object OpenOverlaySettings : Event

        data object OpenDeviceAdminSettings : Event

        data class SetButtonSize(
            val size: ButtonSize,
        ) : Event

        data class SetLongPressAction(
            val action: LongPressAction,
        ) : Event

        data object OpenAbout : Event

        data object OpenPrivacyPolicy : Event
    }
}

/**
 * Presenter for the Settings screen.
 * Manages permission state observation and preference updates.
 */
@AssistedInject
class SettingsPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        private val permissionRepository: PermissionRepository,
        private val appPreferences: AppPreferences,
    ) : Presenter<SettingsScreen.State> {
        @Composable
        override fun present(): SettingsScreen.State {
            var permissionState by remember { mutableStateOf(permissionRepository.getPermissionState()) }
            var buttonSize by remember { mutableStateOf(appPreferences.getButtonSize()) }
            var longPressAction by remember { mutableStateOf(appPreferences.getLongPressAction()) }

            // Observe all state changes in a single coroutine scope
            LaunchedEffect(Unit) {
                // Observe permission state changes
                launch {
                    permissionRepository.observePermissionState().collect { newState ->
                        permissionState = newState
                    }
                }

                // Observe button size changes
                launch {
                    appPreferences.observeButtonSize().collect { newSize ->
                        buttonSize = newSize
                    }
                }

                // Observe long press action changes
                launch {
                    appPreferences.observeLongPressAction().collect { newAction ->
                        longPressAction = newAction
                    }
                }
            }

            return SettingsScreen.State(
                permissionState = permissionState,
                buttonSize = buttonSize,
                longPressAction = longPressAction,
            ) { event ->
                when (event) {
                    SettingsScreen.Event.NavigateBack -> {
                        navigator.pop()
                    }

                    SettingsScreen.Event.OpenAccessibilitySettings -> {
                        permissionRepository.openAccessibilitySettings()
                    }

                    SettingsScreen.Event.OpenOverlaySettings -> {
                        permissionRepository.openOverlaySettings()
                    }

                    SettingsScreen.Event.OpenDeviceAdminSettings -> {
                        permissionRepository.requestDeviceAdmin()
                    }

                    is SettingsScreen.Event.SetButtonSize -> {
                        appPreferences.setButtonSize(event.size)
                    }

                    is SettingsScreen.Event.SetLongPressAction -> {
                        appPreferences.setLongPressAction(event.action)
                    }

                    SettingsScreen.Event.OpenAbout -> {
                        // TODO: Navigate to about screen when implemented
                    }

                    SettingsScreen.Event.OpenPrivacyPolicy -> {
                        // TODO: Open privacy policy URL
                    }
                }
            }
        }

        @CircuitInject(SettingsScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): SettingsPresenter
        }
    }
