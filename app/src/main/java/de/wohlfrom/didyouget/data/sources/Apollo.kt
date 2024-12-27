package de.wohlfrom.didyouget.data.sources

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private var instance: ApolloClient? = null
private var storedServerUrl: String? = null
private var storedLoggedinToken: String? = null

fun apolloClient(serverUrl: String? = null, token: String? = null): ApolloClient {
    if (instance != null &&
        (serverUrl == null ||
                (serverUrl == storedServerUrl && token == storedLoggedinToken))
    ) {
        return instance!!
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(token))
        .build()

    instance = ApolloClient.Builder()
        .serverUrl(serverUrl!!)
        .okHttpClient(okHttpClient)
        .build()

    storedServerUrl = serverUrl
    storedLoggedinToken = token

    return instance!!
}

private class AuthorizationInterceptor(val token: String? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token ?: "")
            .build()

        return chain.proceed(request)
    }
}
