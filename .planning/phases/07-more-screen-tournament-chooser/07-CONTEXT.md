# Phase 7: More Screen & Tournament Chooser - Context

**Gathered:** 2026-03-19
**Status:** Ready for planning

<domain>
## Phase Boundary

Complete the MoreScreen by wiring all 9 items (6 external URLs, email, tournament chooser navigation, premium navigation) and build the Tournament Chooser screen with tournament switching that updates the API header and reloads the app. This is a 1:1 Flutter migration.

This phase does NOT implement the Premium screen content (Phase 8) or add analytics/logging.

</domain>

<decisions>
## Implementation Decisions

### URL management
- Hardcoded constants for all 6 external URLs (About Us, Donate, Terms, Privacy, Facebook, Instagram)
- Extract exact URL values from Flutter RemoteConfig defaults / source code
- Use `LocalUriHandler.openUri(url)` directly for all items — no safe wrapper
- Consistent with existing mailto and match web link patterns

### Tournament switching mechanism
- Create a tournament provider/service that stores active tournament and exposes it
- Persist selected tournament across app restarts using KMP multiplatform settings/DataStore
- `HttpClientFactory` already has `TRNMT` header hardcoded to `'mba'` — make it dynamic by reading from tournament provider
- Default tournament: MBA (matches Flutter)

### App reload after tournament switch
- Match Flutter: pop all routes to root and push MainScreen (full UI rebuild)
- All ViewModels recreated from scratch with new tournament header active
- No reactive/observable pattern needed — clean restart handles it

### Tournament Chooser UI
- Full screen with ActionBar + back button (match Flutter, consistent with other detail screens)
- 3 items displayed as radio-button-style list items (RadioListTile equivalent in Compose)
- Currently active tournament shown via filled radio indicator
- Tap switches immediately — no confirmation dialog
- After switch: save to persistence, update HTTP client header, pop to root

### Premium placeholder
- Show "Buy Premium" item in MoreScreen (all 9 items visible from day one)
- Navigate to a minimal placeholder screen with title + "Coming soon" message
- Add `Screen.Premium` route to navigation graph now — Phase 8 replaces content

### Claude's Discretion
- Exact KMP settings library choice (multiplatform-settings, DataStore, etc.)
- How to inject tournament provider into HttpClientFactory (Koin wiring)
- Radio list item composable implementation details
- Placeholder Premium screen layout
- Error handling for malformed URLs (if any)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (migration reference)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/more/more_screen.dart` — MoreScreen layout, item list, URL launching
- `~/Documents/Development/flutter/basket_krk/lib/presentation/trnmnt_chooser/trnmnt_chooser_screen.dart` — Tournament Chooser UI, RadioListTile items, save-and-restart flow
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/set_current_tournament_usecase.dart` — Save tournament + update interceptors
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/get_current_tournament_usecase.dart` — Read current tournament from storage
- `~/Documents/Development/flutter/basket_krk/lib/core/request.dart` — Dio `TRNMT` header injection (`createBaseOptions`)
- `~/Documents/Development/flutter/basket_krk/lib/services/local_storage_service.dart` — SharedPreferences storage with `'trnmt_key'`
- `~/Documents/Development/flutter/basket_krk/lib/domain/model/other/tournament_type.dart` — TournamentType enum with string keys

### Existing KMP components (reuse/modify these)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/more/MoreScreen.kt` — Existing MoreScreen with 9 NavigationItem composables and TODO placeholders
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/more/MoreViewModel.kt` — Existing ViewModel with GetPlatform
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/NavigationItem.kt` — Reusable list item component
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` — Navigation routes (add TournamentChooser + Premium)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` — NavHost (add new routes)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/MainScreen.kt` — Tab container (wire navigation callbacks)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TournamentType.kt` — Existing enum with MBA, WMBA, KNBA and string keys

### HTTP client (modify for dynamic header)
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt` — Currently hardcodes `TRNMT: 'mba'` header — needs dynamic value from tournament provider

### String resources
- `presentation/src/commonMain/composeResources/values/strings.xml` — All More/Tournament strings already defined (`more_*`, `trnmnt_*`, `choose_trnmnt`)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `NavigationItem` composable — already used by MoreScreen for all 9 items
- `LocalUriHandler` — already used for mailto, match web links
- `TournamentType` enum — MBA, WMBA, KNBA with `.key` string property
- `ActionBar` — standard top bar with back button
- All string resources pre-defined in strings.xml

### Established Patterns
- MVVM + StateFlow for ViewModels
- Koin DI: `viewModelOf()` for ViewModels, `single<Interface> { Impl }` for services
- `Screen` sealed class with `@Serializable` for navigation routes
- `NavController.navigate()` for screen transitions
- `LocalUriHandler.openUri()` for external URLs

### Integration Points
- `Screen.kt` — add `TournamentChooser` and `Premium` routes
- `App.kt` NavHost — add composable entries for new screens
- `MainScreen.kt` — wire `onOpenTournamentChooser` and `onOpenPayments` callbacks to NavController
- `HttpClientFactory` — inject tournament provider for dynamic TRNMT header
- `DataModule` / `PresentationModule` — register tournament service, use cases, ViewModels

</code_context>

<specifics>
## Specific Ideas

- 1:1 Flutter migration — match same API contract (TRNMT header), same UI (RadioListTile for tournaments), same behavior (pop-to-root on switch)
- MoreScreen UI already 80% built — mainly needs URL constants wired into existing TODO callbacks
- Email "Write to Us" already works — keep as-is
- Tournament switching is the most complex part: persistence + HTTP header + app reload
- 3 tournaments: Liga MBA (Men), Liga MBA (Women), Liga KNBA — string resources exist

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 07-more-screen-tournament-chooser*
*Context gathered: 2026-03-19*
