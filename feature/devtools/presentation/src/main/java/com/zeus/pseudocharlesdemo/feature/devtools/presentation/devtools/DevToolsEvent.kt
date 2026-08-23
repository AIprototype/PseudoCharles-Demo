package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import com.zeus.pseudocharlesdemo.core.presentation.UiText

sealed interface DevToolsEvent {
    data class ShowMessage(val message: UiText) : DevToolsEvent

    /** Handled in the Root — needs an Activity context. */
    data object OpenNotificationSettings : DevToolsEvent
}
