package dev.hossain.power.circuit.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope

/**
 * UI composable for the Home/Dashboard screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(screen = HomeScreen::class, scope = AppScope::class)
@Composable
fun HomeContent(
    state: HomeScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Power Button Assist") },
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

            // Header Section
            HeaderSection()

            // Permission Status Card
            PermissionStatusCard(
                permissionState = state.permissionState,
                onFixClicked = { state.eventSink(HomeScreen.Event.OpenOnboarding) },
            )

            // Floating Button Toggle Card
            FloatingButtonCard(
                isEnabled = state.isFloatingButtonEnabled,
                canEnable = state.permissionState.isMinimallyConfigured,
                onToggle = { state.eventSink(HomeScreen.Event.ToggleFloatingButton) },
            )

            // Quick Actions Section
            QuickActionsSection(
                onTestPowerPanel = { state.eventSink(HomeScreen.Event.OpenPowerPanel) },
                onOpenSettings = { state.eventSink(HomeScreen.Event.OpenSettings) },
            )

            // Footer
            FooterSection()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "⚡ Power Button Assist",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Software power button replacement",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PermissionStatusCard(
    permissionState: dev.hossain.power.data.PermissionState,
    onFixClicked: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (permissionState.isMinimallyConfigured) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Permission Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (permissionState.isMinimallyConfigured) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Permission indicators
            PermissionIndicator(
                text = "Accessibility Service",
                isEnabled = permissionState.accessibilityEnabled,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PermissionIndicator(
                text = "Overlay Permission",
                isEnabled = permissionState.overlayEnabled,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PermissionIndicator(
                text = "Device Admin (Optional)",
                isEnabled = permissionState.deviceAdminEnabled,
            )

            // Fix button if not configured
            if (!permissionState.isMinimallyConfigured) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onFixClicked,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fix Permissions")
                }
            }
        }
    }
}

@Composable
private fun PermissionIndicator(
    text: String,
    isEnabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint =
                if (isEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (isEnabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}

@Composable
private fun FloatingButtonCard(
    isEnabled: Boolean,
    canEnable: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = "Floating Power Button",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            supportingContent = {
                Text(
                    text =
                        if (canEnable) {
                            if (isEnabled) "Button is visible" else "Button is hidden"
                        } else {
                            "Enable permissions first"
                        },
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            trailingContent = {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { if (canEnable) onToggle() },
                    enabled = canEnable,
                )
            },
            colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        )
    }
}

@Composable
private fun QuickActionsSection(
    onTestPowerPanel: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onTestPowerPanel,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Test Power Panel")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Settings")
            }
        }
    }
}

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { /* TODO: Navigate to about screen */ }) {
            Text(
                text = "About & Limitations",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
