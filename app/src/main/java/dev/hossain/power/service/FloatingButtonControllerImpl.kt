package dev.hossain.power.service

import android.content.Context
import android.content.Intent
import dev.hossain.power.data.AppPreferences
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Implementation of [FloatingButtonController] that manages the [FloatingButtonService] lifecycle.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class FloatingButtonControllerImpl
    constructor(
        @ApplicationContext private val context: Context,
        private val appPreferences: AppPreferences,
    ) : FloatingButtonController {
        override fun startService() {
            val intent = Intent(context, FloatingButtonService::class.java)
            context.startForegroundService(intent)
        }

        override fun stopService() {
            val intent = Intent(context, FloatingButtonService::class.java)
            context.stopService(intent)
        }

        override fun isServiceRunning(): Boolean {
            // Check the preference state instead of using deprecated getRunningServices API
            // The service state is managed via AppPreferences when started/stopped
            return appPreferences.isFloatingButtonEnabled()
        }
    }
