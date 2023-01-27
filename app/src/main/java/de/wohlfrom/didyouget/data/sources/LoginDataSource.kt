package de.wohlfrom.didyouget.data.sources

import de.wohlfrom.didyouget.LoginMutation
import de.wohlfrom.didyouget.data.Result
import de.wohlfrom.didyouget.data.model.LoggedInUser
import de.wohlfrom.didyouget.type.UserInput
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
                null
            }

            val token = response?.data?.login?.token
            if (token == null || response.hasErrors()) {
                return Result.Error(Exception(response?.data?.login?.failureMessage))
            }
            return Result.Success(LoggedInUser(username, password, token))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}