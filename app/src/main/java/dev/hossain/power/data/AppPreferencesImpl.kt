package dev.hossain.power.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import dev.hossain.power.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of [AppPreferences] using SharedPreferences.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class AppPreferencesImpl
    constructor(
        @ApplicationContext context: Context,
    ) : AppPreferences {
        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        override fun getFloatingButtonPosition(): Point? {
            val x = prefs.getInt(KEY_BUTTON_X, Int.MIN_VALUE)
            val y = prefs.getInt(KEY_BUTTON_Y, Int.MIN_VALUE)
            return if (x != Int.MIN_VALUE && y != Int.MIN_VALUE) {
                Point(x, y)
            } else {
                null
            }
        }

        override fun saveFloatingButtonPosition(position: Point) {
            prefs
                .edit()
                .putInt(KEY_BUTTON_X, position.x)
                .putInt(KEY_BUTTON_Y, position.y)
                .apply()
        }

        override fun isFloatingButtonEnabled(): Boolean = prefs.getBoolean(KEY_BUTTON_ENABLED, false)

        override fun setFloatingButtonEnabled(enabled: Boolean) {
            prefs
                .edit()
                .putBoolean(KEY_BUTTON_ENABLED, enabled)
                .apply()
        }

        override fun observeFloatingButtonEnabled(): Flow<Boolean> =
            callbackFlow {
                // Send initial value
                trySend(isFloatingButtonEnabled())

                // Listen for changes
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key == KEY_BUTTON_ENABLED) {
                            trySend(isFloatingButtonEnabled())
                        }
                    }

                prefs.registerOnSharedPreferenceChangeListener(listener)

                awaitClose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

        override fun getButtonSize(): ButtonSize {
            val sizeName = prefs.getString(KEY_BUTTON_SIZE, ButtonSize.MEDIUM.name)
            return try {
                ButtonSize.valueOf(sizeName ?: ButtonSize.MEDIUM.name)
            } catch (e: IllegalArgumentException) {
                ButtonSize.MEDIUM
            }
        }

        override fun setButtonSize(size: ButtonSize) {
            prefs
                .edit()
                .putString(KEY_BUTTON_SIZE, size.name)
                .apply()
        }

        override fun observeButtonSize(): Flow<ButtonSize> =
            callbackFlow {
                // Send initial value
                trySend(getButtonSize())

                // Listen for changes
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key == KEY_BUTTON_SIZE) {
                            trySend(getButtonSize())
                        }
                    }

                prefs.registerOnSharedPreferenceChangeListener(listener)

                awaitClose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

        override fun getLongPressAction(): LongPressAction {
            val actionName = prefs.getString(KEY_LONG_PRESS_ACTION, LongPressAction.OPEN_PANEL.name)
            return try {
                LongPressAction.valueOf(actionName ?: LongPressAction.OPEN_PANEL.name)
            } catch (e: IllegalArgumentException) {
                LongPressAction.OPEN_PANEL
            }
        }

        override fun setLongPressAction(action: LongPressAction) {
            prefs
                .edit()
                .putString(KEY_LONG_PRESS_ACTION, action.name)
                .apply()
        }

        override fun observeLongPressAction(): Flow<LongPressAction> =
            callbackFlow {
                // Send initial value
                trySend(getLongPressAction())

                // Listen for changes
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key == KEY_LONG_PRESS_ACTION) {
                            trySend(getLongPressAction())
                        }
                    }

                prefs.registerOnSharedPreferenceChangeListener(listener)

                awaitClose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

        companion object {
            private const val PREFS_NAME = "power_app_prefs"
            private const val KEY_BUTTON_X = "floating_button_x"
            private const val KEY_BUTTON_Y = "floating_button_y"
            private const val KEY_BUTTON_ENABLED = "floating_button_enabled"
            private const val KEY_BUTTON_SIZE = "button_size"
            private const val KEY_LONG_PRESS_ACTION = "long_press_action"
        }
    }
