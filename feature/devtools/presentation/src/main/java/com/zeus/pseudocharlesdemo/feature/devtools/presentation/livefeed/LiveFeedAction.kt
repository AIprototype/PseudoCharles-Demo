package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

sealed interface LiveFeedAction {
    data object OnConnect : LiveFeedAction
    data object OnDisconnect : LiveFeedAction
    data class OnAutoEmitToggle(val enabled: Boolean) : LiveFeedAction
    data object OnSendCheckIn : LiveFeedAction
    data object OnSendPlainText : LiveFeedAction
    data object OnSendBinary : LiveFeedAction
    data object OnClearFrames : LiveFeedAction
    data object OnOpenInspector : LiveFeedAction
}
