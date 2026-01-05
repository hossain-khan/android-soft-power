package dev.hossain.power.service

/**
 * Controller for managing the floating button service lifecycle.
 */
interface FloatingButtonController {
    /**
     * Starts the floating button service.
     * The service will display a floating power button overlay.
     */
    fun startService()

    /**
     * Stops the floating button service.
     * The floating button overlay will be removed.
     */
    fun stopService()

    /**
     * Checks if the floating button service is currently running.
     *
     * @return true if the service is running, false otherwise
     */
    fun isServiceRunning(): Boolean
}
