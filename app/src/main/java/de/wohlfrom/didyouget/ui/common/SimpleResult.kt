package de.wohlfrom.didyouget.ui.common

/**
 * A simple result class that either returns success or an error with according message.
 * Usually used to signal a fragment that saving the update was successful
 * and the fragment can be closed.
 */
data class SimpleResult (
    val success: Boolean? = false, // Set to true if successful
    val error: String? = null
)
