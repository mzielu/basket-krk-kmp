# Phase 7: More Screen & Tournament Chooser - Research

**Researched:** 2026-03-19
**Domain:** Compose Multiplatform — external URL launching, KMP key-value persistence, dynamic Ktor headers, navigation extension
**Confidence:** HIGH

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- **URL management:** Hardcoded constants for all 6 external URLs. Extract exact URL values from Flutter RemoteConfig defaults. Use `LocalUriHandler.openUri(url)` directly — no safe wrapper. Consistent with existing mailto and match web link patterns.
- **Tournament switching mechanism:** Create a tournament provider/service that stores active tournament and exposes it. Persist selected tournament across app restarts using KMP multiplatform settings/DataStore. `HttpClientFactory` already has `TRNMT` header hardcoded to `'mba'` — make it dynamic by reading from tournament provider. Default tournament: MBA (matches Flutter).
- **App reload after tournament switch:** Match Flutter: pop all routes to root and push MainScreen (full UI rebuild). All ViewModels recreated from scratch with new tournament header active. No reactive/observable pattern needed — clean restart handles it.
- **Tournament Chooser UI:** Full screen with ActionBar + back button. 3 items displayed as radio-button-style list items (RadioListTile equivalent in Compose). Currently active tournament shown via filled radio indicator. Tap switches immediately — no confirmation dialog. After switch: save to persistence, update HTTP client header, pop to root.
- **Premium placeholder:** Show "Buy Premium" item in MoreScreen (all 9 items visible from day one). Navigate to a minimal placeholder screen with title + "Coming soon" message. Add `Screen.Premium` route to navigation graph now — Phase 8 replaces content.

### Claude's Discretion
- Exact KMP settings library choice (multiplatform-settings, DataStore, etc.)
- How to inject tournament provider into HttpClientFactory (Koin wiring)
- Radio list item composable implementation details
- Placeholder Premium screen layout
- Error handling for malformed URLs (if any)

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| MORE-01 | User can see a list of 9 navigation items in the More tab | MoreScreen.kt already has all 9 NavigationItem composables with TODO callbacks; just wire URLs |
| MORE-02 | User can tap "About Us" to open the about page in browser | `LocalUriHandler.openUri("https://www.basketkrk.pl/about_me")` |
| MORE-03 | User can tap "Donate" to open the donation page in browser | `LocalUriHandler.openUri("https://zrzutka.pl/4x4fwg")` |
| MORE-04 | User can tap "Terms of Use" to open terms page in browser | `LocalUriHandler.openUri("https://www.basketkrk.pl/terms_of_use")` |
| MORE-05 | User can tap "Privacy Policy" to open privacy page in browser | `LocalUriHandler.openUri("https://www.basketkrk.pl/privacy_policy")` |
| MORE-06 | User can tap "Write to Us" to open email client pre-filled | Already implemented in MoreScreen.kt — no change needed |
| MORE-07 | User can tap "Check Facebook" to open Facebook page in browser | `LocalUriHandler.openUri("https://www.facebook.com/profile.php?id=100091411303018")` |
| MORE-08 | User can tap "Check Instagram" to open Instagram profile in browser | `LocalUriHandler.openUri("http://instagram.com/_u/basket_krk")` |
| MORE-09 | User can tap "Change Tournament" to navigate to Tournament Chooser | Wire `onOpenTournamentChooser` callback already present in MoreScreen signature |
| MORE-10 | User can tap "Buy Premium" to navigate to Premium screen | Wire `onOpenPayments` callback already present in MoreScreen signature |
| TRNT-01 | User can see a list of available tournaments | TournamentChooserScreen with 3 RadioListTile-equivalent items |
| TRNT-02 | User can select a tournament to switch the active tournament | On tap: save via SetCurrentTournament use case, pop to root, recreate MainScreen |
| TRNT-03 | App updates the API tournament header after switching and reloads data | HttpClientFactory reads TournamentProvider on each `create()` call; full restart ensures fresh clients |
</phase_requirements>

---

## Summary

