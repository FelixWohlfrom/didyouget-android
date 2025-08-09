package de.wohlfrom.didyouget.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import de.wohlfrom.didyouget.data.model.LoggedInUser
import de.wohlfrom.didyouget.data.sources.LoginDataSource
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.apolloClient
import androidx.core.content.edit

/**
 * The key in storage in which the currently logged in user is stored.
 */
private const val LOGGED_IN_USER_KEY = "loggedinUser"

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(val dataSource: LoginDataSource, activity: Activity) {

    private var localStore: SharedPreferences?

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
        user = null
        localStore = activity.getPreferences(Context.MODE_PRIVATE)

        if (localStore != null) {
            val userString = localStore!!.getString(LOGGED_IN_USER_KEY, null)
            if (userString != null) {
                user = Gson().fromJson(userString, LoggedInUser::class.java)
                apolloClient(user!!.serverUrl, user!!.token)
            }
        }
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(serverUrl: String, username: String, password: String):
            Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(serverUrl, username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        } else {
            if (result is Result.Error) {
                Log.e("login", result.exception.stackTraceToString())
            }
            logout()
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser

        localStore!!.edit {
            putString(LOGGED_IN_USER_KEY, Gson().toJson(loggedInUser))
        }
    }

    suspend fun checkLoggedIn(): Boolean {
        if (this.user != null) {
            val result = dataSource.checkLoggedIn()

            if (result is Result.Success) {
                return true
            }
            user = null
        }
        return false
    }
}
