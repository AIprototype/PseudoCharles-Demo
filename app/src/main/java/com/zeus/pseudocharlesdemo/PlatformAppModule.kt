package com.zeus.pseudocharlesdemo

import com.zeus.pseudocharlesdemo.core.data.NetworkInterceptorProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val platformAppModule: Module = module {
    single<NetworkInterceptorProvider> { PseudoCharlesInterceptorProvider(androidContext()) }
}
