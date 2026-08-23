package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.core.domain.DashboardState
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections.CaptureSection
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections.DashboardSection
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections.MockConfigSection
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections.NetworkSpeedSection
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections.NotificationSection

/**
 * One card per PseudoCharles capability, driven entirely through
 * [com.zeus.pseudocharlesdemo.core.domain.MockProxyController] — this module never imports the SDK.
 */
@Composable
fun DevToolsScreen(
    state: DevToolsState,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.available) {
        Text(
            text = stringResource(R.string.dev_tools_unavailable),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MockConfigSection(hasActiveMock = state.hasActiveMock, onAction = onAction)
        }
        item {
            NetworkSpeedSection(current = state.networkSpeed, onAction = onAction)
        }
        item {
            DashboardSection(dashboard = state.dashboard, onAction = onAction)
        }
        item {
            NotificationSection(
                enabled = state.persistentNotificationRequested,
                permissionBlocked = state.notificationPermissionBlocked,
                onAction = onAction
            )
        }
        item {
            CaptureSection(
                trafficLoggingEnabled = state.trafficLoggingEnabled,
                socketLoggingEnabled = state.socketLoggingEnabled,
                trafficCount = state.trafficCount,
                socketFrameCount = state.socketFrameCount,
                onAction = onAction
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DevToolsScreenPreview() {
    DevToolsScreen(
        state = DevToolsState(
            available = true,
            hasActiveMock = true,
            networkSpeed = NetworkSpeedProfile.GPRS,
            dashboard = DashboardState.Running("http://192.168.1.15:8432", 8432),
            trafficCount = 42,
            socketFrameCount = 18,
            persistentNotificationRequested = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DevToolsScreenUnavailablePreview() {
    DevToolsScreen(state = DevToolsState(available = false), onAction = {})
}
