package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveEvents(events: Flow<T>, onEvent: (T) -> Unit) {
    LaunchedEffect(events) {
        events.collect(onEvent)
    }
}
