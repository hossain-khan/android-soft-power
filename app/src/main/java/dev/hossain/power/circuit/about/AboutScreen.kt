package dev.hossain.power.circuit.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.power.BuildConfig
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.parcelize.Parcelize

/**
 * Circuit screen for About & Limitations.
 * Provides honest, clear information about app capabilities and limitations.
 * Critical for Play Store compliance and user expectation management.
 */
@Parcelize
data object AboutScreen : Screen {
    data class State(
        val appVersion: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object NavigateBack : Event

        data object OpenPrivacyPolicy : Event

        data object OpenSourceCode : Event

        data object ContactSupport : Event
    }
}

/**
 * Presenter for the About screen.
 * Handles navigation and external link opening.
 */
@AssistedInject
class AboutPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        @ApplicationContext private val context: Context,
    ) : Presenter<AboutScreen.State> {
        companion object {
            private const val PRIVACY_POLICY_URL = "https://github.com/hossain-khan/android-soft-power/blob/main/PRIVACY.md"
            private const val SOURCE_CODE_URL = "https://github.com/hossain-khan/android-soft-power"
            private const val CONTACT_EMAIL = "mailto:hossain.khan@gmail.com?subject=Power%20Button%20Assist%20Feedback"
        }

        @Composable
        override fun present(): AboutScreen.State =
            AboutScreen.State(
                appVersion = BuildConfig.VERSION_NAME,
            ) { event ->
                when (event) {
                    AboutScreen.Event.NavigateBack -> {
                        navigator.pop()
                    }

                    AboutScreen.Event.OpenPrivacyPolicy -> {
                        openUrl(PRIVACY_POLICY_URL)
                    }

                    AboutScreen.Event.OpenSourceCode -> {
                        openUrl(SOURCE_CODE_URL)
                    }

                    AboutScreen.Event.ContactSupport -> {
                        openUrl(CONTACT_EMAIL)
                    }
                }
            }

        private fun openUrl(url: String) {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(intent)
        }

        @CircuitInject(AboutScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): AboutPresenter
        }
    }
