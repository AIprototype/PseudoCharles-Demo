package com.zeus.pseudocharlesdemo.feature.devtools.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

/** Collects one-shot events for the lifetime of the composition. `onEvent` may suspend, so it can
 * drive a snackbar directly. */

@Composable
fun <T> ObserveEvents(events: Flow<T>, onEvent: suspend (T) -> Unit) {
    LaunchedEffect(events) {
        events.collect(onEvent)
    }
}
