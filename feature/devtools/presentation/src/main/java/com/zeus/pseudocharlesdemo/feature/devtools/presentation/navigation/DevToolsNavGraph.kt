package com.zeus.pseudocharlesdemo.feature.devtools.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsRoot
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed.LiveFeedRoot

/**
 * Both destinations that exist to exercise the SDK. Mirrors
 * `NavGraphBuilder.breweryGraph` — one extension per feature, assembled in `:app`.
 */
fun NavGraphBuilder.devToolsGraph(snackbarHostState: SnackbarHostState) {
    composable<LiveFeedRoute> {
        LiveFeedRoot(snackbarHostState = snackbarHostState)
    }
    composable<DevToolsRoute> {
        DevToolsRoot(snackbarHostState = snackbarHostState)
    }
}
