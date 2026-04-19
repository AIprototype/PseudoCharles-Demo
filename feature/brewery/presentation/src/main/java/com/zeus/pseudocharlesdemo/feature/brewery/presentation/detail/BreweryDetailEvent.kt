package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

sealed interface BreweryDetailEvent {
    data object NavigateBack : BreweryDetailEvent
    data class OpenUrl(val url: String) : BreweryDetailEvent
    data class DialPhone(val phone: String) : BreweryDetailEvent
}
