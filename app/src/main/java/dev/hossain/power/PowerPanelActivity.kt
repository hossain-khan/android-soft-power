package dev.hossain.power

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import dev.hossain.power.circuit.powerpanel.PowerPanelScreen
import dev.hossain.power.di.ActivityKey
import dev.hossain.power.ui.theme.PowerAppTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

/**
 * Transparent activity that displays the Power Panel bottom sheet.
 *
 * This activity is launched when the floating button is tapped. It shows the power panel
 * as a bottom sheet overlay and finishes when the panel is dismissed.
 *
 * ## Configuration
 * - Theme: Transparent background (see styles.xml)
 * - Launch mode: Single instance to avoid multiple panels
 * - Finish on touch outside: Yes (bottom sheet dismisses)
 *
 * Uses Metro DI for constructor injection with Circuit.
 */
@ActivityKey(PowerPanelActivity::class)
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
@Inject
class PowerPanelActivity
    constructor(
        private val circuit: Circuit,
    ) : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                PowerAppTheme {
                    // Create a backstack with PowerPanelScreen as root
                    val backStack = rememberSaveableBackStack(root = PowerPanelScreen)
                    val navigator = rememberCircuitNavigator(backStack)

                    // Finish activity when backstack is empty (panel dismissed)
                    LaunchedEffect(backStack.size) {
                        if (backStack.size == 0) {
                            finish()
                        }
                    }

                    CircuitCompositionLocals(circuit) {
                        ContentWithOverlays {
                            NavigableCircuitContent(
                                navigator = navigator,
                                backStack = backStack,
                            )
                        }
                    }
                }
            }
        }
    }
