# Brewery Explorer — PseudoCharles Demo

[![Maven Central](https://img.shields.io/maven-central/v/io.github.aiprototype/pseudocharles)](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://developer.android.com/about/versions/nougat)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

A demo Android app that showcases how to integrate the **PseudoCharles** SDK into a real-world project. The app uses the [Open Brewery DB](https://www.openbrewerydb.org/) API to search and browse breweries, while PseudoCharles intercepts the network layer to let you configure mock responses on-device.

---

## What is PseudoCharles?

**PseudoCharles** is an in-app mock proxy SDK for Android. It intercepts OkHttp/Retrofit network calls and lets testers configure mock HTTP status codes and JSON responses directly from the device — no desktop proxy required. Think of it as Chucker meets Charles Proxy, built into your debug builds.

**Maven Central:**

- SDK: [io.github.aiprototype:pseudocharles](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles)
- No-op (release): [io.github.aiprototype:pseudocharles-noop](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles-noop)

---

## Installation

> **Latest version:** ![Maven Central](https://img.shields.io/maven-central/v/io.github.aiprototype/pseudocharles?label=latest)
>
> Replace `{latest_version}` in the snippets below with the version shown above.

### Gradle — Kotlin DSL (Recommended)

**Using Version Catalog (`gradle/libs.versions.toml`):**

```toml
[versions]
pseudocharles = "{latest_version}"

[libraries]
pseudocharles = { group = "io.github.aiprototype", name = "pseudocharles", version.ref = "pseudocharles" }
pseudocharles-noop = { group = "io.github.aiprototype", name = "pseudocharles-noop", version.ref = "pseudocharles" }
```

Then in your module's `build.gradle.kts`:

```kotlin
dependencies {
    debugImplementation(libs.pseudocharles)
    releaseImplementation(libs.pseudocharles.noop)
}
```

**Without Version Catalog:**

```kotlin
dependencies {
    debugImplementation("io.github.aiprototype:pseudocharles:{latest_version}")
    releaseImplementation("io.github.aiprototype:pseudocharles-noop:{latest_version}")
}
```

### Gradle — Groovy DSL

```groovy
dependencies {
    debugImplementation 'io.github.aiprototype:pseudocharles:{latest_version}'
    releaseImplementation 'io.github.aiprototype:pseudocharles-noop:{latest_version}'
}
```

### Maven

```xml
<!-- Debug profile -->
<dependency>
    <groupId>io.github.aiprototype</groupId>
    <artifactId>pseudocharles</artifactId>
    <version>{latest_version}</version>
</dependency>

<!-- Release profile -->
<dependency>
    <groupId>io.github.aiprototype</groupId>
    <artifactId>pseudocharles-noop</artifactId>
    <version>{latest_version}</version>
</dependency>
```

> **Note:** The `-noop` artifact provides the same public API as the full SDK but all methods are no-ops. This means your code compiles identically in release builds with zero overhead — no `BuildConfig` checks needed.

---

## Quick Start

### 1. Add the interceptor to your OkHttpClient

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(PseudoCharles.interceptor(applicationContext))
    .build()
```

### 2. Launch the config screen

From a FAB, debug menu, shake gesture, or any trigger:

```kotlin
FloatingActionButton(onClick = {
    context.startActivity(PseudoCharles.getLaunchIntent(context))
}) {
    Icon(Icons.Default.BugReport, contentDescription = "Mock config")
}
```

### 3. (Optional) Embed the config screen as a Composable

If you prefer to host the config UI inline (e.g., in a debug drawer or bottom sheet):

```kotlin
@Composable
fun DebugPanel() {
    PseudoCharles.ConfigScreen()
}
```

> **Note:** `interceptor(context)` must be called at least once before showing `ConfigScreen()` so the internal data source is initialized.

### Using with Ktor

If your project uses Ktor instead of raw OkHttp/Retrofit, use the **OkHttp engine** and pass a preconfigured client:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(PseudoCharles.interceptor(context))
    .build()

val ktorClient = HttpClient(OkHttp) {
    engine { preconfigured = okHttpClient }
    // ... install ContentNegotiation, Logging, etc.
}
```

This is exactly how this demo app integrates PseudoCharles — see [`HttpClientFactory.kt`](core/data/src/main/java/com/zeus/pseudocharlesdemo/core/data/HttpClientFactory.kt).

---

## Public API Reference

All APIs are accessed through the `PseudoCharles` singleton object (`com.zeus.pseudocharles.PseudoCharles`).

### Core

| Method | Returns | Description |
|---|---|---|
| `interceptor(context)` | `Interceptor` | OkHttp interceptor that serves mock responses for endpoints with active mocks. Add via `addInterceptor()`, not `addNetworkInterceptor()`. |
| `getLaunchIntent(context)` | `Intent` | Launches the standalone PseudoCharles config Activity. |
| `ConfigScreen()` | `@Composable` | Renders the full config UI inline. Requires `interceptor()` to have been called first. |

### State

| Method / Property | Returns | Description |
|---|---|---|
| `isEnabled()` | `Boolean` | `true` after `interceptor()` has been called; always `false` in the `-noop` release variant. Use this to conditionally show debug UI. |
| `isActive()` | `Boolean` | `true` if any endpoint currently has an active mock configured. Reads from in-memory cache — safe to call from the main thread. |

### Mock Management

| Method | Returns | Description |
|---|---|---|
| `clearAll(context)` | `Unit` | Clears all mock configurations and observed endpoints. Async — safe from any thread. |

### Embedded Web Server

Configure mocks remotely from a browser on the same Wi-Fi network.

| Method | Returns | Description |
|---|---|---|
| `startServer(context)` | `Unit` | Starts the embedded web server (protected by a 4-digit PIN). |
| `stopServer()` | `Unit` | Stops the server. |
| `serverUrl()` | `String?` | URL of the running server (e.g., `http://192.168.1.15:8080`), or `null` if not running. |
| `isServerRunning()` | `Boolean` | Whether the server is currently active. |

### Traffic Inspector

| Method / Property | Type | Description |
|---|---|---|
| `trafficLoggingEnabled` | `Boolean` (property) | Toggle request/response traffic capture. Default: `true`. Set to `false` to reduce memory in long sessions. |
| `clearTrafficLog()` | `Unit` | Clears all captured traffic entries from memory. |
| `getTrafficSnapshot()` | `List<TrafficEntry>` | Returns a point-in-time snapshot of captured traffic (newest first). |

---

## About This Demo App

This app searches the [Open Brewery DB](https://www.openbrewerydb.org/) and displays brewery details. On cold start the search screen seeds itself with a handful of random breweries (`GET /breweries/random`) so there's something to look at before the user types. It demonstrates PseudoCharles integration in a modern, multi-module Android architecture.

### How PseudoCharles is wired

- **Single `main/` wiring:** `PseudoCharlesInterceptorProvider` always installs `PseudoCharles.interceptor(context)` into the shared `OkHttpClient`. No debug/release source-set split is needed — in release, the `-noop` artifact makes the call a harmless no-op and `isEnabled()` stays `false`.
- **Gradle variant dependency:** `debugImplementation` pulls the full SDK; `releaseImplementation` pulls `-noop`.
- **Init timing:** `PseudoCharlesDemoApp.onCreate()` calls `initializeMockProxy(this)` before `startKoin { }` so `isEnabled()` is already `true` by the time the Scaffold composes the FAB slot.
- **Debug FAB:** `MainActivity` shows a floating action button (gated by `PseudoCharles.isEnabled()`) that opens the config screen. A badge appears when mocks are active (`PseudoCharles.isActive()`).

### Module Structure

```
:app                                   Application, Koin setup, NavHost, PseudoCharles FAB
:core:domain                           Result, DataError, Error (pure Kotlin, no Android)
:core:data                             HttpClientFactory (Ktor + OkHttp), safeCall, Koin module
:core:presentation                     UiText, error-to-UiText mapping
:core:design-system                    Material3 theme, colors, typography
:feature:brewery:domain                Brewery model, BreweryType, BreweryRepository interface
:feature:brewery:data                  DTOs, OpenBreweryApi (Ktor), KtorBreweryRepository, mappers
:feature:brewery:presentation          MVI (search + detail), Compose screens, nav graph
```

### Architecture

- **Presentation:** MVI — single `State` data class, sealed `Action` interface, one-time `Event` via `Channel`
- **Navigation:** Type-safe Compose Navigation with `@Serializable` route objects
- **DI:** Koin — one module per feature layer, `viewModelOf` / `singleOf`
- **Networking:** Ktor `HttpClient` with OkHttp engine (required for PseudoCharles interceptor)
- **Error handling:** `Result<D, E>` with `DataError.Network` enum, mapped to `UiText` for display

---

## Tech Stack

| Technology | Version |
|---|---|
| Kotlin | 2.2.10 |
| AGP | 9.1.0 |
| Compose BOM | 2026.03.01 |
| Material3 | via Compose BOM |
| Ktor Client | 3.1.3 |
| OkHttp | 4.12.0 |
| Koin | 4.0.0 |
| Navigation Compose | 2.8.4 |
| PseudoCharles | ![Maven Central](https://img.shields.io/maven-central/v/io.github.aiprototype/pseudocharles?label=latest) |
| Min SDK | 24 |
| Target/Compile SDK | 36 |

---

## Build & Run

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Install on connected device/emulator
./gradlew :app:installDebug

# Run unit tests
./gradlew test

# Verify Gradle config integrity
./gradlew prepareKotlinBuildScriptModel
```

---

## License

This demo app is licensed under the [MIT License](LICENSE).

The PseudoCharles SDK itself is licensed separately under the Apache License 2.0 — see its [Maven Central listing](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles) for details.
