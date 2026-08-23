package com.zeus.pseudocharlesdemo

import com.zeus.pseudocharlesdemo.core.data.NetworkInterceptorProvider
import com.zeus.pseudocharlesdemo.core.data.WebSocketFactoryProvider
import com.zeus.pseudocharlesdemo.core.domain.MockProxyController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Every binding that needs the PseudoCharles SDK. Kept in `:app` so no other module depends on a
 * debug-only artifact — the implementations behind these three interfaces are the app's entire
 * contact surface with the SDK.
 */
val platformAppModule: Module = module {
    single<NetworkInterceptorProvider> { PseudoCharlesInterceptorProvider(androidContext()) }
    single<WebSocketFactoryProvider> { PseudoCharlesSocketFactoryProvider() }
    single<MockProxyController> { PseudoCharlesMockProxyController(androidContext()) }
}
