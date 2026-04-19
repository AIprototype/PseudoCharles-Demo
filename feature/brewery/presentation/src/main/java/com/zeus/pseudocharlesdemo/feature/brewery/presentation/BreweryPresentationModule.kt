package com.zeus.pseudocharlesdemo.feature.brewery.presentation

import com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail.BreweryDetailViewModel
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.search.BrewerySearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val breweryPresentationModule = module {
    viewModelOf(::BrewerySearchViewModel)
    viewModelOf(::BreweryDetailViewModel)
}
