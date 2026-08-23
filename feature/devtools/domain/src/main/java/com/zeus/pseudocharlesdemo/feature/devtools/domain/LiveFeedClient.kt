package com.zeus.pseudocharlesdemo.feature.devtools.domain

import kotlinx.coroutines.flow.Flow

/**
 * A long-lived WebSocket used to generate inspectable socket traffic.
 *
 * Implementations own the connection for the lifetime of the process, **not** the lifetime of a
 * ViewModel. Tearing the socket down and reopening it on every configuration change would open a
 * fresh connection each rotation, which makes the inspector's per-connection grouping meaningless.
 * The ViewModel observes; the client connects.
 */
interface LiveFeedClient {

    /** The endpoint being talked to, so the UI can name the third party it depends on. */
    val endpoint: String

    val status: Flow<LiveFeedStatus>

    val frames: Flow<List<LiveFeedFrame>>

    fun connect()

    fun disconnect()

    /** Starts/stops emitting a check-in event every couple of seconds. */
    fun setAutoEmit(enabled: Boolean)

    /** Sends one Socket.IO-framed `taproom:checkin` event. */
    fun emitCheckIn()

    /** Sends an undecodable plain-text frame, to contrast with the decoded ones. */
    fun emitPlainText()

    /** Sends a binary frame, which the inspector renders as `[binary: size type]`. */
    fun emitBinary()

    fun clearFrames()
}
