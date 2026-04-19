package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import com.zeus.pseudocharlesdemo.core.presentation.UiText

sealed interface BrewerySearchEvent {
    data class NavigateToDetail(val id: String) : BrewerySearchEvent
    data class ShowError(val message: UiText) : BrewerySearchEvent
}
