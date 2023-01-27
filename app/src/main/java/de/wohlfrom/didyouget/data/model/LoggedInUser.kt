package de.wohlfrom.didyouget.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val displayName: String,
    val password: String,
    val token: String
)
