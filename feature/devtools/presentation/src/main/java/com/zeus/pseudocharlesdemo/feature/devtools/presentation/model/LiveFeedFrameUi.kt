package com.zeus.pseudocharlesdemo.feature.devtools.presentation.model

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedDirection
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFrame
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFrameKind
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Render-ready view of a [LiveFeedFrame], with the timestamp already formatted. */
@Immutable
data class LiveFeedFrameUi(
    val id: String,
    val time: String,
    val direction: LiveFeedDirection,
    val kind: LiveFeedFrameKind,
    val event: String?,
    val payload: String
)

private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

fun LiveFeedFrame.toLiveFeedFrameUi(): LiveFeedFrameUi = LiveFeedFrameUi(
    id = id,
    time = timeFormat.format(Date(timestamp)),
    direction = direction,
    kind = kind,
    event = event,
    payload = payload
)