Phase 7 is a 1:1 Flutter migration with very well-scoped work. The MoreScreen is 80% already built — it has all 9 NavigationItem composables but the 6 URL items have empty TODO lambdas. The email ("Write to Us") item is already functional. The primary new work is: (1) wiring URL constants into the existing TODO lambdas, (2) building TournamentChooserScreen with RadioButton-style list and save-then-restart logic, (3) making HttpClientFactory read the active tournament from a persistent provider instead of the hardcoded `"mba"` string, and (4) adding stub PremiumScreen + new navigation routes.

The tournament switching mechanism is the most architecturally significant piece. Flutter's `SharedPreferences` maps to `multiplatform-settings` (the de-facto KMP equivalent, v1.3.0). The HTTP header update strategy mirrors Flutter exactly: on switch, save to storage, then rebuild the full navigation stack — which causes Koin to reconstruct the `ApiService` (and therefore `HttpClientFactory`) from scratch with the new tournament value.

**Primary recommendation:** Use `multiplatform-settings-no-arg` v1.3.0 in the `data` module for tournament persistence. Inject a `TournamentProvider` singleton into `HttpClientFactory`. The provider reads from Settings on startup and exposes the current key as a plain `String` — no coroutines or Flows needed because the header is only read at client creation time.

---

## Standard Stack

### Core

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| multiplatform-settings-no-arg | 1.3.0 | KMP key-value persistence (SharedPreferences / NSUserDefaults) | De-facto KMP equivalent of SharedPreferences; no-arg variant works from commonMain without platform injection |
| Compose `LocalUriHandler` | (bundled with Compose Multiplatform 1.9.3) | Opening URLs and mailto: links from Compose | Already used in MoreScreen for email; same mechanism for all external links |
| Koin 4.1.1 | (already in project) | DI wiring for TournamentProvider + refactored HttpClientFactory | Already used project-wide |
| `androidx.navigation:navigation-compose` 2.9.1 | (already in project) | Type-safe NavHost for TournamentChooser + Premium routes | Already used project-wide |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Material3 `RadioButton` | (bundled with Compose) | Radio-button selection indicator in TournamentChooserScreen | Only needed for tournament list; no extra dependency |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| multiplatform-settings-no-arg | DataStore KMP (Androidx) | DataStore is suspend/Flow-based — overkill for a single String read; multiplatform-settings is synchronous like the original SharedPreferences |
| multiplatform-settings-no-arg | multiplatform-settings (with-arg variant) | With-arg variant needs platform-specific factory plumbing; no-arg works from commonMain directly |

**Installation (data module `build.gradle.kts`):**
```kotlin
// In commonMain.dependencies:
implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
```

**Version verification:** `1.3.0` confirmed from GitHub releases page (released November 2024, updated to Kotlin 2.1.0).

---

## Architecture Patterns

### Recommended Project Structure for New Files

```
domain/src/commonMain/.../domain/
├── usecase/
│   ├── GetCurrentTournament.kt          # interface + impl
│   └── SetCurrentTournament.kt          # interface + impl

data/src/commonMain/.../data/
├── tournament/
│   └── TournamentProvider.kt            # singleton, holds Settings, exposes key String
└── service/
    └── HttpClientFactory.kt             # modified: takes TournamentProvider param

presentation/src/commonMain/.../presentation/
├── navigation/
│   └── Screen.kt                        # add TournamentChooser + Premium
├── App.kt                               # add composable routes for new screens
├── screens/main/
│   └── MainScreen.kt                    # wire onOpenTournamentChooser + onOpenPayments
├── screens/main/more/
│   ├── MoreScreen.kt                    # wire URL constants, remove TODOs
│   └── MoreConstants.kt                 # new: holds all 6 URL constants
└── screens/tournamentchooser/
    ├── TournamentChooserScreen.kt        # new: RadioButton list + save+restart
    └── TournamentChooserViewModel.kt     # new: reads current tournament, handles save
```

### Pattern 1: TournamentProvider Singleton

**What:** A simple class in the `data` module that wraps `Settings` (multiplatform-settings). It is a Koin `single {}` and is injected into `HttpClientFactory`. On construction it reads the stored tournament key (defaulting to `"mba"` if absent).

**When to use:** Any time the app needs the active tournament key — currently only `HttpClientFactory`.

