package com.zeus.pseudocharlesdemo.feature.devtools.presentation

import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsViewModel
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed.LiveFeedViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val devToolsPresentationModule = module {
    viewModelOf(::LiveFeedViewModel)
    viewModelOf(::DevToolsViewModel)
}
