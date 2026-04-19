package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType

sealed interface BrewerySearchAction {
    data class OnQueryChange(val query: String) : BrewerySearchAction
    data class OnBreweryClick(val id: String) : BrewerySearchAction
    data class OnTypeToggle(val type: BreweryType) : BrewerySearchAction
    data object OnSearch : BrewerySearchAction
    data object OnClearFilters : BrewerySearchAction
}
