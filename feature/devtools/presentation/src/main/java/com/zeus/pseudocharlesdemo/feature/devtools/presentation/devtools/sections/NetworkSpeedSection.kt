package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.sections

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.R
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools.DevToolsAction
import kotlinx.collections.immutable.persistentListOf

/**
 * A curated subset of the SDK's 15 presets — enough to span the range without a wall of chips.
 * The full set, plus the free-form Custom editor, lives in PseudoCharles → Settings.
 */
private val presets = persistentListOf(
    NetworkSpeedProfile.OFF,
    NetworkSpeedProfile.OFFLINE,
    NetworkSpeedProfile.GPRS,
    NetworkSpeedProfile.THREE_G_SLOW,
    NetworkSpeedProfile.FOUR_G_WEAK,
    NetworkSpeedProfile.FOUR_G,
    NetworkSpeedProfile.WIFI,
    NetworkSpeedProfile.FIVE_G,
    NetworkSpeedProfile.VERY_BAD
)

@Composable
fun NetworkSpeedSection(
    current: NetworkSpeedProfile,
    onAction: (DevToolsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    DevToolsSection(
        title = stringResource(R.string.dev_tools_network_header),
        modifier = modifier
    ) {
        SectionCaption(stringResource(R.string.dev_tools_network_body))

        // Row + horizontalScroll rather than FlowRow — keeps the chips on one predictable line
        // regardless of label length, and avoids Foundation's FlowRow entirely.
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { profile ->
                FilterChip(
                    selected = profile == current,
                    onClick = { onAction(DevToolsAction.OnNetworkSpeedSelected(profile)) },
                    label = { Text(profile.label) }
                )
            }
        }

        TextButton(onClick = { onAction(DevToolsAction.OnFlakyProfileSelected) }) {
            Text(stringResource(R.string.dev_tools_network_flaky))
        }
        SectionCaption(stringResource(R.string.dev_tools_network_flaky_desc))

        Text(
            text = stringResource(R.string.dev_tools_network_current, current.label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        val down = current.downKbps
        val latency = current.latencyMs
        if (down != null && latency != null) {
            SectionCaption(stringResource(R.string.dev_tools_network_detail, down, latency))
        }
    }
}
