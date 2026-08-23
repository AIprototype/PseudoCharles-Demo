package com.zeus.pseudocharlesdemo.core.data

import io.ktor.client.HttpClient
import okhttp3.OkHttpClient
import org.koin.dsl.module

val coreDataModule = module {
    // Single instance so the Ktor engine and the WebSocket demo share one connection pool and one
    // interceptor chain. See OkHttpClientFactory.
    single<OkHttpClient> { OkHttpClientFactory(interceptorProvider = get()).create() }

    single<HttpClient> { HttpClientFactory(okHttpClient = get()).create() }
}
