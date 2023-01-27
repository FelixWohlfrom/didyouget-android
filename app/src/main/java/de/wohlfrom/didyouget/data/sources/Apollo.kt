package de.wohlfrom.didyouget.data.sources

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import de.wohlfrom.didyouget.data.model.LoggedInUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private var instance: ApolloClient? = null
private var storedServerUrl: String? = null
private var storedLoggedinUser: LoggedInUser? = null

fun apolloClient(serverUrl: String? = null, user: LoggedInUser? = null): ApolloClient {
    if (instance != null &&
        (serverUrl == null ||
                (serverUrl == storedServerUrl && user == storedLoggedinUser))
    ) {
        return instance!!
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(user))
        .build()

    instance = ApolloClient.Builder()
        .serverUrl(serverUrl!!)
        .okHttpClient(okHttpClient)
        .build()

    storedServerUrl = serverUrl
    storedLoggedinUser = user

    return instance!!
}

private class AuthorizationInterceptor(val user: LoggedInUser? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", user?.token ?: "")
            .build()

        return chain.proceed(request)
    }
}
