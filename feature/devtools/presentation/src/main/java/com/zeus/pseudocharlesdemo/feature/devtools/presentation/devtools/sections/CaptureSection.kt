package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsAction

@Composable
fun CaptureSection(
    trafficLoggingEnabled: Boolean,
    socketLoggingEnabled: Boolean,
    trafficCount: Int,
    socketFrameCount: Int,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    DevToolsSection(
        title = stringResource(R.string.dev_tools_capture_header),
        modifier = modifier
    ) {
        SwitchRow(
            label = stringResource(R.string.dev_tools_capture_traffic),
            checked = trafficLoggingEnabled,
            onCheckedChange = { onAction(DevToolsAction.OnTrafficLoggingToggle(it)) }
        )
        SwitchRow(
            label = stringResource(R.string.dev_tools_capture_sockets),
            checked = socketLoggingEnabled,
            onCheckedChange = { onAction(DevToolsAction.OnSocketLoggingToggle(it)) }
        )

        SectionCaption(
            stringResource(R.string.dev_tools_capture_counts, trafficCount, socketFrameCount)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { onAction(DevToolsAction.OnClearTraffic) }) {
                Text(stringResource(R.string.dev_tools_capture_clear_traffic))
            }
            TextButton(onClick = { onAction(DevToolsAction.OnClearSockets) }) {
                Text(stringResource(R.string.dev_tools_capture_clear_sockets))
            }
        }
    }
}
