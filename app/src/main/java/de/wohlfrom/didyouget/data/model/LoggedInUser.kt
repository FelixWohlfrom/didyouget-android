package de.wohlfrom.didyouget.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val serverUrl: String,
    val username: String,
    val token: String
)
