package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile

sealed interface DevToolsAction {
    data object OnRefresh : DevToolsAction
    data object OnOpenConfig : DevToolsAction
    data object OnClearMocks : DevToolsAction

    data class OnNetworkSpeedSelected(val profile: NetworkSpeedProfile) : DevToolsAction
    data object OnFlakyProfileSelected : DevToolsAction

    data class OnDashboardToggle(val running: Boolean) : DevToolsAction
    data class OnCopyDashboardUrl(val url: String) : DevToolsAction

    /** Only reached once POST_NOTIFICATIONS is confirmed — see DevToolsRoot. */
    data class OnNotificationToggle(val enabled: Boolean) : DevToolsAction
    data class OnNotificationPermissionBlocked(val blocked: Boolean) : DevToolsAction
    data object OnOpenNotificationSettings : DevToolsAction

    data class OnTrafficLoggingToggle(val enabled: Boolean) : DevToolsAction
    data class OnSocketLoggingToggle(val enabled: Boolean) : DevToolsAction
    data object OnClearTraffic : DevToolsAction
    data object OnClearSockets : DevToolsAction
}
