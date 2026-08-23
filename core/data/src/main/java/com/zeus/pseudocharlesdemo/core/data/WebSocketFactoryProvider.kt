package com.zeus.pseudocharlesdemo.core.data

import okhttp3.WebSocket

/**
 * Decorates an OkHttp [WebSocket.Factory] before any socket is opened.
 *
 * An OkHttp `Interceptor` only ever sees the HTTP upgrade handshake — WebSocket frames bypass the
 * interceptor chain entirely — so traffic inspectors need this separate seam. `:app` implements it
 * with `PseudoCharles.webSocketFactory(delegate)`; the release no-op returns the delegate unchanged.
 *
 * Sibling of [NetworkInterceptorProvider]: same inversion, different point in OkHttp's API.
 */
interface WebSocketFactoryProvider {
    fun decorate(delegate: WebSocket.Factory): WebSocket.Factory
}
