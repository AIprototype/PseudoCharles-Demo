package com.zeus.pseudocharlesdemo.feature.devtools.presentation.livefeed

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedDirection
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFailure
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedFrameKind
import com.zeus.pseudocharlesdemo.feature.devtools.domain.LiveFeedStatus
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.model.LiveFeedFrameUi
import kotlinx.collections.immutable.persistentListOf

/**
 * The Live tab: opens a WebSocket through the mock proxy's factory seam and shows what went over it.
 *
 * The frame list here is the *app's* own record. The point of the screen is the button at the top
 * that opens PseudoCharles' Sockets tab, where the same frames appear with full Engine.IO /
 * Socket.IO decoding.
 */
@Composable
fun LiveFeedScreen(
    state: LiveFeedState,
    onAction: (LiveFeedAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StatusCard(state = state, onAction = onAction)
        }

        item {
            Text(
                text = stringResource(R.string.live_feed_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Text(
                text = stringResource(R.string.live_feed_endpoint, state.endpoint),
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SendControls(state = state, onAction = onAction)
        }

        if (state.inspectorAvailable) {
            item {
                Button(
                    onClick = { onAction(LiveFeedAction.OnOpenInspector) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.live_feed_open_inspector))
                }
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.live_feed_frames_header, state.frames.size),
                    style = MaterialTheme.typography.titleSmall
                )
                TextButton(onClick = { onAction(LiveFeedAction.OnClearFrames) }) {
                    Text(stringResource(R.string.live_feed_clear))
                }
            }
        }

        if (state.frames.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.live_feed_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items = state.frames, key = { it.id }) { frame ->
                FrameRow(frame = frame)
            }
        }
    }
}

/**
 * Status, plus the connect affordance.
 *
 * Every failure reason gets its own title and body. This screen depends on a third-party echo
 * server, and a generic "connection failed" would leave a reader unable to tell an outage apart
 * from PseudoCharles failing to capture frames — which is the exact thing the screen exists to
 * demonstrate.
 */
@Composable
private fun StatusCard(
    state: LiveFeedState,
    onAction: (LiveFeedAction) -> Unit
) {
    val status = state.status
    val failed = status as? LiveFeedStatus.Failed

    val container = when {
        failed != null -> MaterialTheme.colorScheme.errorContainer
        status is LiveFeedStatus.Connected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when {
        failed != null -> MaterialTheme.colorScheme.onErrorContainer
        status is LiveFeedStatus.Connected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = statusTitle(status),
                style = MaterialTheme.typography.titleMedium
            )

            statusBody(status)?.let { body ->
                Text(text = body, style = MaterialTheme.typography.bodyMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.isConnected) {
                    OutlinedButton(onClick = { onAction(LiveFeedAction.OnDisconnect) }) {
                        Text(stringResource(R.string.live_feed_disconnect))
                    }
                } else {
                    Button(
                        onClick = { onAction(LiveFeedAction.OnConnect) },
                        enabled = !state.isBusy
                    ) {
                        Text(
                            stringResource(
                                if (failed != null) R.string.live_feed_retry
                                else R.string.live_feed_connect
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun statusTitle(status: LiveFeedStatus): String = when (status) {
    LiveFeedStatus.Disconnected -> stringResource(R.string.live_feed_status_disconnected)
    LiveFeedStatus.Connecting -> stringResource(R.string.live_feed_status_connecting)
    LiveFeedStatus.Connected -> stringResource(R.string.live_feed_status_connected)
    is LiveFeedStatus.Failed -> when (val reason = status.reason) {
        LiveFeedFailure.Unreachable ->
            stringResource(R.string.live_feed_status_unreachable_title)
        LiveFeedFailure.NoNetwork ->
            stringResource(R.string.live_feed_status_no_network_title)
        is LiveFeedFailure.Rejected ->
            stringResource(R.string.live_feed_status_rejected_title, reason.code)
        is LiveFeedFailure.ClosedByServer ->
            stringResource(R.string.live_feed_status_closed_title)
        is LiveFeedFailure.Unknown ->
            stringResource(R.string.live_feed_status_unknown_title)
    }
}

@Composable
private fun statusBody(status: LiveFeedStatus): String? = when (status) {
    is LiveFeedStatus.Failed -> when (val reason = status.reason) {
        LiveFeedFailure.Unreachable ->
            stringResource(R.string.live_feed_status_unreachable_body)
        LiveFeedFailure.NoNetwork ->
            stringResource(R.string.live_feed_status_no_network_body)
        is LiveFeedFailure.Rejected ->
            stringResource(R.string.live_feed_status_rejected_body)
        is LiveFeedFailure.ClosedByServer ->
            stringResource(R.string.live_feed_status_closed_body, reason.code, reason.reason)
        is LiveFeedFailure.Unknown ->
            stringResource(R.string.live_feed_status_unknown_body, reason.message)
    }
    else -> null
}

@Composable
private fun SendControls(
    state: LiveFeedState,
    onAction: (LiveFeedAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.live_feed_auto_emit),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.autoEmit,
                onCheckedChange = { onAction(LiveFeedAction.OnAutoEmitToggle(it)) },
                enabled = state.isConnected
            )
        }

        // Row + horizontalScroll rather than FlowRow: the buttons stay on one predictable line.
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { onAction(LiveFeedAction.OnSendCheckIn) },
                enabled = state.isConnected,
                label = { Text(stringResource(R.string.live_feed_send_checkin)) }
            )
            AssistChip(
                onClick = { onAction(LiveFeedAction.OnSendPlainText) },
                enabled = state.isConnected,
                label = { Text(stringResource(R.string.live_feed_send_text)) }
            )
            AssistChip(
                onClick = { onAction(LiveFeedAction.OnSendBinary) },
                enabled = state.isConnected,
                label = { Text(stringResource(R.string.live_feed_send_binary)) }
            )
        }
    }
}

@Composable
private fun FrameRow(frame: LiveFeedFrameUi) {
    val outbound = frame.direction == LiveFeedDirection.OUTBOUND
    val accent = if (outbound) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            color = accent.copy(alpha = 0.15f)
        ) {
            Icon(
                imageVector = if (outbound) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = if (outbound) "Outbound" else "Inbound",
                tint = accent,
                modifier = Modifier.padding(6.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = frame.event ?: frame.kind.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
                Text(
                    text = frame.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = frame.payload,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiveFeedScreenConnectedPreview() {
    LiveFeedScreen(
        state = LiveFeedState(
            endpoint = "wss://echo.websocket.org",
            status = LiveFeedStatus.Connected,
            inspectorAvailable = true,
            autoEmit = true,
            frames = persistentListOf(
                LiveFeedFrameUi(
                    id = "1",
                    time = "12:04:11.204",
                    direction = LiveFeedDirection.INBOUND,
                    kind = LiveFeedFrameKind.TEXT,
                    event = "taproom:checkin",
                    payload = """42["taproom:checkin",{"brewery":"Fremont Brewing","pours":3}]"""
                ),
                LiveFeedFrameUi(
                    id = "2",
                    time = "12:04:11.101",
                    direction = LiveFeedDirection.OUTBOUND,
                    kind = LiveFeedFrameKind.TEXT,
                    event = "taproom:checkin",
                    payload = """42["taproom:checkin",{"brewery":"Fremont Brewing","pours":3}]"""
                )
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LiveFeedScreenUnreachablePreview() {
    LiveFeedScreen(
        state = LiveFeedState(
            endpoint = "wss://echo.websocket.org",
            status = LiveFeedStatus.Failed(LiveFeedFailure.Unreachable)
        ),
        onAction = {}
    )
}
