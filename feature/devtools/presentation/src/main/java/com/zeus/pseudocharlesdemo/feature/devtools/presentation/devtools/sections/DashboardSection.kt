package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.core.domain.DashboardState
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsAction

/**
 * Start/stop the embedded Ktor dashboard and surface the URL to type into a laptop browser.
 *
 * Backed by the SDK's real `StateFlow`, so [DashboardState.Failed] (port conflict, crash-loop
 * guard) shows up here rather than the switch silently staying on.
 */
@Composable
fun DashboardSection(
    dashboard: DashboardState,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current

    DevToolsSection(
        title = stringResource(R.string.dev_tools_dashboard_header),
        modifier = modifier
    ) {
        SwitchRow(
            label = stringResource(R.string.dev_tools_dashboard_toggle),
            checked = dashboard is DashboardState.Running,
            onCheckedChange = { onAction(DevToolsAction.OnDashboardToggle(it)) }
        )

        when (dashboard) {
            DashboardState.Stopped ->
                SectionCaption(stringResource(R.string.dev_tools_dashboard_stopped))

            is DashboardState.Running -> {
                Text(
                    text = stringResource(R.string.dev_tools_dashboard_running, dashboard.url),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(dashboard.url))
                            onAction(DevToolsAction.OnCopyDashboardUrl(dashboard.url))
                        }
                    ) {
                        Text(stringResource(R.string.dev_tools_dashboard_copy))
                    }
                }
                SectionCaption(stringResource(R.string.dev_tools_dashboard_pin))
                SectionCaption(stringResource(R.string.dev_tools_dashboard_chrome))
            }

            is DashboardState.Failed ->
                Text(
                    text = stringResource(R.string.dev_tools_dashboard_failed, dashboard.message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
        }
    }
}
