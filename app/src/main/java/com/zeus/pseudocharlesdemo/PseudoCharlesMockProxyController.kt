package com.zeus.pseudocharlesdemo

import android.content.Context
import android.content.Intent
import com.zeus.pseudocharles.PseudoCharles
import com.zeus.pseudocharles.ServerRuntimeState
import com.zeus.pseudocharles.model.NetworkProfile
import com.zeus.pseudocharles.model.NetworkProfileId
import com.zeus.pseudocharles.model.FailureMode
import com.zeus.pseudocharles.server.PseudoCharlesServer
import com.zeus.pseudocharlesdemo.core.domain.CustomNetworkSpec
import com.zeus.pseudocharlesdemo.core.domain.DashboardState
import com.zeus.pseudocharlesdemo.core.domain.MockProxyController
import com.zeus.pseudocharlesdemo.core.domain.MockProxyStatus
import com.zeus.pseudocharlesdemo.core.domain.NetworkFailureMode
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * The single place in this app that knows PseudoCharles exists.
 *
 * Everything above this class talks to [MockProxyController], so no feature module has to depend on
 * a `debugImplementation` artifact. In release the no-op artifacts supply identical signatures, so
 * this file compiles and runs unchanged — [isAvailable] simply reports `false` and every call below
 * is a no-op.
 */
