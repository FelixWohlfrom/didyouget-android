package de.wohlfrom.didyouget.data.sources

import de.wohlfrom.didyouget.data.model.LoggedInUser
import de.wohlfrom.didyouget.mutations.LoginMutation
import de.wohlfrom.didyouget.queries.IsLoggedInQuery
import de.wohlfrom.didyouget.type.UserInput
import java.io.IOException

/**
 * Class that handles authentication with login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(serverUrl: String, username: String, password: String):
            Result<LoggedInUser> {
        val response = apolloClient(serverUrl).mutation(
                LoginMutation(UserInput(username = username, password = password))
            ).execute()

        if (response.exception != null) {
            return Result.Error(IOException("Error logging in", response.exception))
        }

        val token = response.data?.login?.token
            ?: return Result.Error(Exception(response.data?.login?.failureMessage))
        apolloClient(serverUrl, token)
        return Result.Success(LoggedInUser(serverUrl, username, token))
    }

    fun logout() {
        // TODO: revoke authentication
    }

    suspend fun checkLoggedIn(): Result<Boolean> {
        val response = apolloClient().query(IsLoggedInQuery()).execute()

        if (response.exception != null) {
            return Result.Error(IOException("Error while checking for user logged in",
                response.exception))
        }

        return if (response.data?.isLoggedIn?.success == true) {
            Result.Success(true)
        } else {
            Result.Error(
                Exception(response.errors?.get(0)?.message ?:
                          "Unknown error while checking for user logged in")
            )
        }
    }
}
