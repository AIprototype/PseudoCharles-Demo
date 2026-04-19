package com.zeus.pseudocharlesdemo

import android.app.Application
import com.zeus.pseudocharlesdemo.core.data.coreDataModule
import com.zeus.pseudocharlesdemo.feature.brewery.data.breweryDataModule
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.breweryPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PseudoCharlesDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeMockProxy(this)
        startKoin {
            androidContext(this@PseudoCharlesDemoApp)
            modules(
                platformAppModule,
                coreDataModule,
                breweryDataModule,
                breweryPresentationModule
            )
        }
    }
}