class PseudoCharlesMockProxyController(
    private val context: Context
) : MockProxyController {

    override val isAvailable: Boolean get() = PseudoCharles.isEnabled()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _status = MutableStateFlow(MockProxyStatus())
    override val status: Flow<MockProxyStatus> = _status.asStateFlow()

    /**
     * The profile this app last asked for, held until the SDK reports it back.
     *
     * `setNetworkProfile` persists through DataStore and only then mirrors the value into the
     * volatile field `networkProfile()` reads, so a [refresh] issued immediately after a write
     * still returns the *previous* profile. Without this, tapping a preset appeared to do nothing
     * until the screen was left and re-entered. Cleared as soon as the SDK agrees.
     */
    private var requestedProfile: NetworkSpeedProfile? = null

    private var reconcileJob: Job? = null

    override val dashboard: Flow<DashboardState> =
        PseudoCharlesServer.state.map { it.toDashboardState() }

    override fun refresh() {
        val sdkProfile = PseudoCharles.networkProfile().toNetworkSpeedProfile()
        val pending = requestedProfile
        if (pending != null && pending == sdkProfile) requestedProfile = null

        _status.value = MockProxyStatus(
            available = PseudoCharles.isEnabled(),
            hasActiveMock = PseudoCharles.isActive(),
            trafficCount = PseudoCharles.getTrafficSnapshot().size,
            socketFrameCount = PseudoCharles.getSocketTrafficSnapshot().size,
            // Prefer the pending value while the SDK's async write is still in flight.
            networkSpeed = requestedProfile ?: sdkProfile,
            trafficLoggingEnabled = PseudoCharles.trafficLoggingEnabled,
            socketLoggingEnabled = PseudoCharles.socketLoggingEnabled,
            // Carried forward, never read back: the SDK has no getter for this one.
            persistentNotificationRequested = _status.value.persistentNotificationRequested
        )
    }

    /**
     * Re-reads a few times after a write.
     *
     * Both the active-mock cache and the network profile are fed back from DataStore
     * asynchronously, so a single post-write read can be stale. Cheap: every value read is
     * in-memory, no disk I/O.
     */
    private fun scheduleReconcile() {
        reconcileJob?.cancel()
        reconcileJob = scope.launch {
            repeat(RECONCILE_ATTEMPTS) {
                delay(RECONCILE_INTERVAL_MS)
                refresh()
            }
        }
    }

    override fun openMockConfig() {
        val intent = PseudoCharles.getLaunchIntent(context)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun setNetworkSpeed(profile: NetworkSpeedProfile) {
        PseudoCharles.setNetworkProfile(context, profile.toNetworkProfileId())
        requestedProfile = profile
        _status.value = _status.value.copy(networkSpeed = profile)
        scheduleReconcile()
    }

    override fun setCustomNetworkSpeed(spec: CustomNetworkSpec) {
        PseudoCharles.setCustomNetworkProfile(
            context,
            NetworkProfile(
                downKbps = spec.downKbps,
                upKbps = spec.upKbps,
                latencyMs = spec.latencyMs,
                jitterPercent = spec.jitterPercent,
                failureRatePercent = spec.failureRatePercent,
                failureMode = when (spec.failureMode) {
                    NetworkFailureMode.IMMEDIATE -> FailureMode.IMMEDIATE
                    NetworkFailureMode.TIMEOUT -> FailureMode.TIMEOUT
                }
            )
        )
        // setCustomNetworkProfile only stores the values; CUSTOM has to be selected to apply them.
        setNetworkSpeed(NetworkSpeedProfile.CUSTOM)
    }

    override fun startDashboard() = PseudoCharles.startServer(context)

    override fun stopDashboard() = PseudoCharles.stopServer()

    override fun setPersistentNotification(enabled: Boolean) {
        PseudoCharles.setPersistentNotificationEnabled(context, enabled)
        _status.value = _status.value.copy(persistentNotificationRequested = enabled)
    }

    override fun setTrafficLogging(enabled: Boolean) {
        PseudoCharles.trafficLoggingEnabled = enabled
    }

    override fun setSocketLogging(enabled: Boolean) {
        PseudoCharles.socketLoggingEnabled = enabled
    }

    override fun clearTraffic() = PseudoCharles.clearTrafficLog()

    override fun clearSockets() = PseudoCharles.clearSocketTraffic()

    override fun clearAllMocks() {
        PseudoCharles.clearAll(context)
        scheduleReconcile()
    }

    private companion object {
        const val RECONCILE_ATTEMPTS = 4
        const val RECONCILE_INTERVAL_MS = 250L
    }
}

private fun ServerRuntimeState.toDashboardState(): DashboardState = when (this) {
    is ServerRuntimeState.Running -> DashboardState.Running(url = url, port = port)
    is ServerRuntimeState.Error -> DashboardState.Failed(message)
    else -> DashboardState.Stopped
}

/** Exhaustive by construction: adding a demo preset fails to compile until it is mapped here. */
private fun NetworkSpeedProfile.toNetworkProfileId(): NetworkProfileId = when (this) {
    NetworkSpeedProfile.OFF -> NetworkProfileId.OFF
    NetworkSpeedProfile.OFFLINE -> NetworkProfileId.OFFLINE
    NetworkSpeedProfile.GPRS -> NetworkProfileId.GPRS
    NetworkSpeedProfile.EDGE -> NetworkProfileId.EDGE
    NetworkSpeedProfile.THREE_G_SLOW -> NetworkProfileId.THREE_G_SLOW
    NetworkSpeedProfile.THREE_G -> NetworkProfileId.THREE_G
    NetworkSpeedProfile.FOUR_G_WEAK -> NetworkProfileId.FOUR_G_WEAK
    NetworkSpeedProfile.FOUR_G_CONGESTED -> NetworkProfileId.FOUR_G_CONGESTED
    NetworkSpeedProfile.FOUR_G -> NetworkProfileId.FOUR_G
    NetworkSpeedProfile.FOUR_G_STRONG -> NetworkProfileId.FOUR_G_STRONG
    NetworkSpeedProfile.FIVE_G -> NetworkProfileId.FIVE_G
    NetworkSpeedProfile.FIVE_G_MMWAVE -> NetworkProfileId.FIVE_G_MMWAVE
    NetworkSpeedProfile.WIFI -> NetworkProfileId.WIFI
    NetworkSpeedProfile.VERY_BAD -> NetworkProfileId.VERY_BAD
    NetworkSpeedProfile.CUSTOM -> NetworkProfileId.CUSTOM
}

private fun NetworkProfileId.toNetworkSpeedProfile(): NetworkSpeedProfile = when (this) {
    NetworkProfileId.OFF -> NetworkSpeedProfile.OFF
    NetworkProfileId.OFFLINE -> NetworkSpeedProfile.OFFLINE
    NetworkProfileId.GPRS -> NetworkSpeedProfile.GPRS
    NetworkProfileId.EDGE -> NetworkSpeedProfile.EDGE
    NetworkProfileId.THREE_G_SLOW -> NetworkSpeedProfile.THREE_G_SLOW
    NetworkProfileId.THREE_G -> NetworkSpeedProfile.THREE_G
    NetworkProfileId.FOUR_G_WEAK -> NetworkSpeedProfile.FOUR_G_WEAK
    NetworkProfileId.FOUR_G_CONGESTED -> NetworkSpeedProfile.FOUR_G_CONGESTED
    NetworkProfileId.FOUR_G -> NetworkSpeedProfile.FOUR_G
    NetworkProfileId.FOUR_G_STRONG -> NetworkSpeedProfile.FOUR_G_STRONG
    NetworkProfileId.FIVE_G -> NetworkSpeedProfile.FIVE_G
    NetworkProfileId.FIVE_G_MMWAVE -> NetworkSpeedProfile.FIVE_G_MMWAVE
    NetworkProfileId.WIFI -> NetworkSpeedProfile.WIFI
    NetworkProfileId.VERY_BAD -> NetworkSpeedProfile.VERY_BAD
    NetworkProfileId.CUSTOM -> NetworkSpeedProfile.CUSTOM
}