```kotlin
// data/src/commonMain/.../data/tournament/TournamentProvider.kt
import com.russhwolf.settings.Settings

private const val TOURNAMENT_KEY = "trnmt_key"
private const val DEFAULT_TOURNAMENT = "mba"

class TournamentProvider {
    private val settings = Settings()

    fun getCurrentKey(): String =
        settings.getStringOrNull(TOURNAMENT_KEY) ?: DEFAULT_TOURNAMENT

    fun setCurrentKey(key: String) {
        settings.putString(TOURNAMENT_KEY, key)
    }
}
```

### Pattern 2: HttpClientFactory Modification

**What:** Accept `TournamentProvider` as a constructor parameter instead of hardcoding `"mba"`.

**When to use:** Called once per Koin `single` lifecycle. Since full nav restart recreates the graph, `create()` is called again after tournament switch, picking up the newly persisted key.

```kotlin
// Modified HttpClientFactory.kt
class HttpClientFactory(private val tournamentProvider: TournamentProvider) {
    fun create(): HttpClient = HttpClient(platformEngine()) {
        // ...
        defaultRequest {
            contentType(ContentType.Application.Json)
            header("TRNMT", tournamentProvider.getCurrentKey())
            header("OS", "android")
            header("APP-VERSION", "100")
        }
        // ...
    }
}
```

**Koin wiring in DataModule:**
```kotlin
val dataModule = module {
    single { TournamentProvider() }
    single { ApiService(client = HttpClientFactory(get()).create()) }
    // ... rest unchanged
}
```

### Pattern 3: Use Cases (standard project style)

```kotlin
// domain/src/commonMain/.../domain/usecase/GetCurrentTournamentUseCase.kt
interface GetCurrentTournament : OutUseCase<TournamentType>

class GetCurrentTournamentUseCase(
    private val tournamentProvider: TournamentProvider  // or via repository abstraction
) : GetCurrentTournament {
    override fun invoke(): TournamentType =
        TournamentType.fromKey(tournamentProvider.getCurrentKey())
}
```

**Note on layer separation:** `TournamentProvider` lives in `data`. For clean architecture, the domain use cases should depend on an abstraction. Given this project uses repository interfaces in domain (`SeasonRepository`, etc.), the cleanest approach is:
- Add `TournamentRepository` interface to domain with `getCurrentTournament(): TournamentType` and `setCurrentTournament(type: TournamentType)`
- Implement in data as `TournamentRepositoryImpl` wrapping `TournamentProvider`
- Use cases in domain depend on `TournamentRepository`

This matches every existing data/domain pattern in the project.

### Pattern 4: TournamentChooserScreen + Save-And-Restart

**What:** Screen reads current tournament from `GetCurrentTournament` use case via ViewModel, renders 3 RadioButton rows. On tap, calls `SetCurrentTournament` then triggers restart callback.

```kotlin
// Restart logic in App.kt composable entry
composable<Screen.TournamentChooser> {
    TournamentChooserScreen(
        viewModel = koinViewModel<TournamentChooserViewModel>(),
        onSwitchAndRestart = {
            navController.navigate(Screen.Main) {
                popUpTo(0) { inclusive = true }
            }
        },
        onNavigateBack = { navController.popBackStack() }
    )
}
```

The `popUpTo(0) { inclusive = true }` clears the entire back stack and navigates to `Screen.Main`, matching Flutter's `popUntil((route) => false)` + `push(RootRoute())`.

### Pattern 5: RadioButton Row (Compose equivalent of Flutter RadioListTile)

```kotlin
@Composable
fun TournamentRadioItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}
```

### Pattern 6: MoreScreen URL Constants

```kotlin
// presentation/src/commonMain/.../screens/main/more/MoreConstants.kt
object MoreUrls {
    const val ABOUT_US = "https://www.basketkrk.pl/about_me"
    const val DONATE   = "https://zrzutka.pl/4x4fwg"
    const val TERMS    = "https://www.basketkrk.pl/terms_of_use"
    const val PRIVACY  = "https://www.basketkrk.pl/privacy_policy"
    const val FACEBOOK = "https://www.facebook.com/profile.php?id=100091411303018"
    const val INSTAGRAM = "http://instagram.com/_u/basket_krk"
}
```

