package com.zeus.pseudocharlesdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zeus.pseudocharles.PseudoCharles
import com.zeus.pseudocharlesdemo.core.designsystem.theme.BreweryExplorerTheme
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.BrewerySearchRoute
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.breweryGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreweryExplorerTheme {
                BreweryExplorerApp(
                    onOpenUrl = { url -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) },
                    onDialPhone = { phone -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))) },
                    onOpenMockConfig = {
                        startActivity(PseudoCharles.getLaunchIntent(this@MainActivity))
                    }
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
    onOpenMockConfig: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Brewery Explorer") })
        },
        floatingActionButton = {
            if (PseudoCharles.isEnabled()) {
                FloatingActionButton(onClick = onOpenMockConfig) {
                    BadgedBox(
                        badge = {
                            if (PseudoCharles.isActive()) Badge()
                        }
                    ) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = "Open mock configuration"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = BrewerySearchRoute
            ) {
                breweryGraph(
                    navController = navController,
                    onOpenUrl = onOpenUrl,
                    onDialPhone = onDialPhone
                )
            }
        }
    }
}
