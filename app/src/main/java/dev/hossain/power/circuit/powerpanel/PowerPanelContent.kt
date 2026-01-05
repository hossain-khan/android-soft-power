package dev.hossain.power.circuit.powerpanel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope

/**
 * UI composable for the Quick Power Panel bottom sheet.
 * Displays power-related actions in a Material 3 bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(screen = PowerPanelScreen::class, scope = AppScope::class)
@Composable
fun PowerPanelContent(
    state: PowerPanelScreen.State,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = { state.eventSink(PowerPanelScreen.Event.Dismiss) },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
        ) {
            // Header Section
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Quick Power Menu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Software power button",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions List
            state.actions.forEach { action ->
                PowerActionItem(
                    action = action,
                    onClick = {
                        if (action.enabled) {
                            handleActionClick(action.id, state.eventSink)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Individual power action item in the list.
 */
@Composable
private fun PowerActionItem(
    action: PowerAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (action.enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
            )
        },
        supportingContent =
            action.subtitle?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            if (action.enabled) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            },
                    )
                }
            },
        leadingContent = {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint =
                    if (action.enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
            )
        },
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = action.enabled) { onClick() },
        colors =
            ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    )
}

/**
 * Helper function to map action IDs to events.
 */
private fun handleActionClick(
    actionId: String,
    eventSink: (PowerPanelScreen.Event) -> Unit,
) {
    when (actionId) {
        "lock_screen" -> eventSink(PowerPanelScreen.Event.LockScreen)
        "screen_off" -> eventSink(PowerPanelScreen.Event.TurnScreenOff)
        "power_settings" -> eventSink(PowerPanelScreen.Event.OpenPowerSettings)
        "emergency_call" -> eventSink(PowerPanelScreen.Event.EmergencyCall)
        "about" -> eventSink(PowerPanelScreen.Event.OpenAbout)
    }
}
