package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.core.presentation.UiText
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.model.BreweryUi

@Immutable
data class BreweryDetailState(
    val brewery: BreweryUi? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null
)
