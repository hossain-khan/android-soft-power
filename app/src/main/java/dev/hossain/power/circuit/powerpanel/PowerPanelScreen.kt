package dev.hossain.power.circuit.powerpanel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.power.data.PermissionRepository
import dev.hossain.power.di.ApplicationContext
import dev.hossain.power.service.PowerActionExecutor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Circuit screen for the Quick Power Panel bottom sheet.
 * Displays power-related actions when the floating button is tapped.
 */
@Parcelize
data object PowerPanelScreen : Screen {
    data class State(
        val actions: List<PowerAction>,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object LockScreen : Event

        data object TurnScreenOff : Event

        data object OpenPowerSettings : Event

        data object EmergencyCall : Event

        data object OpenAbout : Event

        data object Dismiss : Event
    }
}

/**
 * Represents a power action item in the Quick Power Panel.
 */
data class PowerAction(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
)

/**
 * Presenter for the Quick Power Panel screen.
 * Manages action availability based on permission state.
 */
@AssistedInject
class PowerPanelPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        @ApplicationContext private val context: Context,
        private val permissionRepository: PermissionRepository,
        private val powerActionExecutor: PowerActionExecutor,
    ) : Presenter<PowerPanelScreen.State> {
        @Composable
        override fun present(): PowerPanelScreen.State {
            var permissionState by remember { mutableStateOf(permissionRepository.getPermissionState()) }
            val coroutineScope = rememberCoroutineScope()

            // Observe permission state changes
            LaunchedEffect(Unit) {
                permissionRepository.observePermissionState().collect { newState ->
                    permissionState = newState
                }
            }

            // Determine available actions based on permissions
            val actions =
                remember(permissionState) {
                    buildList {
                        // Lock Screen action - requires either accessibility or device admin
                        add(
                            PowerAction(
                                id = "lock_screen",
                                icon = Icons.Default.Lock,
                                title = "Lock screen",
                                subtitle = "Instantly lock your device",
                                enabled = permissionState.accessibilityEnabled || permissionState.deviceAdminEnabled,
                            ),
                        )

                        // Turn Screen Off action - requires accessibility service
                        add(
                            PowerAction(
                                id = "screen_off",
                                icon = Icons.Default.PowerSettingsNew,
                                title = "Turn screen off",
                                subtitle = "Locks device if security is enabled",
                                enabled = permissionState.accessibilityEnabled,
                            ),
                        )

                        // Power Settings - always available
                        add(
                            PowerAction(
                                id = "power_settings",
                                icon = Icons.Default.Settings,
                                title = "Power & system settings",
                                subtitle = "Open system power options",
                                enabled = true,
                            ),
                        )

                        // Emergency Call - always available
                        add(
                            PowerAction(
                                id = "emergency_call",
                                icon = Icons.Default.Phone,
                                title = "Emergency call",
                                subtitle = "Open emergency dialer",
                                enabled = true,
                            ),
                        )

                        // About & Limitations - always available
                        add(
                            PowerAction(
                                id = "about",
                                icon = Icons.Default.Info,
                                title = "About & limitations",
                                subtitle = null,
                                enabled = true,
                            ),
                        )
                    }
                }

            return PowerPanelScreen.State(
                actions = actions,
            ) { event ->
                when (event) {
                    PowerPanelScreen.Event.LockScreen -> {
                        coroutineScope.launch {
                            val result = powerActionExecutor.lockScreen()
                            if (result.isSuccess) {
                                navigator.pop()
                            }
                        }
                    }

                    PowerPanelScreen.Event.TurnScreenOff -> {
                        coroutineScope.launch {
                            val result = powerActionExecutor.turnScreenOff()
                            if (result.isSuccess) {
                                navigator.pop()
                            }
                        }
                    }

                    PowerPanelScreen.Event.OpenPowerSettings -> {
                        coroutineScope.launch {
                            powerActionExecutor.openPowerSettings()
                            navigator.pop()
                        }
                    }

                    PowerPanelScreen.Event.EmergencyCall -> {
                        coroutineScope.launch {
                            powerActionExecutor.openEmergencyDialer()
                            navigator.pop()
                        }
                    }

                    PowerPanelScreen.Event.OpenAbout -> {
                        // TODO: Navigate to about screen when implemented
                        navigator.pop()
                    }

                    PowerPanelScreen.Event.Dismiss -> {
                        navigator.pop()
                    }
                }
            }
        }

        @CircuitInject(PowerPanelScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): PowerPanelPresenter
        }
    }
