package com.zeus.pseudocharlesdemo

import com.zeus.pseudocharles.PseudoCharles
import com.zeus.pseudocharlesdemo.core.data.WebSocketFactoryProvider
import okhttp3.WebSocket

/**
 * Routes every WebSocket the app opens through PseudoCharles' inspector.
 *
 * OkHttp interceptors only ever see the upgrade handshake — frames go through `RealWebSocket` and
 * bypass the interceptor chain entirely — so this is a second, separate seam from
 * [PseudoCharlesInterceptorProvider].
 *
 * In release the no-op artifact returns `delegate` unchanged, making this a true pass-through.
 */
class PseudoCharlesSocketFactoryProvider : WebSocketFactoryProvider {
    override fun decorate(delegate: WebSocket.Factory): WebSocket.Factory =
        PseudoCharles.webSocketFactory(delegate)
}
