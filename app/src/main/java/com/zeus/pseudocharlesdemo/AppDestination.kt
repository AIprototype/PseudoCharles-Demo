package com.zeus.pseudocharlesdemo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.vector.ImageVector
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation.BrewerySearchRoute
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.navigation.DevToolsRoute
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.navigation.LiveFeedRoute

/**
 * The bottom-navigation destinations.
 *
 * [requiresMockProxy] entries exist only to exercise the SDK, so they are hidden when the no-op
 * artifact is in play. A release build therefore renders exactly the single-screen app it always
 * did — no dead tabs, no debug affordances shipped to users.
 */
enum class AppDestination(
    val route: Any,
    val labelRes: Int,
    val icon: ImageVector,
    val requiresMockProxy: Boolean
) {
    EXPLORE(BrewerySearchRoute, R.string.tab_explore, Icons.Default.Search, false),
    LIVE(LiveFeedRoute, R.string.tab_live, Icons.Default.Sensors, true),
    DEV_TOOLS(DevToolsRoute, R.string.tab_dev_tools, Icons.Default.BugReport, true)
}
