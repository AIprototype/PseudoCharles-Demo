package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

import com.zeus.pseudocharlesdemo.core.presentation.UiText

sealed interface LiveFeedEvent {
    data class ShowMessage(val message: UiText) : LiveFeedEvent
}
