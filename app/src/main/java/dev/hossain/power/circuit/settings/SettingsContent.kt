package dev.hossain.power.circuit.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.power.data.ButtonSize
import dev.hossain.power.data.LongPressAction
import dev.hossain.power.data.PermissionState
import dev.zacsweers.metro.AppScope

/**
 * UI composable for the Settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(screen = SettingsScreen::class, scope = AppScope::class)
@Composable
fun SettingsContent(
    state: SettingsScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(SettingsScreen.Event.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Permissions Section
            PermissionsSection(
                permissionState = state.permissionState,
                onOpenAccessibility = { state.eventSink(SettingsScreen.Event.OpenAccessibilitySettings) },
                onOpenOverlay = { state.eventSink(SettingsScreen.Event.OpenOverlaySettings) },
                onOpenDeviceAdmin = { state.eventSink(SettingsScreen.Event.OpenDeviceAdminSettings) },
            )

            // Customization Section
            CustomizationSection(
                buttonSize = state.buttonSize,
                longPressAction = state.longPressAction,
                onButtonSizeChange = { state.eventSink(SettingsScreen.Event.SetButtonSize(it)) },
                onLongPressActionChange = { state.eventSink(SettingsScreen.Event.SetLongPressAction(it)) },
            )

            // About Section
            AboutSection(
                onOpenAbout = { state.eventSink(SettingsScreen.Event.OpenAbout) },
                onOpenPrivacyPolicy = { state.eventSink(SettingsScreen.Event.OpenPrivacyPolicy) },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PermissionsSection(
    permissionState: PermissionState,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onOpenDeviceAdmin: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Text(
                text = "Permissions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            PermissionItem(
                title = "Accessibility Service",
                isEnabled = permissionState.accessibilityEnabled,
                onClick = onOpenAccessibility,
            )

            HorizontalDivider()

            PermissionItem(
                title = "Overlay Permission",
                isEnabled = permissionState.overlayEnabled,
                onClick = onOpenOverlay,
            )

            HorizontalDivider()

            PermissionItem(
                title = "Device Admin",
                subtitle = "Optional",
                isEnabled = permissionState.deviceAdminEnabled,
                onClick = onOpenDeviceAdmin,
            )
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null,
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent =
            subtitle?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
        leadingContent = {
            Icon(
                imageVector =
                    if (isEnabled) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Warning
                    },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint =
                    if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
            )
        },
        trailingContent = {
            if (!isEnabled) {
                TextButton(onClick = onClick) {
                    Text("Enable")
                }
            } else {
                Text(
                    text = "Enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

@Composable
private fun CustomizationSection(
    buttonSize: ButtonSize,
    longPressAction: LongPressAction,
    onButtonSizeChange: (ButtonSize) -> Unit,
    onLongPressActionChange: (LongPressAction) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Customization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            // Button Size Selector
            Text(
                text = "Button Size",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ButtonSize.entries.forEachIndexed { index, size ->
                    SegmentedButton(
                        selected = buttonSize == size,
                        onClick = { onButtonSizeChange(size) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = ButtonSize.entries.size),
                    ) {
                        Text(
                            text =
                                when (size) {
                                    ButtonSize.SMALL -> "Small"
                                    ButtonSize.MEDIUM -> "Medium"
                                    ButtonSize.LARGE -> "Large"
                                },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Long Press Action Selector
            Text(
                text = "Long Press Action",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Column(
                modifier = Modifier.selectableGroup(),
            ) {
                LongPressAction.entries.forEach { action ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = longPressAction == action,
                                    onClick = { onLongPressActionChange(action) },
                                    role = Role.RadioButton,
                                ).padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = longPressAction == action,
                            onClick = null,
                        )
                        Text(
                            text =
                                when (action) {
                                    LongPressAction.LOCK_SCREEN -> "Lock Screen"
                                    LongPressAction.SCREEN_OFF -> "Screen Off"
                                    LongPressAction.OPEN_PANEL -> "Open Power Panel"
                                },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSection(
    onOpenAbout: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = {
                    Text("About & Limitations")
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                modifier =
                    Modifier.selectable(
                        selected = false,
                        onClick = onOpenAbout,
                    ),
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text("Privacy Policy")
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                modifier =
                    Modifier.selectable(
                        selected = false,
                        onClick = onOpenPrivacyPolicy,
                    ),
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text("App Version")
                },
                supportingContent = {
                    Text(
                        text = "v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
        }
    }
}
