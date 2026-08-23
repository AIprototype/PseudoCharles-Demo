package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeus.pseudocharlesdemo.core.domain.MockProxyController
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedClient
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.model.toLiveFeedFrameUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Observes the process-scoped [LiveFeedClient]; it deliberately does not own the socket.
 *
 * A ViewModel is destroyed and recreated on configuration change. If the connection lived here,
 * every rotation would close one socket and open another, and the SDK's Sockets tab — which groups
 * frames by connection — would show a new connection per rotation instead of one continuous feed.
 */
class LiveFeedViewModel(
    private val client: LiveFeedClient,
    private val mockProxy: MockProxyController
) : ViewModel() {

    private val _state = MutableStateFlow(
        LiveFeedState(
            endpoint = client.endpoint,
            inspectorAvailable = mockProxy.isAvailable
        )
    )
    val state: StateFlow<LiveFeedState> = _state

    private val _events = Channel<LiveFeedEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        client.status
            .onEach { status -> _state.update { it.copy(status = status) } }
            .launchIn(viewModelScope)

        client.frames
            .onEach { frames ->
                val ui = frames.map { it.toLiveFeedFrameUi() }.toImmutableList()
                _state.update { it.copy(frames = ui) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: LiveFeedAction) {
        when (action) {
            LiveFeedAction.OnConnect -> client.connect()

            LiveFeedAction.OnDisconnect -> {
                client.disconnect()
                _state.update { it.copy(autoEmit = false) }
            }

            is LiveFeedAction.OnAutoEmitToggle -> {
                client.setAutoEmit(action.enabled)
                _state.update { it.copy(autoEmit = action.enabled) }
            }

            LiveFeedAction.OnSendCheckIn -> client.emitCheckIn()
            LiveFeedAction.OnSendPlainText -> client.emitPlainText()
            LiveFeedAction.OnSendBinary -> client.emitBinary()
            LiveFeedAction.OnClearFrames -> client.clearFrames()
            LiveFeedAction.OnOpenInspector -> mockProxy.openMockConfig()
        }
    }
}