These are extracted verbatim from Flutter's `RemoteConfigService` `DEFAULT_*` constants — the guaranteed fallback values when Firebase Remote Config is unavailable.

### Anti-Patterns to Avoid

- **Reactive TournamentProvider:** Do not make `getCurrentKey()` a `StateFlow` or `Flow`. The value is only needed at HTTP client construction time, and the full restart pattern makes reactivity unnecessary and adds complexity.
- **Passing TournamentProvider to presentation layer:** Tournament selection should go through use cases in presentation, not depend on `TournamentProvider` directly. Keep domain boundary clean.
- **Rebuilding only part of the back stack:** Must clear the ENTIRE stack (`popUpTo(0) { inclusive = true }`) before navigating to Main. Partial clearing leaves stale ViewModels with old tournament context.
- **Creating HttpClient inside TournamentProvider:** Keep concerns separate — `TournamentProvider` only reads/writes the key string.

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Cross-platform key-value storage | Custom expect/actual SharedPreferences wrapper | `multiplatform-settings-no-arg` 1.3.0 | Library handles Android SharedPreferences, iOS NSUserDefaults, JS localStorage, and web; handles thread safety and null defaults |
| Opening URLs on both platforms | Custom `expect fun openUrl()` | `LocalUriHandler.current.openUri()` (Compose) | Already implemented and working in MoreScreen for mailto; handles mailto: and https: uniformly |

**Key insight:** This phase needs zero new platform-specific `expect/actual` code. `multiplatform-settings-no-arg` provides its own `expect/actual` internally. `LocalUriHandler` is already wired by Compose on both platforms.

---

## Common Pitfalls

### Pitfall 1: HttpClientFactory Constructor Change Breaks Koin Single
**What goes wrong:** `HttpClientFactory()` in `dataModule` is called as `HttpClientFactory().create()` — changing to require `TournamentProvider` param breaks existing registration without updating the module.
**Why it happens:** `single { ApiService(client = HttpClientFactory().create()) }` is a lambda — the IDE won't flag missing params at compile time if `TournamentProvider` is optional.
**How to avoid:** Update `dataModule` in the same commit that changes `HttpClientFactory`. Use `single { ApiService(client = HttpClientFactory(get()).create()) }`.
**Warning signs:** `NoBeanDefFoundException` for `TournamentProvider` at runtime.

### Pitfall 2: NavController Reference in TournamentChooserViewModel
**What goes wrong:** Trying to call `navController.navigate()` from inside the ViewModel to trigger restart.
**Why it happens:** NavController is a UI concern — it should not be injected into ViewModel.
**How to avoid:** ViewModel exposes an effect/event (e.g., `SharedFlow<TournamentChooserEffect>`) or a simpler `var restartRequested: Boolean` state. The composable collects it and calls `onSwitchAndRestart()` callback. Alternatively, the composable calls the use case itself and the ViewModel only exposes current state.
**Warning signs:** Compilation error trying to inject `NavController` into Koin or `rememberNavController()` outside Compose scope.

### Pitfall 3: multiplatform-settings-no-arg in Wrong Module
**What goes wrong:** Adding `multiplatform-settings-no-arg` to the `presentation` module instead of `data`.
**Why it happens:** `TournamentProvider` is conceptually storage — it belongs in `data`. Presentation only sees use case interfaces.
**How to avoid:** Add the dependency only to `data/build.gradle.kts`. `TournamentRepository` interface (domain) takes no dependency on the library.
**Warning signs:** `Settings` class imported in presentation-layer files.

### Pitfall 4: Incomplete Back Stack Clear After Tournament Switch
**What goes wrong:** Using `navController.popBackStack(Screen.Main, false)` instead of `popUpTo(0) { inclusive = true }`. If `Screen.Main` is not on the back stack (e.g., user navigated to TournamentChooser from deep inside stack), this is a no-op.
**Why it happens:** `popBackStack(route, ...)` requires the route to exist in the stack — it does nothing if not found.
**How to avoid:** Always use `popUpTo(0) { inclusive = true }` which clears everything regardless of stack state, matching Flutter's `popUntil((route) => false)`.
**Warning signs:** After tournament switch, back button still navigates to TournamentChooser or other old screens.

