package com.zeus.pseudocharlesdemo.core.domain

/** How a simulated network failure presents itself. Mirrors the SDK's `FailureMode`. */
enum class NetworkFailureMode {
    /** Fails fast, like a refused connection. */
    IMMEDIATE,

    /** The "black hole" — hangs for the read timeout, then fails. The one that breaks real apps. */
    TIMEOUT
}

/**
 * Free-form network conditions, mirroring the SDK's `NetworkProfile`.
 *
 * Bandwidth is decimal kbps (1 kbps = 1000 bits), matching Chrome DevTools and carrier marketing.
 */
data class CustomNetworkSpec(
    val downKbps: Int,
    val upKbps: Int,
    val latencyMs: Int,
    val jitterPercent: Int = 20,
    val failureRatePercent: Int = 0,
    val failureMode: NetworkFailureMode = NetworkFailureMode.TIMEOUT
)
