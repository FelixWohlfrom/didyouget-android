package de.wohlfrom.didyouget.data.sources

import de.wohlfrom.didyouget.data.model.LoggedInUser
import de.wohlfrom.didyouget.graphql.IsLoggedInQuery
import de.wohlfrom.didyouget.graphql.LoginMutation
import de.wohlfrom.didyouget.graphql.type.UserInput
import java.io.IOException

/**
 * Class that handles authentication with login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(serverUrl: String, username: String, password: String):
            Result<LoggedInUser> {
        try {
            val response = try {
                apolloClient(serverUrl).mutation(
                    LoginMutation(UserInput(username, password)))
                    .execute()
            } catch (e: Exception) {
                return Result.Error(e)
            }

            val token = response.data?.login?.token
                ?: return Result.Error(Exception(response.data?.login?.failureMessage))
            apolloClient(serverUrl, token)
            return Result.Success(LoggedInUser(serverUrl, username, token))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    suspend fun checkLoggedIn(): Result<Boolean> {
        val response = apolloClient().query(IsLoggedInQuery()).execute()

        return if (response.data?.isLoggedIn?.success == true) {
            Result.Success(true);
        } else if (response.hasErrors()) {
            Result.Error(Exception(response.errors.toString()))
        } else {
            Result.Error(Exception("Unknown error while checking for user logged in"))
        }
    }
}
