package dev.hossain.power.circuit.about

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.power.R
import dev.zacsweers.metro.AppScope

/**
 * UI composable for the About & Limitations screen.
 * Displays honest information about app capabilities and limitations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(screen = AboutScreen::class, scope = AppScope::class)
@Composable
fun AboutContent(
    state: AboutScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(AboutScreen.Event.NavigateBack) }) {
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

            // App Info Section
            AppInfoSection(state.appVersion)

            // What This App Can Do Section
            WhatCanDoSection()

            // What This App Cannot Do Section (CRITICAL for compliance)
            WhatCannotDoSection()

            // Privacy Section
            PrivacySection()

            // Links Section
            LinksSection(eventSink = state.eventSink)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppInfoSection(appVersion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "⚡",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.semantics { contentDescription = "Power icon" },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.about_app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "v$appVersion",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun WhatCanDoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.about_what_can_do_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            CapabilityItem(
                text = stringResource(R.string.about_can_lock_screen),
                canDo = true,
            )
            CapabilityItem(
                text = stringResource(R.string.about_can_screen_off),
                canDo = true,
            )
            CapabilityItem(
                text = stringResource(R.string.about_can_open_settings),
                canDo = true,
            )
            CapabilityItem(
                text = stringResource(R.string.about_can_emergency_call),
                canDo = true,
            )
            CapabilityItem(
                text = stringResource(R.string.about_can_floating_button),
                canDo = true,
            )
        }
    }
}

@Composable
private fun WhatCannotDoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.about_what_cannot_do_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.height(12.dp))

            CapabilityItem(
                text = stringResource(R.string.about_cannot_power_menu),
                canDo = false,
            )
            CapabilityItem(
                text = stringResource(R.string.about_cannot_shutdown),
                canDo = false,
            )
            CapabilityItem(
                text = stringResource(R.string.about_cannot_restart),
                canDo = false,
            )
            CapabilityItem(
                text = stringResource(R.string.about_cannot_power_controls),
                canDo = false,
            )

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.about_android_limitation),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun CapabilityItem(
    text: String,
    canDo: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (canDo) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint =
                if (canDo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (canDo) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}

@Composable
private fun PrivacySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = stringResource(R.string.about_privacy_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            PrivacyItem(text = stringResource(R.string.about_privacy_no_data))
            PrivacyItem(text = stringResource(R.string.about_privacy_no_screen_reading))
            PrivacyItem(text = stringResource(R.string.about_privacy_no_tracking))
            PrivacyItem(text = stringResource(R.string.about_privacy_accessibility))
        }
    }
}

@Composable
private fun PrivacyItem(text: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.semantics { contentDescription = "" },
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun LinksSection(eventSink: (AboutScreen.Event) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.about_links_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.about_privacy_policy))
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
                        onClick = { eventSink(AboutScreen.Event.OpenPrivacyPolicy) },
                    ),
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.about_source_code))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                modifier =
                    Modifier.selectable(
                        selected = false,
                        onClick = { eventSink(AboutScreen.Event.OpenSourceCode) },
                    ),
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.about_contact_support))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                modifier =
                    Modifier.selectable(
                        selected = false,
                        onClick = { eventSink(AboutScreen.Event.ContactSupport) },
                    ),
            )
        }
    }
}
