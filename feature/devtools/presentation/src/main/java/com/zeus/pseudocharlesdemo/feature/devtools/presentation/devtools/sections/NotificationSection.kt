package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsAction

/**
 * Toggles the SDK's Charles-style traffic notification.
 *
 * The disclaimer under the switch is not decoration. `setPersistentNotificationEnabled` is
 * write-only — the SDK exposes no getter — so this switch can only ever show what *this app* last
 * requested. Change it in PseudoCharles' own Settings, or revoke the notification permission, and
 * the two drift apart. Saying so here is cheaper than fielding the bug report.
 */
@Composable
fun NotificationSection(
    enabled: Boolean,
    permissionBlocked: Boolean,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    DevToolsSection(
        title = stringResource(R.string.dev_tools_notification_header),
        modifier = modifier
    ) {
        SwitchRow(
            label = stringResource(R.string.dev_tools_notification_toggle),
            checked = enabled,
            onCheckedChange = { onAction(DevToolsAction.OnNotificationToggle(it)) }
        )

        SectionCaption(stringResource(R.string.dev_tools_notification_disclaimer))

        if (permissionBlocked) {
            Text(
                text = stringResource(R.string.dev_tools_notification_denied),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            TextButton(onClick = { onAction(DevToolsAction.OnOpenNotificationSettings) }) {
                Text(stringResource(R.string.dev_tools_notification_open_settings))
            }
        }
    }
}
