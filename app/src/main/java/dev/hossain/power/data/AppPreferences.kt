package dev.hossain.power.data

import android.graphics.Point
import kotlinx.coroutines.flow.Flow

/**
 * Repository for storing and retrieving app preferences.
 * Handles floating button position, service state, and other user preferences.
 */
interface AppPreferences {
    /**
     * Gets the saved floating button position.
     *
     * @return The button position as a Point, or null if not yet saved
     */
    fun getFloatingButtonPosition(): Point?

    /**
     * Saves the floating button position.
     *
     * @param position The button position to save
     */
    fun saveFloatingButtonPosition(position: Point)

    /**
     * Gets whether the floating button service should be running.
     *
     * @return true if the service should be enabled, false otherwise
     */
    fun isFloatingButtonEnabled(): Boolean

    /**
     * Sets whether the floating button service should be running.
     *
     * @param enabled true to enable the service, false to disable
     */
    fun setFloatingButtonEnabled(enabled: Boolean)

    /**
     * Observes changes to the floating button enabled state.
     *
     * @return Flow that emits true when enabled, false when disabled
     */
    fun observeFloatingButtonEnabled(): Flow<Boolean>
}
