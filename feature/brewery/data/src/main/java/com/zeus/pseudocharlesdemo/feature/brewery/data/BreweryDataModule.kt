package com.zeus.pseudocharlesdemo.feature.brewery.data

import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val breweryDataModule = module {
    singleOf(::OpenBreweryApi)
    singleOf(::KtorBreweryRepository) bind BreweryRepository::class
}
