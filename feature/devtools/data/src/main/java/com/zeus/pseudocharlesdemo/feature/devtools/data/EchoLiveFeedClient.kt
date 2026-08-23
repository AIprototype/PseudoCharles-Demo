package com.zeus.pseudocharlesdemo.feature.devtools.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.zeus.pseudocharlesdemo.core.data.WebSocketFactoryProvider
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedClient
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedDirection
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFailure
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFrame
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFrameKind
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.IOException
import java.util.UUID

/**
 * Generates inspectable WebSocket traffic by talking to a public echo server through the mock
 * proxy's [WebSocketFactoryProvider].
 *
 * **This feed is simulated.** Open Brewery DB has no realtime API, so the app sends itself
 * Socket.IO-shaped events and the echo server bounces them straight back. That round trip is what
 * makes it useful: the echoed copies arrive as *inbound* frames carrying a decodable Socket.IO
 * event name, which is precisely what the SDK's decoder, the dashboard's `/sockets` page and the
 * persistent notification's socket lines all key off. A one-way send would exercise none of them.
 *
 * The socket is owned for the life of the process (see [LiveFeedClient]), not the life of a
 * ViewModel, so rotating the device does not open a second connection.
 */
class EchoLiveFeedClient(
    private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val socketFactoryProvider: WebSocketFactoryProvider
) : LiveFeedClient {

    override val endpoint: String = ENDPOINT

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _status = MutableStateFlow<LiveFeedStatus>(LiveFeedStatus.Disconnected)
    override val status: StateFlow<LiveFeedStatus> = _status.asStateFlow()

    private val _frames = MutableStateFlow<List<LiveFeedFrame>>(emptyList())
    override val frames: StateFlow<List<LiveFeedFrame>> = _frames.asStateFlow()

    private var webSocket: WebSocket? = null
    private var autoEmitJob: Job? = null

    /** Distinguishes "we asked to close" from "the server dropped us". */
    @Volatile
    private var closingByUser = false

    private var checkInCounter = 0

    override fun connect() {
        if (_status.value == LiveFeedStatus.Connected || _status.value == LiveFeedStatus.Connecting) return

        if (!hasNetwork()) {
            _status.value = LiveFeedStatus.Failed(LiveFeedFailure.NoNetwork)
            return
        }

        closingByUser = false
        _status.value = LiveFeedStatus.Connecting

        // The one line that makes every frame below visible in the inspector: the client is wrapped
        // before any socket is created. Interceptors cannot do this — they never see WS frames.
        val factory = socketFactoryProvider.decorate(okHttpClient)
        webSocket = factory.newWebSocket(Request.Builder().url(ENDPOINT).build(), Listener())
    }

    override fun disconnect() {
        setAutoEmit(false)
        closingByUser = true
        webSocket?.close(NORMAL_CLOSURE, "Demo finished")
        webSocket = null
        _status.value = LiveFeedStatus.Disconnected
    }

    override fun setAutoEmit(enabled: Boolean) {
        autoEmitJob?.cancel()
        autoEmitJob = if (!enabled) {
            null
        } else {
            scope.launch {
                while (isActive) {
                    emitCheckIn()
                    delay(AUTO_EMIT_INTERVAL_MS)
                }
            }
        }
    }

    override fun emitCheckIn() {
        val brewery = BREWERIES[checkInCounter++ % BREWERIES.size]
        val pours = 1 + (checkInCounter * 7) % 12
        // Socket.IO wire format: '4' = Engine.IO MESSAGE, '2' = Socket.IO EVENT.
        val payload = """42["$EVENT_CHECK_IN",{"brewery":"$brewery","pours":$pours}]"""
        send(payload, LiveFeedFrameKind.TEXT, EVENT_CHECK_IN)
    }

    override fun emitPlainText() {
        send("plain text frame — no Socket.IO envelope", LiveFeedFrameKind.TEXT, event = null)
    }

    override fun emitBinary() {
        val socket = webSocket ?: return
        val bytes = ByteArray(BINARY_FRAME_BYTES) { (it * 31 % 256).toByte() }.toByteString()
        if (socket.send(bytes)) {
            record(LiveFeedDirection.OUTBOUND, LiveFeedFrameKind.BINARY, null, "${bytes.size} bytes")
        }
    }

    override fun clearFrames() {
        _frames.value = emptyList()
    }

    private fun send(payload: String, kind: LiveFeedFrameKind, event: String?) {
        val socket = webSocket ?: return
        if (socket.send(payload)) record(LiveFeedDirection.OUTBOUND, kind, event, payload)
    }

    private fun record(
        direction: LiveFeedDirection,
        kind: LiveFeedFrameKind,
        event: String?,
        payload: String
    ) {
        val frame = LiveFeedFrame(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            direction = direction,
            kind = kind,
            event = event,
            payload = payload
        )
        _frames.update { (listOf(frame) + it).take(MAX_FRAMES) }
    }

    /** Best-effort event-name extraction, mirroring what the SDK's decoder does with `42[...]`. */
    private fun decodeEventName(text: String): String? {
        if (!text.startsWith("42[")) return null
        val firstQuote = text.indexOf('"', startIndex = 3)
        if (firstQuote < 0) return null
        val secondQuote = text.indexOf('"', startIndex = firstQuote + 1)
        if (secondQuote < 0) return null
        return text.substring(firstQuote + 1, secondQuote)
    }

    private fun hasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true // Can't tell — assume online and let the socket report the truth.
        val capabilities = cm.activeNetwork?.let(cm::getNetworkCapabilities) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private inner class Listener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            _status.value = LiveFeedStatus.Connected
            record(LiveFeedDirection.INBOUND, LiveFeedFrameKind.OPEN, null, "Connected to $ENDPOINT")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            record(LiveFeedDirection.INBOUND, LiveFeedFrameKind.TEXT, decodeEventName(text), text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            record(LiveFeedDirection.INBOUND, LiveFeedFrameKind.BINARY, null, "${bytes.size} bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            record(LiveFeedDirection.INBOUND, LiveFeedFrameKind.CLOSING, null, "$code $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            record(LiveFeedDirection.INBOUND, LiveFeedFrameKind.CLOSED, null, "$code $reason")
            _status.value = if (closingByUser) {
                LiveFeedStatus.Disconnected
            } else {
                LiveFeedStatus.Failed(LiveFeedFailure.ClosedByServer(code, reason))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            record(
                LiveFeedDirection.INBOUND,
                LiveFeedFrameKind.FAILURE,
                null,
                t.message ?: t::class.java.simpleName
            )
            this@EchoLiveFeedClient.webSocket = null
            setAutoEmit(false)

            // Classified after a short settle. Losing connectivity aborts the socket *before*
            // ConnectivityManager reports the network gone, so classifying inline reported
            // airplane mode as a third-party outage — the exact confusion this screen must avoid.
            scope.launch {
                delay(CLASSIFY_SETTLE_MS)
                _status.value = LiveFeedStatus.Failed(classify(t, response))
            }
        }

        /**
         * Order matters. Every transport failure here is an [IOException] of some flavour —
         * `UnknownHostException` when DNS cannot resolve, `SocketException` when the link drops
         * mid-connection — and the *same* exception means different things depending on whether
         * this device still has a network. So connectivity decides, not the exception type.
         */
        private fun classify(t: Throwable, response: Response?): LiveFeedFailure = when {
            response != null -> LiveFeedFailure.Rejected(response.code)
            !hasNetwork() -> LiveFeedFailure.NoNetwork
            t is IOException -> LiveFeedFailure.Unreachable
            else -> LiveFeedFailure.Unknown(t.message ?: t::class.java.simpleName)
        }
    }

    private companion object {
        const val ENDPOINT = "wss://echo.websocket.org"
        const val EVENT_CHECK_IN = "taproom:checkin"
        const val AUTO_EMIT_INTERVAL_MS = 2_500L
        const val NORMAL_CLOSURE = 1000
        const val MAX_FRAMES = 100
        const val BINARY_FRAME_BYTES = 48
        const val CLASSIFY_SETTLE_MS = 700L

        val BREWERIES = listOf(
            "Fremont Brewing",
            "Russian River Brewing",
            "Tree House Brewing",
            "Cloudwater Brew Co",
            "Omnipollo",
            "Mikkeller",
            "Other Half Brewing",
            "Verdant Brewing Co"
        )
    }
}
