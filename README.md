# Brewery Explorer, PseudoCharles Demo

[![Maven Central](https://img.shields.io/maven-central/v/io.github.aiprototype/pseudocharles)](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://developer.android.com/about/versions/nougat)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

A demo Android app that showcases how to integrate the **PseudoCharles** SDK into a real-world project. The app uses the [Open Brewery DB](https://www.openbrewerydb.org/) API to search and browse breweries, while PseudoCharles intercepts the network layer to let you configure mock responses on-device.

---

## What is PseudoCharles?

**PseudoCharles** is an in-app mock proxy SDK for Android. It intercepts OkHttp/Retrofit network calls and lets testers configure mock HTTP status codes and JSON responses directly from the device , no desktop proxy required. Think of it as Chucker meets Charles Proxy, built into your debug builds.

**Maven Central:**

- Core SDK: [io.github.aiprototype:pseudocharles](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles)
- Core no-op (release): [io.github.aiprototype:pseudocharles-noop](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles-noop)
- Web dashboard (optional): [io.github.aiprototype:pseudocharles-server](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles-server)
- Web dashboard no-op (release): [io.github.aiprototype:pseudocharles-server-noop](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles-server-noop)

The server pair is **optional**. Depend on the core pair alone and you get a completely Ktor-free
dependency tree; add the server pair when you want the browser dashboard. This demo uses all four.

---

## Installation

> **Latest version:** ![Maven Central](https://img.shields.io/maven-central/v/io.github.aiprototype/pseudocharles?label=latest)
>
> Replace `{latest_version}` in the snippets below with the version shown above.

### Gradle , Kotlin DSL (Recommended)

**Using Version Catalog (`gradle/libs.versions.toml`):**

```toml
[versions]
pseudocharles = "{latest_version}"

[libraries]
pseudocharles = { group = "io.github.aiprototype", name = "pseudocharles", version.ref = "pseudocharles" }
pseudocharles-noop = { group = "io.github.aiprototype", name = "pseudocharles-noop", version.ref = "pseudocharles" }
pseudocharles-server = { group = "io.github.aiprototype", name = "pseudocharles-server", version.ref = "pseudocharles" }
pseudocharles-server-noop = { group = "io.github.aiprototype", name = "pseudocharles-server-noop", version.ref = "pseudocharles" }
```

Then in your module's `build.gradle.kts`:

```kotlin
dependencies {
    debugImplementation(libs.pseudocharles)
    debugImplementation(libs.pseudocharles.server)          // optional: browser dashboard
    releaseImplementation(libs.pseudocharles.noop)
    releaseImplementation(libs.pseudocharles.server.noop)   // pair with the line above
}
```

**Without Version Catalog:**

```kotlin
dependencies {
    debugImplementation("io.github.aiprototype:pseudocharles:{latest_version}")
    debugImplementation("io.github.aiprototype:pseudocharles-server:{latest_version}")
    releaseImplementation("io.github.aiprototype:pseudocharles-noop:{latest_version}")
    releaseImplementation("io.github.aiprototype:pseudocharles-server-noop:{latest_version}")
}
```

### Gradle , Groovy DSL

```groovy
dependencies {
    debugImplementation 'io.github.aiprototype:pseudocharles:{latest_version}'
    debugImplementation 'io.github.aiprototype:pseudocharles-server:{latest_version}'
    releaseImplementation 'io.github.aiprototype:pseudocharles-noop:{latest_version}'
    releaseImplementation 'io.github.aiprototype:pseudocharles-server-noop:{latest_version}'
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

> **Note:** The `-noop` artifact provides the same public API as the full SDK but all methods are no-ops. This means your code compiles identically in release builds with zero overhead , no `BuildConfig` checks needed.

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

This is exactly how this demo app integrates PseudoCharles , see [`HttpClientFactory.kt`](core/data/src/main/java/com/zeus/pseudocharlesdemo/core/data/HttpClientFactory.kt).

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

| Method / Property | Returns | Description                                                                                                                           |
|---|---|---------------------------------------------------------------------------------------------------------------------------------------|
| `isEnabled()` | `Boolean` | `true` after `interceptor()` has been called; always `false` in the `-noop` release variant. Use this to conditionally show debug UI. |
| `isActive()` | `Boolean` | `true` if any endpoint currently has an active mock configured. Reads from in-memory cache , safe to call from the main thread.       |

### Mock Management

| Method | Returns | Description                                                                          |
|---|---|--------------------------------------------------------------------------------------|
| `clearAll(context)` | `Unit` | Clears all mock configurations and observed endpoints. Async , safe from any thread. |

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

### Socket Inspection (WebSocket / Socket.IO)

An OkHttp `Interceptor` only ever sees the HTTP upgrade handshake , frames bypass the interceptor
chain entirely , so socket capture uses a separate seam: a decorated `WebSocket.Factory`.

| Method / Property | Type | Description                                                                                                                                                                       |
|---|---|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `webSocketFactory(delegate)` | `WebSocket.Factory` | Wraps any `WebSocket.Factory` (an `OkHttpClient` is one) so frames are captured. View-only , frames are forwarded unchanged. Returns `delegate` untouched in the `-noop` variant. |
| `socketLoggingEnabled` | `Boolean` (property) | Toggle socket frame capture. Default: `true`.                                                                                                                                     |
| `clearSocketTraffic()` | `Unit` | Clears captured frames from memory.                                                                                                                                               |
| `getSocketTrafficSnapshot()` | `List<SocketFrame>` | Point-in-time snapshot of captured frames.                                                                                                                                        |

```kotlin
// Raw OkHttp WebSocket , what this demo does, see EchoLiveFeedClient.kt
val factory = PseudoCharles.webSocketFactory(okHttpClient)
factory.newWebSocket(request, listener)

// Socket.IO Java client , route its transport through the same wrapper
val options = IO.Options().apply {
    webSocketFactory = PseudoCharles.webSocketFactory(okHttpClient)
    callFactory = okHttpClient
}
```

> **Ktor is not supported here.** Ktor's OkHttp engine calls `newWebSocket` internally and exposes no
> factory seam, and the CIO engine isn't OkHttp at all. HTTP traffic through Ktor is captured
> normally via the interceptor , only *socket frames* are out of reach.

### Mocked Network Speed

Makes every request through the interceptor behave as if it were on a chosen mobile network, with
**no code change in your app**. Normally driven from Settings; the programmatic form exists so tests
(and demos like this one) can drive it.

| Method | Returns | Description                                                                  |
|---|---|------------------------------------------------------------------------------|
| `setNetworkProfile(context, NetworkProfileId)` | `Unit` | Applies one of 15 presets. Persisted across process death.                   |
| `setCustomNetworkProfile(context, NetworkProfile)` | `Unit` | Stores free-form conditions. Select `NetworkProfileId.CUSTOM` to apply them. |
| `networkProfile()` | `NetworkProfileId` | Currently active preset. Reads an in-memory field , no disk.                 |

Presets: `OFF`, `OFFLINE`, `GPRS`, `EDGE`, `THREE_G_SLOW`, `THREE_G`, `FOUR_G_WEAK`,
`FOUR_G_CONGESTED`, `FOUR_G`, `FOUR_G_STRONG`, `FIVE_G`, `FIVE_G_MMWAVE`, `WIFI`, `VERY_BAD`,
`CUSTOM`. Values are real-world measured medians, not carrier spec sheets.

> **Gotcha this demo hit:** the write goes through DataStore and is mirrored back into the field
> `networkProfile()` reads, so re-reading *immediately* after a write still returns the previous
> value. Reflect the requested profile optimistically and reconcile a moment later , see
> [`PseudoCharlesMockProxyController.kt`](app/src/main/java/com/zeus/pseudocharlesdemo/PseudoCharlesMockProxyController.kt).

### Persistent Traffic Notification

| Method | Returns | Description |
|---|---|---|
| `setPersistentNotificationEnabled(context, enabled)` | `Unit` | Shows/hides the Charles-style traffic notification. Off by default. |

> **Write-only.** There is no matching getter, so a host app that surfaces this as a switch owns the
> displayed state and must expect it to drift from reality. This demo says so directly under the
> toggle rather than pretending the two are in sync.

On Android 13+ the **host app** must hold `POST_NOTIFICATIONS` for anything to appear , the call
itself never throws if the permission is missing. See
[`DevToolsRoot.kt`](feature/devtools/presentation/src/main/java/com/zeus/pseudocharlesdemo/feature/devtools/presentation/devtools/DevToolsRoot.kt)
for the full request → rationale → app-settings flow.

### Feature-module entry point (`:pseudocharles-server`)

```kotlin
public object PseudoCharlesServer {
    val state: StateFlow<ServerRuntimeState>   // Stopped / Running(url, port) / Error(message)
    fun start(context: Context)
    fun stop()
}
```

`ServerRuntimeState` is defined in **core**, so you can collect this flow without importing anything
from the server module , and it still compiles against `-server-noop` in release. This demo prefers
it over `startServer()`/`isServerRunning()` because a real state flow surfaces port conflicts and
the crash-loop guard instead of leaving a switch stuck on.

---

## What This Demo Shows, and Where

Every headline capability of the SDK is reachable in a few taps. **Live** and **Dev Tools** only
appear in debug builds , in release the no-op artifacts make `isEnabled()` false and the app renders
as a plain single-screen brewery browser.

| SDK capability | Where in the app | What to look for |
|---|---|---|
| Mock config UI | Dev Tools → **Open PseudoCharles**, or the bug FAB on Explore | Endpoints auto-discovered from real traffic |
| **Mocked network speed** | Dev Tools → **Mocked network speed** → `GPRS` | A banner appears app-wide; Explore visibly crawls. Measured: `GET /v1/breweries/random` went from sub-second to **5016 ms** |
| In-flight requests (new in alpha11) | PseudoCharles → **Traffic**, while throttled | Pulsing rows reading *Waiting for response…*, resolving on body drain |
| **Socket inspection** | **Live** tab → Connect → Emit | Outbound + echoed inbound frames; PseudoCharles → **Sockets** decodes `Engine.IO MESSAGE (4)` / `Socket.IO EVENT (2)` / `taproom:checkin` |
| **Persistent notification** | Dev Tools → **Show traffic notification** | Title `PseudoCharles · N events · M in flight`; while throttled the header reads `Throttled · GPRS (2G)` and gains a **Full speed** action |
| **Web dashboard** | Dev Tools → **Run remote config server** | URL + PIN shown in-app; `/traffic` and `/sockets` serve the same data as the app |
| Traffic / socket capture toggles | Dev Tools → **Capture** | Live counts, clear actions |

### Reaching the dashboard from a laptop

```bash
# Chrome 115+ forces HTTPS on network IPs, so use Safari/Firefox with the shown URL,
# or forward the port and use localhost:
adb forward tcp:8432 tcp:8432
open http://127.0.0.1:8432      # default PIN: 4572
```

---

## About This Demo App

This app searches the [Open Brewery DB](https://www.openbrewerydb.org/) and displays brewery details. On cold start the search screen seeds itself with a handful of random breweries (`GET /breweries/random`) so there's something to look at before the user types. It demonstrates PseudoCharles integration in a modern, multi-module Android architecture.

### How PseudoCharles is wired

**The SDK is confined to `:app`.** No feature module imports `com.zeus.pseudocharles`. Because
PseudoCharles ships as a `debugImplementation` artifact, letting feature modules depend on it would
make them depend on a debug-only dependency; instead the app inverts it through three interfaces
that live in SDK-free modules and are implemented once, in `:app` , the only assembler.

| Interface | Declared in | Implemented by | Purpose |
|---|---|---|---|
| `NetworkInterceptorProvider` | `:core:data` | `PseudoCharlesInterceptorProvider` | HTTP capture + mocking |
| `WebSocketFactoryProvider` | `:core:data` | `PseudoCharlesSocketFactoryProvider` | Socket frame capture |
| `MockProxyController` | `:core:domain` | `PseudoCharlesMockProxyController` | Everything the Dev Tools screen drives |

- **Single `main/` wiring:** no debug/release source-set split. The `-noop` artifacts have identical
  signatures, so the same code compiles and runs in both variants , `isEnabled()` just returns
  `false` and every call becomes a no-op.
- **Gradle variant dependency:** `debugImplementation` pulls the full SDK and server; `releaseImplementation` pulls the two `-noop` twins.
- **Init timing:** `PseudoCharlesDemoApp.onCreate()` calls `initializeMockProxy(this)` before `startKoin { }` so `isEnabled()` is already `true` by the time the Scaffold composes.
- **One shared `OkHttpClient`:** `OkHttpClientFactory` provides a single instance consumed by both the Ktor engine and the socket client. An `OkHttpClient` *is* a `WebSocket.Factory`, so sharing it means one connection pool and one interceptor chain.
- **Debug FAB + tabs:** the bug FAB (Explore only) and the Live / Dev Tools tabs are all gated on `MockProxyController.isAvailable`, so release renders exactly the original single-screen app.

### The live socket feed is simulated

Open Brewery DB has no realtime API. To produce inspectable socket traffic the **Live** tab opens a
WebSocket to the public `wss://echo.websocket.org` and sends Socket.IO-shaped events to itself:

```
OUT 42["taproom:checkin",{"brewery":"Fremont Brewing","pours":3}]
IN  42["taproom:checkin",{"brewery":"Fremont Brewing","pours":3}]   <- echoed back
```

The round trip is the point: the echoed copies arrive as **inbound** frames carrying a decodable
event name, which is what the SDK's decoder, the dashboard's `/sockets` page and the notification's
socket lines all key off. A one-way send would exercise none of them. The UI labels this plainly.

Because it depends on a third party, `LiveFeedStatus.Failed` carries a **typed** reason
(`Unreachable`, `NoNetwork`, `Rejected`, `ClosedByServer`, `Unknown`) and each gets its own copy ,
an echo-server outage must never be mistaken for socket inspection being broken.

### Module Structure

```
:app                                   Application, Koin setup, bottom nav, the 3 SDK implementations
:core:domain                           Result, DataError, MockProxyController + its models (pure Kotlin)
:core:data                             OkHttpClientFactory, HttpClientFactory, WebSocketFactoryProvider, safeCall
:core:presentation                     UiText, error-to-UiText mapping
:core:design-system                    Material3 theme, colors, typography
:feature:brewery:domain                Brewery model, BreweryType, BreweryRepository interface
:feature:brewery:data                  DTOs, OpenBreweryApi (Ktor), KtorBreweryRepository, mappers
:feature:brewery:presentation          MVI (search + detail), Compose screens, nav graph
:feature:devtools:domain               LiveFeedClient, LiveFeedFrame, typed LiveFeedFailure
:feature:devtools:data                 EchoLiveFeedClient (WebSocket through the mock proxy)
:feature:devtools:presentation         MVI (Live feed + Dev Tools), sections, nav graph
```

### Architecture

- **Presentation:** MVI , single `State` data class, sealed `Action` interface, one-time `Event` via `Channel`
- **Navigation:** Type-safe Compose Navigation with `@Serializable` route objects
- **DI:** Koin , one module per feature layer, `viewModelOf` / `singleOf`
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
| PseudoCharles | 1.1.0-alpha11 (core + server, with `-noop` twins) |
| Min SDK | 24 |
| Target/Compile SDK | 36 |

---

## Build & Run

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Build release , also proves the -noop artifacts keep every call site compiling
./gradlew :app:assembleRelease

# Install on connected device/emulator
./gradlew :app:installDebug

# Run unit tests
./gradlew test

# Verify Gradle config integrity (run after any catalog / *.gradle.kts edit)
./gradlew prepareKotlinBuildScriptModel
```

### Verifying the release build really is zero-cost

`assembleRelease` succeeding only proves the API surface matches. To prove nothing ships, compare
what is actually in the dex (the demo does not minify, so the dex is exactly what the artifacts
contributed):

```bash
unzip -o -q app/build/outputs/apk/release/app-release-unsigned.apk -d /tmp/rel
$ANDROID_HOME/build-tools/<ver>/dexdump -f /tmp/rel/classes*.dex \
  | grep "Class descriptor" | sed "s/.*'L//;s/;'.*//" | sort -u > /tmp/rel-classes.txt

grep -c '^com/zeus/pseudocharles/' /tmp/rel-classes.txt   # 22  (debug: 1125)
grep -c '^io/ktor/server/'        /tmp/rel-classes.txt    #  0  (debug:  793)
```

Measured on this app: **24.6 MB debug → 14.4 MB release**. The 22 surviving classes are the no-op
stub plus the public models that keep call sites compiling , no Compose config UI, no Ktor server,
no interceptor, no throttle, no socket capture.

---

## License

This demo app is licensed under the [MIT License](LICENSE).

The PseudoCharles SDK itself is licensed separately under the Apache License 2.0 , see its [Maven Central listing](https://central.sonatype.com/artifact/io.github.aiprototype/pseudocharles) for details.