### Pitfall 5: Settings() Initialization in commonMain (no-arg variant)
**What goes wrong:** `Settings()` with no-arg constructor requires the `multiplatform-settings-no-arg` artifact specifically. Using `multiplatform-settings` (base artifact) requires passing platform-specific factories.
**Why it happens:** Confusion between the two artifact IDs.
**How to avoid:** Use `com.russhwolf:multiplatform-settings-no-arg:1.3.0` — the `Settings()` no-arg constructor works from commonMain.
**Warning signs:** Compile error "None of the following candidates is applicable..." for `Settings()` constructor.

---

## Code Examples

Verified patterns from existing project source:

### Existing: LocalUriHandler pattern (already in MoreScreen.kt)
```kotlin
// Source: presentation/.../screens/main/more/MoreScreen.kt (lines 141-145)
val uriHandler = LocalUriHandler.current
// ...
onClick = {
    val mailto = "mailto:kontakt@basketkrk.pl?subject=[${viewState.platform}] Mail from the app"
    uriHandler.openUri(mailto)
}
```
The same `uriHandler.openUri(url)` call works for any HTTPS URL.

### Existing: Screen sealed class extension
```kotlin
// Source: presentation/.../navigation/Screen.kt
@Serializable
sealed class Screen {
    // ... existing entries ...
    @Serializable
    data object TournamentChooser : Screen()

    @Serializable
    data object Premium : Screen()
}
```

### Existing: NavHost composable entry pattern
```kotlin
// Source: presentation/.../App.kt (lines 114-138 for reference)
composable<Screen.TournamentChooser> {
    TournamentChooserScreen(
        viewModel = koinViewModel<TournamentChooserViewModel>(),
        onSwitchAndRestart = {
            navController.navigate(Screen.Main) {
                popUpTo(0) { inclusive = true }
            }
        },
        onNavigateBack = { navController.popBackStack() },
    )
}

composable<Screen.Premium> {
    PremiumScreen(
        onNavigateBack = { navController.popBackStack() },
    )
}
```

### Existing: MainScreen callback wiring (must change)
```kotlin
// Source: presentation/.../screens/main/MainScreen.kt (line 66-70) — CURRENT (broken)
MainTab.MORE -> MoreScreen(
    onOpenPayments = {},
    onOpenTournamentChooser = {}
)
// Change to:
MainTab.MORE -> MoreScreen(
    onOpenPayments = onOpenPremium,
    onOpenTournamentChooser = onOpenTournamentChooser,
)
```

`MainScreen` signature must accept `onOpenTournamentChooser: () -> Unit` and `onOpenPremium: () -> Unit` and pass them through from `App.kt`.

### Existing: Koin viewModelOf pattern
```kotlin
// Source: presentation/.../di/PresentationModule.kt
viewModelOf(::TournamentChooserViewModel)
```

---

## Exact URL Constants (from Flutter source)

Extracted from `RemoteConfigService.dart` DEFAULT constants — these are the canonical values:

| Item | URL |
|------|-----|
| About Us | `https://www.basketkrk.pl/about_me` |
| Donate | `https://zrzutka.pl/4x4fwg` |
| Terms of Use | `https://www.basketkrk.pl/terms_of_use` |
| Privacy Policy | `https://www.basketkrk.pl/privacy_policy` |
| Facebook | `https://www.facebook.com/profile.php?id=100091411303018` |
| Instagram | `http://instagram.com/_u/basket_krk` |
| Email recipient | `kontakt@basketkrk.pl` |

Note: Instagram URL uses `http://` (not `https://`) — this is a redirect URL (`_u/` is the Instagram username redirect scheme). Keep as-is from Flutter source.

---

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| `SharedPreferences` (Android-only) | `multiplatform-settings-no-arg` | KMP era | Works identically on Android + iOS from commonMain |
| Manual expect/actual for platform storage | no-arg Settings() constructor in commonMain | multiplatform-settings v0.8+ | No platform-specific factory code needed |

---

## Open Questions

