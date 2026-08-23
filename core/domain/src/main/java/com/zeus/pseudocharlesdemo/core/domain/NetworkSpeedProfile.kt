package com.zeus.pseudocharlesdemo.core.domain

/**
 * Demo-side mirror of the PseudoCharles `NetworkProfileId` presets.
 *
 * Declared here, in a pure-JVM module, so feature modules can offer a network-speed picker without
 * importing `com.zeus.pseudocharles` — the SDK is a `debugImplementation` artifact and only `:app`
 * is allowed to see it. `:app` maps this to the SDK enum with an exhaustive `when`, so adding a
 * preset here is a compile error there until it is mapped.
 *
 * [downKbps] and [latencyMs] are the SDK's published values, carried along purely so the UI can
 * describe a preset without asking the SDK. They are `null` for the two non-throttling entries.
 */
enum class NetworkSpeedProfile(
    val label: String,
    val downKbps: Int?,
    val latencyMs: Int?
) {
    OFF("Off", null, null),
    OFFLINE("Offline", null, null),
    GPRS("GPRS", 50, 500),
    EDGE("EDGE", 240, 400),
    THREE_G_SLOW("Slow 3G", 400, 400),
    THREE_G("3G", 780, 200),
    FOUR_G_WEAK("4G weak", 1_600, 150),
    FOUR_G_CONGESTED("4G congested", 4_000, 120),
    FOUR_G("4G", 12_000, 70),
    FOUR_G_STRONG("4G strong", 30_000, 50),
    FIVE_G("5G", 150_000, 30),
    FIVE_G_MMWAVE("5G mmWave", 500_000, 15),
    WIFI("Wi-Fi", 40_000, 20),
    VERY_BAD("Very bad network", 1_000, 500),
    CUSTOM("Custom", null, null);

    /** True when this profile actually paces traffic, i.e. everything except [OFF]. */
    val isThrottling: Boolean get() = this != OFF
}
