package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeus.pseudocharlesdemo.core.domain.CustomNetworkSpec
import com.zeus.pseudocharlesdemo.core.domain.MockProxyController
import com.zeus.pseudocharlesdemo.core.domain.NetworkFailureMode
import com.zeus.pseudocharlesdemo.core.presentation.UiText
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DevToolsViewModel(
    private val mockProxy: MockProxyController
) : ViewModel() {

    private val _state = MutableStateFlow(DevToolsState(available = mockProxy.isAvailable))
    val state: StateFlow<DevToolsState> = _state

    private val _events = Channel<DevToolsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        mockProxy.status
            .onEach { status ->
                _state.update {
                    it.copy(
                        available = status.available,
                        hasActiveMock = status.hasActiveMock,
                        networkSpeed = status.networkSpeed,
                        trafficCount = status.trafficCount,
                        socketFrameCount = status.socketFrameCount,
                        trafficLoggingEnabled = status.trafficLoggingEnabled,
                        socketLoggingEnabled = status.socketLoggingEnabled,
                        persistentNotificationRequested = status.persistentNotificationRequested
                    )
                }
            }
            .launchIn(viewModelScope)

        mockProxy.dashboard
            .onEach { dashboard -> _state.update { it.copy(dashboard = dashboard) } }
            .launchIn(viewModelScope)

        mockProxy.refresh()
    }

    fun onAction(action: DevToolsAction) {
        when (action) {
            DevToolsAction.OnRefresh -> mockProxy.refresh()

            DevToolsAction.OnOpenConfig -> mockProxy.openMockConfig()

            DevToolsAction.OnClearMocks -> {
                mockProxy.clearAllMocks()
                mockProxy.refresh()
                notify(R.string.dev_tools_mocks_cleared)
            }

            is DevToolsAction.OnNetworkSpeedSelected -> {
                mockProxy.setNetworkSpeed(action.profile)
                mockProxy.refresh()
            }

            DevToolsAction.OnFlakyProfileSelected -> {
                // The "black hole" failure mode is the one that actually breaks mobile apps: the
                // request hangs for the full read timeout before failing, rather than failing fast.
                mockProxy.setCustomNetworkSpeed(
                    CustomNetworkSpec(
                        downKbps = 1_000,
                        upKbps = 200,
                        latencyMs = 500,
                        jitterPercent = 60,
                        failureRatePercent = 10,
                        failureMode = NetworkFailureMode.TIMEOUT
                    )
                )
                mockProxy.refresh()
            }

            is DevToolsAction.OnDashboardToggle ->
                if (action.running) mockProxy.startDashboard() else mockProxy.stopDashboard()

            is DevToolsAction.OnCopyDashboardUrl -> notify(R.string.dev_tools_dashboard_copied)

            is DevToolsAction.OnNotificationToggle -> {
                mockProxy.setPersistentNotification(action.enabled)
                _state.update { it.copy(persistentNotificationRequested = action.enabled) }
            }

            is DevToolsAction.OnNotificationPermissionBlocked ->
                _state.update { it.copy(notificationPermissionBlocked = action.blocked) }

            DevToolsAction.OnOpenNotificationSettings ->
                viewModelScope.launch { _events.send(DevToolsEvent.OpenNotificationSettings) }

            is DevToolsAction.OnTrafficLoggingToggle -> {
                mockProxy.setTrafficLogging(action.enabled)
                mockProxy.refresh()
            }

            is DevToolsAction.OnSocketLoggingToggle -> {
                mockProxy.setSocketLogging(action.enabled)
                mockProxy.refresh()
            }

            DevToolsAction.OnClearTraffic -> {
                mockProxy.clearTraffic()
                mockProxy.refresh()
            }

            DevToolsAction.OnClearSockets -> {
                mockProxy.clearSockets()
                mockProxy.refresh()
            }
        }
    }

    private fun notify(resId: Int) {
        viewModelScope.launch {
            _events.send(DevToolsEvent.ShowMessage(UiText.StringResource(resId)))
        }
    }
}
