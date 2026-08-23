package com.zeus.pseudocharlesdemo.feature.devtools.domain

/**
 * Why a live feed connection is not up.
 *
 * Typed rather than a message string on purpose. This screen exists to prove that socket
 * *inspection* works, and it runs against a public third-party echo server — so a reader must
 * never be left unable to tell "the echo server is down" apart from "PseudoCharles failed to
 * capture frames". Each reason gets its own explicit copy in the UI.
 */
sealed interface LiveFeedFailure {
    /** DNS or connect failure — almost always the third-party echo server, not the SDK. */
    data object Unreachable : LiveFeedFailure

    /** The device itself has no connectivity (airplane mode, no Wi-Fi). */
    data object NoNetwork : LiveFeedFailure

    /** The upgrade handshake was refused with an HTTP status. */
    data class Rejected(val code: Int) : LiveFeedFailure

    /** The server closed a healthy connection. */
    data class ClosedByServer(val code: Int, val reason: String) : LiveFeedFailure

    data class Unknown(val message: String) : LiveFeedFailure
}

sealed interface LiveFeedStatus {
    data object Disconnected : LiveFeedStatus

    data object Connecting : LiveFeedStatus

    data object Connected : LiveFeedStatus

    data class Failed(val reason: LiveFeedFailure) : LiveFeedStatus
}
