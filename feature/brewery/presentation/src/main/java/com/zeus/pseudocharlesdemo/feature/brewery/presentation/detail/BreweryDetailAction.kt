package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

sealed interface BreweryDetailAction {
    data object OnBackClick : BreweryDetailAction
    data class OnOpenWebsite(val url: String) : BreweryDetailAction
    data class OnCallPhone(val phone: String) : BreweryDetailAction
}
