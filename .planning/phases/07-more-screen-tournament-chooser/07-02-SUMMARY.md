---
phase: 07-more-screen-tournament-chooser
plan: 02
subsystem: ui
tags: [kmp, compose, koin, multiplatform-settings, tournament, navigation]

# Dependency graph
requires:
  - phase: 07-01-more-screen-tournament-chooser
    provides: "Screen.TournamentChooser nav route placeholder, MoreScreen navigation wiring"
  - phase: 06-season-leaders
    provides: "SeasonLeadersScreen pattern for screen/viewmodel separation"
provides:
  - "TournamentProvider: persistent tournament storage via multiplatform-settings"
  - "TournamentRepository + TournamentRepositoryImpl: domain/data tournament interface"
  - "GetCurrentTournamentUseCase + SetCurrentTournamentUseCase: synchronous use cases"
  - "HttpClientFactory reads dynamic TRNMT header from TournamentProvider"
  - "TournamentChooserScreen: 3 radio-button options with localized names"
  - "TournamentChooserViewModel: emits RestartApp effect on tournament change"
  - "App.kt wired with popUpTo(0)+inclusive=true restart flow"
affects: [Phase 08-premium]

# Tech tracking
tech-stack:
  added: ["com.russhwolf:multiplatform-settings-no-arg:1.3.0"]
  patterns:
    - "Synchronous OutUseCase/InUseCase for non-coroutine reads (Settings API)"
    - "MutableSharedFlow(extraBufferCapacity=1) for one-shot UI effects in ViewModel"
    - "popUpTo(0)+inclusive=true to clear full NavHost back stack before navigating to start"
    - "LaunchedEffect(Unit) collecting SharedFlow effects in Composable"

key-files:
  created:
    - "data/src/commonMain/kotlin/com/mzs/basket_krk/data/tournament/TournamentProvider.kt"
    - "domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/TournamentRepository.kt"
    - "data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/TournamentRepositoryImpl.kt"
    - "domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetCurrentTournamentUseCase.kt"
    - "domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/SetCurrentTournamentUseCase.kt"
    - "presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/tournamentchooser/TournamentChooserViewModel.kt"
    - "presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/tournamentchooser/TournamentChooserScreen.kt"
  modified:
    - "gradle/libs.versions.toml"
    - "data/build.gradle.kts"
    - "data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt"
    - "data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt"
    - "presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt"
    - "presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt"

key-decisions:
  - "TournamentRepository uses non-suspend functions — Settings API (SharedPreferences/NSUserDefaults) is synchronous, no coroutines needed"
  - "HttpClientFactory reads tournament key at HttpClient construction time — client is recreated via Koin on full app restart, so header is always current"
  - "popUpTo(0)+inclusive=true clears full back stack before navigating to Screen.Main — equivalent to Flutter's popUntil(false)+push(RootRoute), forces all ViewModels to recreate"
  - "MutableSharedFlow(extraBufferCapacity=1) used for RestartApp effect — prevents event loss if screen not yet collecting"
  - "Same-tournament tap is a no-op (early return before save and effect emission)"

patterns-established:
  - "Tournament persistence: TournamentProvider wraps Settings, TournamentRepository delegates to provider — zero Koin coupling to storage library"
  - "App restart via navigation: popUpTo(0)+inclusive=true in NavHost block replaces composable on same NavController instance"

requirements-completed: [TRNT-01, TRNT-02, TRNT-03]

# Metrics
duration: 20min
completed: 2026-03-19
---

# Phase 07 Plan 02: Tournament Chooser Summary

**Persistent tournament selector with multiplatform-settings storage, dynamic TRNMT HTTP header via TournamentProvider, and a 3-option radio-button screen that clears the full nav stack and restarts the app on switch**

## Performance

- **Duration:** ~20 min
- **Started:** 2026-03-19T19:25:00Z
- **Completed:** 2026-03-19T19:45:00Z
- **Tasks:** 2
- **Files modified:** 13 (7 created, 6 modified)

## Accomplishments
- Tournament persistence layer built end-to-end: multiplatform-settings-no-arg library, TournamentProvider, TournamentRepository interface (domain), TournamentRepositoryImpl (data)
- HttpClientFactory upgraded from hardcoded "mba" to dynamic `tournamentProvider.getCurrentKey()` read at client construction
- TournamentChooserScreen renders 3 localized radio-button options (Liga MBA Men, Liga MBA Women, Liga KNBA) with current tournament pre-selected
- Switching tournaments saves via SetCurrentTournamentUseCase, emits RestartApp effect, triggers full NavHost back-stack clear and navigation to Screen.Main
- Koin DI wired: TournamentProvider and TournamentRepository in DataModule, both use cases and TournamentChooserViewModel in PresentationModule

## Task Commits

Each task was committed atomically:

1. **Task 1: Tournament data layer** - `eddba88` (feat)
2. **Task 2: TournamentChooserScreen and ViewModel** - `a7d206b` (feat)

**Plan metadata:** (docs commit below)

## Files Created/Modified
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/tournament/TournamentProvider.kt` - Settings wrapper with getCurrentKey/setCurrentKey
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/TournamentRepository.kt` - Synchronous get/set interface
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/TournamentRepositoryImpl.kt` - Delegates to TournamentProvider, converts between String and TournamentType enum
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetCurrentTournamentUseCase.kt` - OutUseCase returning TournamentType
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/SetCurrentTournamentUseCase.kt` - InUseCase saving TournamentType
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt` - Now accepts TournamentProvider, reads dynamic TRNMT header
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt` - TournamentProvider, TournamentRepository, HttpClientFactory(get()) registrations
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt` - GetCurrentTournament, SetCurrentTournament, TournamentChooserViewModel registrations
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/tournamentchooser/TournamentChooserViewModel.kt` - ViewModel with RestartApp SharedFlow effect
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/tournamentchooser/TournamentChooserScreen.kt` - Full screen with ActionBar, radio list, LaunchedEffect effect collector
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` - Placeholder replaced with real TournamentChooserScreen + popUpTo(0) restart
- `gradle/libs.versions.toml` - multiplatformSettings 1.3.0 version + library entry
- `data/build.gradle.kts` - implementation(libs.multiplatform.settings.no.arg)

## Decisions Made
- Synchronous use cases (OutUseCase/InUseCase, not suspend) — Settings API is synchronous on all platforms
- HttpClientFactory reads tournament at construction time, not per-request — full app restart recreates the Koin singleton ensuring fresh header
- popUpTo(0)+inclusive=true is the KMP equivalent of Flutter's `popUntil((route) => false)` + push root

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
- Gradle task name `:presentation:compileKotlinAndroid` was ambiguous (multiple debug/release variants) and `:data:compileKotlinAndroid` doesn't exist in the androidKotlinMultiplatformLibrary project. Used `:presentation:compileDebugKotlinAndroid` and `:data:compileKotlinIosArm64` instead. Build passed successfully.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Phase 07 is complete: MoreScreen, PremiumScreen placeholder, TournamentChooser are all wired
- Phase 08 (Premium) can begin: PremiumScreen placeholder exists at `presentation/screens/premium/PremiumScreen.kt`
- Tournament chooser is fully functional; switching tournament triggers HttpClientFactory reconstruction with new TRNMT header

---
*Phase: 07-more-screen-tournament-chooser*
*Completed: 2026-03-19*
