package com.zeus.pseudocharlesdemo.core.domain

/**
 * Lifecycle of the PseudoCharles remote-config web dashboard, mirrored off the SDK's
 * `ServerRuntimeState` so no feature module has to import the server artifact.
 */
sealed interface DashboardState {
    data object Stopped : DashboardState

    data class Running(val url: String, val port: Int) : DashboardState

    data class Failed(val message: String) : DashboardState
}
