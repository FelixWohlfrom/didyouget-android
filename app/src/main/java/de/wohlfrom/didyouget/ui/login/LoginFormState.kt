package de.wohlfrom.didyouget.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val serverUrlError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
