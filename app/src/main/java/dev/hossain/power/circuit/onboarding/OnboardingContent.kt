package dev.hossain.power.circuit.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope

/**
 * UI composable for the onboarding screen wizard.
 */
@CircuitInject(screen = OnboardingScreen::class, scope = AppScope::class)
@Composable
fun OnboardingContent(
    state: OnboardingScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Progress indicator at the top
            val progress =
                when (state.currentStep) {
                    OnboardingStep.WELCOME -> 0.0f
                    OnboardingStep.ACCESSIBILITY -> 0.25f
                    OnboardingStep.OVERLAY -> 0.5f
                    OnboardingStep.DEVICE_ADMIN -> 0.75f
                    OnboardingStep.COMPLETE -> 1.0f
                }

            if (state.currentStep != OnboardingStep.COMPLETE) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Step content
            when (state.currentStep) {
                OnboardingStep.WELCOME -> {
                    WelcomeStep(onGetStarted = { state.eventSink(OnboardingScreen.Event.NextStep) })
                }

                OnboardingStep.ACCESSIBILITY -> {
                    AccessibilityStep(
                        isEnabled = state.permissionState.accessibilityEnabled,
                        onEnable = { state.eventSink(OnboardingScreen.Event.EnableAccessibility) },
                    )
                }

                OnboardingStep.OVERLAY -> {
                    OverlayStep(
                        isEnabled = state.permissionState.overlayEnabled,
                        onEnable = { state.eventSink(OnboardingScreen.Event.EnableOverlay) },
                    )
                }

                OnboardingStep.DEVICE_ADMIN -> {
                    DeviceAdminStep(
                        isEnabled = state.permissionState.deviceAdminEnabled,
                        onEnable = { state.eventSink(OnboardingScreen.Event.EnableDeviceAdmin) },
                        onSkip = { state.eventSink(OnboardingScreen.Event.SkipDeviceAdmin) },
                    )
                }

                OnboardingStep.COMPLETE -> {
                    CompleteStep(onFinish = { state.eventSink(OnboardingScreen.Event.Complete) })
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "Power Button Alternative",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "A software power button replacement for users with broken or hard-to-use physical power buttons.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Get Started")
        }
    }
}

@Composable
private fun AccessibilityStep(
    isEnabled: Boolean,
    onEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PermissionStepCard(
        title = "Enable Accessibility Service",
        description =
            "The Accessibility Service allows Power Button Assist to perform lock screen and " +
                "screen off actions when you tap the floating power button.\n\n" +
                "This permission is required for the app to function.",
        isEnabled = isEnabled,
        onEnable = onEnable,
        modifier = modifier,
    )
}

@Composable
private fun OverlayStep(
    isEnabled: Boolean,
    onEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PermissionStepCard(
        title = "Allow Display Over Other Apps",
        description =
            "This permission allows Power Button Assist to show a floating power button on top of other apps.\n\n" +
                "This is required to access the power menu from any screen.",
        isEnabled = isEnabled,
        onEnable = onEnable,
        modifier = modifier,
    )
}

@Composable
private fun DeviceAdminStep(
    isEnabled: Boolean,
    onEnable: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Enable Device Admin (Optional)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text =
                        "Device Administrator permission provides more reliable screen locking.\n\n" +
                            "This is optional - you can skip this step and use the basic locking functionality.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (isEnabled) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Enabled",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
            }
        }

        if (!isEnabled) {
            Button(
                onClick = onEnable,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Enable Device Admin")
            }

            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Skip")
            }
        } else {
            Button(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun CompleteStep(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Power Button Assist is ready to use. You can now access the power menu through the floating button.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Get Started")
        }
    }
}

@Composable
private fun PermissionStepCard(
    title: String,
    description: String,
    isEnabled: Boolean,
    onEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (isEnabled) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Enabled",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
            }
        }

        if (!isEnabled) {
            Button(
                onClick = onEnable,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Enable")
            }
        }
    }
}
