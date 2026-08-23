package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedStatus
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.model.LiveFeedFrameUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LiveFeedState(
    val endpoint: String = "",
    val status: LiveFeedStatus = LiveFeedStatus.Disconnected,
    val frames: ImmutableList<LiveFeedFrameUi> = persistentListOf(),
    val autoEmit: Boolean = false,
    /** False in release builds — the "open the inspector" affordance has nothing to open. */
    val inspectorAvailable: Boolean = false
) {
    val isConnected: Boolean get() = status is LiveFeedStatus.Connected
    val isBusy: Boolean get() = status is LiveFeedStatus.Connecting
}
