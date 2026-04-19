package com.zeus.pseudocharlesdemo.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class HttpClientFactory(
    private val interceptorProvider: NetworkInterceptorProvider
) {
    fun create(): HttpClient {
        val okHttpClient = OkHttpClient.Builder().apply {
            interceptorProvider.interceptors().forEach { addInterceptor(it) }
        }.build()

        return HttpClient(OkHttp) {
            engine { preconfigured = okHttpClient }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    }
                )
            }
            install(Logging) { level = LogLevel.BODY }

            defaultRequest {
                url("https://api.openbrewerydb.org/v1/")
            }
        }
    }
}
