package dev.hossain.power.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Manager for providing haptic feedback throughout the app.
 *
 * Handles different haptic feedback intensities and respects system settings.
 */
interface HapticFeedbackManager {
    /**
     * Provides a light haptic feedback for tap actions.
     */
    fun performTapFeedback()

    /**
     * Provides a stronger haptic feedback for long press actions.
     */
    fun performLongPressFeedback()

    /**
     * Provides a success haptic feedback pattern.
     */
    fun performSuccessFeedback()

    /**
     * Provides an error haptic feedback pattern.
     */
    fun performErrorFeedback()
}

/**
 * Implementation of [HapticFeedbackManager] using Android Vibrator API.
 *
 * Handles API level differences and gracefully degrades on devices without vibrator.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class HapticFeedbackManagerImpl
    constructor(
        @ApplicationContext private val context: Context,
    ) : HapticFeedbackManager {
        companion object {
            private const val TAG = "HapticFeedbackManager"
            private const val TAP_DURATION = 10L
            private const val LONG_PRESS_DURATION = 50L
            private const val SUCCESS_DURATION = 20L
            private const val ERROR_DURATION = 100L
        }

        private val vibrator: Vibrator? by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        }

        override fun performTapFeedback() {
            performVibration(TAP_DURATION, VibrationEffect.EFFECT_CLICK)
        }

        override fun performLongPressFeedback() {
            performVibration(LONG_PRESS_DURATION, VibrationEffect.EFFECT_HEAVY_CLICK)
        }

        override fun performSuccessFeedback() {
            performVibration(SUCCESS_DURATION, VibrationEffect.EFFECT_CLICK)
        }

        override fun performErrorFeedback() {
            // Double vibration pattern for error
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, ERROR_DURATION, 100, ERROR_DURATION)
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                try {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                } catch (e: Exception) {
                    Log.e(TAG, "Error performing error feedback", e)
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(ERROR_DURATION)
            }
        }

        /**
         * Performs vibration with the specified duration and effect.
         *
         * @param duration Duration in milliseconds for pre-O devices
         * @param effect Predefined effect constant for O+ devices
         */
        private fun performVibration(
            duration: Long,
            effect: Int,
        ) {
            try {
                val vib = vibrator
                if (vib == null || !vib.hasVibrator()) {
                    Log.d(TAG, "Vibrator not available on this device")
                    return
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Use predefined effects on Android Q+
                    vib.vibrate(VibrationEffect.createPredefined(effect))
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Use one-shot vibration on Android O+
                    vib.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    // Fallback for older versions
                    @Suppress("DEPRECATION")
                    vib.vibrate(duration)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error performing haptic feedback", e)
            }
        }
    }
