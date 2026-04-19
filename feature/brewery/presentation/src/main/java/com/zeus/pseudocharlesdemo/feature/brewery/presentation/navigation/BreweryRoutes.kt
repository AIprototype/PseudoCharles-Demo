package com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object BrewerySearchRoute

@Serializable
data class BreweryDetailRoute(val id: String)
