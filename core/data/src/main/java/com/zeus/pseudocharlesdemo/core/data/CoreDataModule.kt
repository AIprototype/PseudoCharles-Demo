package com.zeus.pseudocharlesdemo.core.data

import io.ktor.client.HttpClient
import org.koin.dsl.module

val coreDataModule = module {
    single<HttpClient> {
        HttpClientFactory(interceptorProvider = get()).create()
    }
}
