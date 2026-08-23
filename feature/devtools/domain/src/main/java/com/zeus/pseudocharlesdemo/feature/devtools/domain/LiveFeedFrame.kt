package com.zeus.pseudocharlesdemo.feature.devtools.domain

/** Which way a frame travelled. */
enum class LiveFeedDirection { OUTBOUND, INBOUND }

/** What kind of frame it was, mirroring the shapes the SDK's inspector distinguishes. */
enum class LiveFeedFrameKind { TEXT, BINARY, OPEN, CLOSING, CLOSED, FAILURE }

/**
 * One frame as this screen renders it.
 *
 * Deliberately *not* the SDK's `SocketFrame`: this list is the app's own view of what it sent and
 * received, and it must stay meaningful in release builds where the inspector is absent. The SDK's
 * far richer capture (with Engine.IO / Socket.IO decoding) is what the Sockets tab shows.
 */
data class LiveFeedFrame(
    val id: String,
    val timestamp: Long,
    val direction: LiveFeedDirection,
    val kind: LiveFeedFrameKind,
    /** Short human label, e.g. the Socket.IO event name, or `null` for undecodable frames. */
    val event: String?,
    val payload: String
)
