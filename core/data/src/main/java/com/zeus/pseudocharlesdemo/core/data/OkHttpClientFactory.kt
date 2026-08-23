package com.zeus.pseudocharlesdemo.core.data

import okhttp3.OkHttpClient

/**
 * Builds the single [OkHttpClient] shared by the whole app.
 *
 * Deliberately separate from [HttpClientFactory] so the same instance backs both the Ktor engine
 * and the WebSocket demo — an `OkHttpClient` is itself a `WebSocket.Factory`, and sharing one means
 * one connection pool, one dispatcher, and one interceptor chain that the mock proxy can observe.
 */
class OkHttpClientFactory(
    private val interceptorProvider: NetworkInterceptorProvider
) {
    fun create(): OkHttpClient = OkHttpClient.Builder()
        .apply { interceptorProvider.interceptors().forEach { addInterceptor(it) } }
        .build()
}
