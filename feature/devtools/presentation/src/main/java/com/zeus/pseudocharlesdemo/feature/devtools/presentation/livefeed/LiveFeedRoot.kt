package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.ObserveEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiveFeedRoot(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: LiveFeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveEvents(events = viewModel.events) { event ->
        when (event) {
            is LiveFeedEvent.ShowMessage ->
                snackbarHostState.showSnackbar(event.message.asString(context))
        }
    }

    LiveFeedScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
