package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.core.presentation.UiText
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.model.BreweryUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class BrewerySearchState(
    val query: String = "",
    val results: ImmutableList<BreweryUi> = persistentListOf(),
    val selectedTypes: ImmutableSet<BreweryType> = persistentSetOf(),
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)
