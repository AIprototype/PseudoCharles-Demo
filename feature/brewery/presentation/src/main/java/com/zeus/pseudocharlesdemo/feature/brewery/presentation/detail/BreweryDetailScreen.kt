package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.R
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.model.BreweryUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreweryDetailScreen(
    state: BreweryDetailState,
    onAction: (BreweryDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.brewery?.name ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(BreweryDetailAction.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage.asString(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.brewery != null -> {
                    BreweryDetailContent(
                        brewery = state.brewery,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun BreweryDetailContent(
    brewery: BreweryUi,
    onAction: (BreweryDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = brewery.typeLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        if (brewery.address.isNotBlank()) {
            DetailSection(
                icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                label = stringResource(R.string.label_address),
                value = brewery.address
            )
        }

        if (brewery.location.isNotBlank()) {
            DetailSection(
                icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                label = stringResource(R.string.label_location),
                value = brewery.location
            )
        }

        HorizontalDivider()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            brewery.phone?.let { phone ->
                AssistChip(
                    onClick = { onAction(BreweryDetailAction.OnCallPhone(phone)) },
                    label = { Text(stringResource(R.string.action_call)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                )
            }
            brewery.websiteUrl?.let { url ->
                AssistChip(
                    onClick = { onAction(BreweryDetailAction.OnOpenWebsite(url)) },
                    label = { Text(stringResource(R.string.action_website)) },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                )
            }
        }
    }
}

@Composable
private fun DetailSection(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
