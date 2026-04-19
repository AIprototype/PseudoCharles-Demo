package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.search.ObserveEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun BreweryDetailRoot(
    viewModel: BreweryDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onDialPhone: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvents(events = viewModel.events) { event ->
        when (event) {
            BreweryDetailEvent.NavigateBack -> onNavigateBack()
            is BreweryDetailEvent.OpenUrl -> onOpenUrl(event.url)
            is BreweryDetailEvent.DialPhone -> onDialPhone(event.phone)
        }
    }

    BreweryDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
