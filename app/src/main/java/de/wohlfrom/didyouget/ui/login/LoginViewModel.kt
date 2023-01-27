package de.wohlfrom.didyouget.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.LoginRepository
import de.wohlfrom.didyouget.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.login(serverUrl, username, password)

            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            } else if (result is Result.Error) {
                _loginResult.value = LoginResult(error = result.exception.message)
            }
        }
    }

    /**
     * Will check if the server url can actually be reached. This should only be done
     * on actual updates of the server url to avoid spamming the url with unnecessary requests.
     */
    fun serverDataChanged(serverUrl: String, username: String, password: String) {
        if (isServerUrlValid(serverUrl)) {
            viewModelScope.launch {
                when (canConnectToUrl(serverUrl)) {
                    is Result.Success ->
                        loginDataChanged(serverUrl, username, password)
                    else ->
                        _loginForm.value =
                            LoginFormState(serverUrlError = R.string.cant_connect_to_server_url)
                }
            }
        } else {
            loginDataChanged(serverUrl, username, password)
        }
    }

    /**
     * Will check if the login data is valid. Won't actually try to connect to the server url.
     */
    fun loginDataChanged(serverUrl: String, username: String, password: String) {
        if (!isServerUrlValid(serverUrl)) {
            _loginForm.value = LoginFormState(serverUrlError = R.string.invalid_server_url)
        } else if (username.isBlank()) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (password.isBlank()) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isServerUrlValid(serverUrl: String): Boolean {
        return serverUrl.isNotBlank() && Patterns.WEB_URL.matcher(serverUrl).matches()
    }

    private suspend fun canConnectToUrl(serverUrl: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL("$serverUrl?query=query { __typename }")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    return@withContext Result.Success(true)
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            } finally {
                urlConnection?.disconnect()
            }
            return@withContext Result.Error(Exception("Unknown error"))
        }
    }
}
