package com.zeus.pseudocharlesdemo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile

/**
 * High-contrast reminder that requests are being deliberately slowed.
 *
 * Without it, a GPRS-throttled brewery search is indistinguishable from a real regression — which
 * is exactly the confusion the SDK's own banner exists to prevent. Lives in the app shell rather
 * than a feature module so nothing below `:app` has to know about the mock proxy.
 */
@Composable
fun ThrottleBanner(
    profile: NetworkSpeedProfile,
    onTurnOff: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = profile.isThrottling, modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.throttle_banner, profile.label),
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(onClick = onTurnOff) {
                    Text(stringResource(R.string.throttle_banner_action))
                }
            }
        }
    }
}
