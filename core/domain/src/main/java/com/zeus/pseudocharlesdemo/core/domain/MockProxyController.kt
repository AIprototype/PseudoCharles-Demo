package com.zeus.pseudocharlesdemo.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * The demo's seam onto the PseudoCharles SDK.
 *
 * PseudoCharles ships as a `debugImplementation` artifact with a no-op twin for release. Rather
 * than let every feature module depend on a debug-only dependency, the app inverts it: this
 * interface lives in the pure-JVM `:core:domain`, and `:app` — the only assembler — provides the
 * single implementation that actually touches `com.zeus.pseudocharles`.
 *
 * This is the same inversion already used for
 * [com.zeus.pseudocharlesdemo.core.data.NetworkInterceptorProvider], extended to the SDK's
 * configuration surface.
 *
 * In release builds the no-op artifact makes [isAvailable] `false` and every call here a cheap
 * no-op, so callers need no variant-specific branching.
 */
interface MockProxyController {

    /** True when the real SDK is on the classpath. False in release builds (no-op artifact). */
    val isAvailable: Boolean

    /** Latest [MockProxyStatus]. Cold values until [refresh] is called — see [MockProxyStatus]. */
    val status: Flow<MockProxyStatus>

    /** Live state of the web dashboard. Backed by a real `StateFlow` in the SDK. */
    val dashboard: Flow<DashboardState>

    /** Re-reads the SDK's pull-only state into [status]. Safe to call often; does no disk I/O. */
    fun refresh()

    /** Opens the SDK's own configuration Activity (Mocks / Traffic / Sockets / Settings). */
    fun openMockConfig()

    // --- Mocked network speed -------------------------------------------------------------------

    /** Applies a preset. Persisted by the SDK, so it survives process death. */
    fun setNetworkSpeed(profile: NetworkSpeedProfile)

    /** Applies free-form conditions and selects [NetworkSpeedProfile.CUSTOM]. */
    fun setCustomNetworkSpeed(spec: CustomNetworkSpec)

    // --- Web dashboard --------------------------------------------------------------------------

    fun startDashboard()

    fun stopDashboard()

    // --- Persistent traffic notification --------------------------------------------------------

    /**
     * Write-only in the SDK — there is no matching getter, so the caller owns the displayed state.
     * On Android 13+ the host app must already hold `POST_NOTIFICATIONS` for anything to appear;
     * this call never throws if it does not.
     */
    fun setPersistentNotification(enabled: Boolean)

    // --- Capture toggles ------------------------------------------------------------------------

    fun setTrafficLogging(enabled: Boolean)

    fun setSocketLogging(enabled: Boolean)

    fun clearTraffic()

    fun clearSockets()

    fun clearAllMocks()
}
