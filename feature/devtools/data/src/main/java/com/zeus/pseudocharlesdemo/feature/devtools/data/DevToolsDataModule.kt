package com.zeus.pseudocharlesdemo.feature.devtools.data

import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val devToolsDataModule: Module = module {
    // `single`, not `factory`: the socket must outlive configuration changes so rotating the
    // device does not open a second connection. See LiveFeedClient's KDoc.
    single<LiveFeedClient> {
        EchoLiveFeedClient(
            context = androidContext(),
            okHttpClient = get(),
            socketFactoryProvider = get()
        )
    }
}
