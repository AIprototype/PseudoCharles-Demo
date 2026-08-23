package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsAction

@Composable
fun MockConfigSection(
    hasActiveMock: Boolean,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    DevToolsSection(
        title = stringResource(R.string.dev_tools_mocks_header),
        modifier = modifier
    ) {
        Text(
            text = stringResource(
                if (hasActiveMock) R.string.dev_tools_mock_active
                else R.string.dev_tools_mock_inactive
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasActiveMock) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Button(
            onClick = { onAction(DevToolsAction.OnOpenConfig) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dev_tools_open_config))
        }

        TextButton(onClick = { onAction(DevToolsAction.OnClearMocks) }) {
            Text(stringResource(R.string.dev_tools_clear_mocks))
        }
    }
}
