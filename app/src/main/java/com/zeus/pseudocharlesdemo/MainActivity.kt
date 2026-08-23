package com.zeus.pseudocharlesdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zeus.pseudocharlesdemo.core.domain.MockProxyController
import com.zeus.pseudocharlesdemo.core.domain.MockProxyStatus
import com.zeus.pseudocharlesdemo.core.domain.NetworkSpeedProfile
import com.zeus.pseudocharlesdemo.core.designsystem.theme.BreweryExplorerTheme
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.BreweryDetailRoute
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.BrewerySearchRoute
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.breweryGraph
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.navigation.devToolsGraph
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreweryExplorerTheme {
                BreweryExplorerApp(
                    onOpenUrl = { url -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) },
                    onDialPhone = { phone -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BreweryExplorerApp(
    onOpenUrl: (String) -> Unit,
    onDialPhone: (String) -> Unit,
    mockProxy: MockProxyController = koinInject()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val status by mockProxy.status.collectAsStateWithLifecycle(initialValue = MockProxyStatus())

    // isEnabled()/isActive()/networkProfile() are plain calls, not flows, so state changed inside
    // the SDK's own UI only lands here when we resume. One read on entry, one per resume.
    LaunchedEffect(Unit) { mockProxy.refresh() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { mockProxy.refresh() }

    val destinations = remember(status.available) {
        AppDestination.entries.filter { !it.requiresMockProxy || status.available }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // The brewery detail screen brings its own Scaffold and top bar. Keeping the bottom bar there
    // would both double-count insets and put bottom navigation on a pushed detail view.
    val isDetail = currentDestination?.hasRoute(BreweryDetailRoute::class) == true
    val isExplore = currentDestination?.hasRoute(BrewerySearchRoute::class) == true
    val showBottomBar = destinations.size > 1 && !isDetail

    Scaffold(
        topBar = {
            if (!isDetail) TopAppBar(title = { Text(stringResource(R.string.app_bar_title)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination?.hasRoute(
                                destination.route::class
                            ) == true,
                            onClick = { navController.navigateToTab(destination) },
                            icon = {
                                Icon(destination.icon, contentDescription = null)
                            },
                            label = { Text(stringResource(destination.labelRes)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Explore only. On the Dev Tools tab it would duplicate "Open PseudoCharles"; on Live
            // it would cover the frame list. Kept here because this one-liner is still the smallest
            // possible integration, and it is what most apps will copy.
            if (status.available && isExplore) {
                FloatingActionButton(onClick = { mockProxy.openMockConfig() }) {
                    BadgedBox(badge = { if (status.hasActiveMock) Badge() }) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = stringResource(R.string.cd_open_mock_config)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ThrottleBanner(
                profile = status.networkSpeed,
                onTurnOff = {
                    mockProxy.setNetworkSpeed(NetworkSpeedProfile.OFF)
                    mockProxy.refresh()
                }
            )

            NavHost(
                navController = navController,
                startDestination = BrewerySearchRoute
            ) {
                breweryGraph(
                    navController = navController,
                    onOpenUrl = onOpenUrl,
                    onDialPhone = onDialPhone
                )
                devToolsGraph(snackbarHostState = snackbarHostState)
            }
        }
    }
}

/** Standard single-top tab switch: no back-stack pile-up, state preserved per tab. */
private fun NavHostController.navigateToTab(destination: AppDestination) {
    navigate(destination.route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
