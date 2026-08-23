package com.zeus.pseudocharlesdemo.core.domain

/**
 * Point-in-time snapshot of the mock proxy.
 *
 * This is a snapshot rather than a stream because the SDK exposes `isEnabled()` / `isActive()` /
 * `networkProfile()` as plain function calls, not flows. [MockProxyController.refresh] re-reads
 * them; the UI calls it on `ON_RESUME` and after every action.
 */
data class MockProxyStatus(
    val available: Boolean = false,
    val hasActiveMock: Boolean = false,
    val trafficCount: Int = 0,
    val socketFrameCount: Int = 0,
    val networkSpeed: NetworkSpeedProfile = NetworkSpeedProfile.OFF,
    val trafficLoggingEnabled: Boolean = true,
    val socketLoggingEnabled: Boolean = true,
    /**
     * Optimistic local state. The SDK exposes a setter for the persistent notification but **no
     * getter**, so this reflects what this app last asked for and can drift from reality if the
     * user changes it in the SDK's own Settings screen or revokes the notification permission.
     */
    val persistentNotificationRequested: Boolean = false
)
