package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.core.domain.DashboardState
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile

@Immutable
data class DevToolsState(
    /** False in release builds, where the no-op artifact replaces the SDK. */
    val available: Boolean = false,
    val hasActiveMock: Boolean = false,
    val networkSpeed: NetworkSpeedProfile = NetworkSpeedProfile.OFF,
    val dashboard: DashboardState = DashboardState.Stopped,
    val trafficCount: Int = 0,
    val socketFrameCount: Int = 0,
    val trafficLoggingEnabled: Boolean = true,
    val socketLoggingEnabled: Boolean = true,
    /** See [com.zeus.pseudocharlesdemo.core.domain.MockProxyStatus] — optimistic, can drift. */
    val persistentNotificationRequested: Boolean = false,
    /** Set once the user has denied POST_NOTIFICATIONS with "don't ask again". */
    val notificationPermissionBlocked: Boolean = false
)
