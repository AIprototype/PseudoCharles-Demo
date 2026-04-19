package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrewerySearchRoot(
    viewModel: BrewerySearchViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvents(events = viewModel.events) { event ->
        when (event) {
            is BrewerySearchEvent.NavigateToDetail -> onNavigateToDetail(event.id)
            is BrewerySearchEvent.ShowError -> { }
        }
    }

    BrewerySearchScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
