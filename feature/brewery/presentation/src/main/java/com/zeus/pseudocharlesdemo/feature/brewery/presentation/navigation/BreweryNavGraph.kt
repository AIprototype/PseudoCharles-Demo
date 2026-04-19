package com.zeus.pseudocharlesdemo.feature.brewery.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail.BreweryDetailRoot
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.search.BrewerySearchRoot

fun NavGraphBuilder.breweryGraph(
    navController: NavController,
    onOpenUrl: (String) -> Unit,
    onDialPhone: (String) -> Unit
) {
    composable<BrewerySearchRoute> {
        BrewerySearchRoot(
            onNavigateToDetail = { id ->
                navController.navigate(BreweryDetailRoute(id = id))
            }
        )
    }
    composable<BreweryDetailRoute> {
        BreweryDetailRoot(
            onNavigateBack = { navController.popBackStack() },
            onOpenUrl = onOpenUrl,
            onDialPhone = onDialPhone
        )
    }
}