1. **Where exactly does `TournamentRepository` live vs. just using `TournamentProvider` from domain use cases?**
   - What we know: All other repositories follow interface-in-domain, impl-in-data pattern
   - What's unclear: The TournamentProvider could be simpler — a domain-layer interface might be overkill for 2 use cases
   - Recommendation: Follow the established pattern (domain repository interface + data impl) for consistency. The added boilerplate is ~20 lines and keeps the architecture coherent.

2. **Does the iOS app need `multiplatform-settings-no-arg` to work out of the box?**
   - What we know: The no-arg variant uses `NSUserDefaults.standardUserDefaults` on iOS automatically
   - What's unclear: iOS target setup — the project has `iosArm64` and `iosSimulatorArm64` targets
   - Recommendation: The library handles these targets natively; no additional iOS-specific code needed.

---

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | kotlin.test (already in commonTest dependencies) |
| Config file | none — no test runner config found in project |
| Quick run command | `./gradlew :domain:allTests` |
| Full suite command | `./gradlew allTests` |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| TRNT-02 | SetCurrentTournament saves correct key to storage | unit | `./gradlew :domain:allTests` | Wave 0 |
| TRNT-03 | GetCurrentTournament returns correct TournamentType from stored key | unit | `./gradlew :domain:allTests` | Wave 0 |
| TRNT-03 | TournamentType.fromKey returns MBA for unknown/null key | unit | `./gradlew :domain:allTests` | Wave 0 |
| MORE-01 through MORE-10 | UI rendering and click behavior | manual-only | N/A | manual — Compose UI test infra not set up |

**Manual-only justification (UI tests):** No Compose UI test infrastructure exists in the project (no `compose-ui-test` dependency, no instrumented test setup). Adding full UI test infra is out of scope for this phase.

### Sampling Rate
- **Per task commit:** `./gradlew :domain:allTests`
- **Per wave merge:** `./gradlew allTests`
- **Phase gate:** Full suite green before `/gsd:verify-work`

### Wave 0 Gaps
- [ ] `domain/src/commonTest/.../usecase/TournamentUseCaseTest.kt` — covers TRNT-02, TRNT-03
- [ ] `domain/src/commonTest/.../model/TournamentTypeTest.kt` — covers fromKey fallback behavior

*(Test framework `kotlin.test` already declared in `commonTest.dependencies` across all modules — no install needed)*

---

## Sources

### Primary (HIGH confidence)
- Direct file read: `presentation/.../screens/main/more/MoreScreen.kt` — existing composables, callback signatures, LocalUriHandler usage
- Direct file read: `presentation/.../navigation/Screen.kt` — existing route structure
- Direct file read: `data/.../service/HttpClientFactory.kt` — hardcoded TRNMT header location
- Direct file read: `domain/.../model/TournamentType.kt` — enum structure, fromKey companion
- Direct file read: `domain/.../base/UseCase.kt` — InUseCase/OutUseCase/InOutUseCase interfaces
- Direct file read: `data/di/DataModule.kt` + `presentation/di/PresentationModule.kt` — Koin registration patterns
- Direct file read: Flutter `remote_config_service.dart` — exact DEFAULT URL constants
- Direct file read: Flutter `trnmnt_chooser_screen.dart` — popUntil + push restart pattern
- Direct file read: `presentation/.../App.kt` — NavHost composable pattern
- Direct file read: `gradle/libs.versions.toml` — confirmed Koin 4.1.1, navigation-compose 2.9.1, no multiplatform-settings present
- Direct file read: `presentation/build.gradle.kts` — confirmed no multiplatform-settings dependency
- GitHub releases (WebFetch): multiplatform-settings v1.3.0 is latest stable (November 2024)

### Secondary (MEDIUM confidence)
- WebSearch verified with GitHub releases: multiplatform-settings-no-arg 1.3.0 is current stable

### Tertiary (LOW confidence)
- None

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — all libraries verified against project files and official releases
- Architecture: HIGH — patterns derived directly from existing project source code
- Pitfalls: HIGH — identified from direct code inspection and KMP navigation docs
- URL constants: HIGH — extracted verbatim from Flutter source DEFAULT constants

**Research date:** 2026-03-19
**Valid until:** 2026-04-19 (stable libraries, slow-moving domain)
