---
phase: 07-more-screen-tournament-chooser
verified: 2026-03-19T20:30:00Z
status: passed
score: 15/15 must-haves verified
re_verification: false
gaps: []
human_verification:
  - test: "Open More tab and tap About Us"
    expected: "Browser opens https://www.basketkrk.pl/about_me"
    why_human: "LocalUriHandler.openUri invocation and actual browser launch cannot be verified programmatically"
  - test: "Tap Change Tournament, select a different tournament, observe app restart"
    expected: "App clears nav stack, returns to Main tab, new tournament key is sent as TRNMT header on all subsequent API calls"
    why_human: "Full navigation stack clear + ViewModel recreation + HTTP header value requires runtime observation"
  - test: "Kill and relaunch the app after changing tournament"
    expected: "Previously selected tournament is still active (Settings persists across restarts)"
    why_human: "Multiplatform-settings persistence requires device-level verification"
---

# Phase 7: More Screen & Tournament Chooser Verification Report

**Phase Goal:** Users can access all secondary app functions from the More tab including tournament switching
**Verified:** 2026-03-19T20:30:00Z
**Status:** passed
**Re-verification:** No — initial verification

## Goal Achievement

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | User sees all 9 items in the More tab | ✓ VERIFIED | 9 `NavigationItem(` calls in MoreScreen.kt (grep count = 9) |
| 2 | User taps About Us and browser opens https://www.basketkrk.pl/about_me | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.ABOUT_US)` at line 91; `ABOUT_US = "https://www.basketkrk.pl/about_me"` in MoreConstants.kt |
| 3 | User taps Donate and browser opens https://zrzutka.pl/4x4fwg | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.DONATE)` at line 101; `DONATE = "https://zrzutka.pl/4x4fwg"` in MoreConstants.kt |
| 4 | User taps Terms and browser opens https://www.basketkrk.pl/terms_of_use | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.TERMS)` at line 121; `TERMS = "https://www.basketkrk.pl/terms_of_use"` |
| 5 | User taps Privacy and browser opens https://www.basketkrk.pl/privacy_policy | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.PRIVACY)` at line 131; `PRIVACY = "https://www.basketkrk.pl/privacy_policy"` |
| 6 | User taps Facebook and browser opens https://www.facebook.com/profile.php?id=100091411303018 | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.FACEBOOK)` at line 154; `FACEBOOK = "https://www.facebook.com/profile.php?id=100091411303018"` |
| 7 | User taps Instagram and browser opens http://instagram.com/_u/basket_krk | ✓ VERIFIED | `uriHandler.openUri(MoreUrls.INSTAGRAM)` at line 164; `INSTAGRAM = "http://instagram.com/_u/basket_krk"` |
| 8 | User taps Write to Us and email client opens with mailto:kontakt@basketkrk.pl | ✓ VERIFIED | `uriHandler.openUri("mailto:kontakt@basketkrk.pl?subject=...")` at lines 141-144; no TODO remains |
| 9 | User taps Change Tournament and navigates to TournamentChooser screen | ✓ VERIFIED | `onOpenTournamentChooser()` in MoreScreen line 81; wired through MainScreen to `navController.navigate(Screen.TournamentChooser)` in App.kt line 75 |
| 10 | User taps Buy Premium and navigates to Premium placeholder screen | ✓ VERIFIED | `onOpenPayments()` in MoreScreen line 111; wired through MainScreen to `navController.navigate(Screen.Premium)` in App.kt line 78 |
| 11 | User navigates to Tournament Chooser and sees 3 tournament options | ✓ VERIFIED | `TournamentChooserScreen` iterates `viewState.tournaments` (= `TournamentType.entries` = MBA/WMBA/KNBA) with `TournamentRadioItem` for each |
| 12 | Currently active tournament shown with filled radio button | ✓ VERIFIED | `RadioButton(selected = tournament == viewState.currentTournament, ...)` at line 105 in TournamentChooserScreen.kt |
| 13 | User taps different tournament, app saves and restarts at Main | ✓ VERIFIED | `setCurrentTournament(tournament)` then `_effect.tryEmit(RestartApp)` in ViewModel; `onSwitchAndRestart` triggers `popUpTo(0) { inclusive = true }` + navigate to `Screen.Main` in App.kt |
| 14 | After restart, API TRNMT header uses the newly selected tournament key | ✓ VERIFIED | `HttpClientFactory` reads `tournamentProvider.getCurrentKey()` (dynamic, not hardcoded); client is a Koin `single` rebuilt on full restart |
| 15 | Selected tournament persists across app restarts | ✓ VERIFIED | `TournamentProvider` uses `com.russhwolf.settings.Settings()` (multiplatform-settings-no-arg 1.3.0); `TOURNAMENT_KEY = "trnmt_key"` with `DEFAULT_TOURNAMENT = "mba"` fallback |

**Score:** 15/15 truths verified

---

### Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `presentation/.../more/MoreConstants.kt` | `object MoreUrls` with 6 URL constants | ✓ VERIFIED | Exists, 11 lines, all 6 constants present with correct URLs |
| `presentation/.../more/MoreScreen.kt` | All 9 items wired, 0 TODOs | ✓ VERIFIED | 178 lines, 0 TODO comments, 6 `MoreUrls.` references, 7 `uriHandler.openUri` calls |
| `presentation/.../navigation/Screen.kt` | `TournamentChooser` and `Premium` routes | ✓ VERIFIED | Both `data object TournamentChooser : Screen()` and `data object Premium : Screen()` present |
| `presentation/.../screens/premium/PremiumScreen.kt` | Placeholder with ActionBar and Coming soon | ✓ VERIFIED | Exists, 45 lines, `Scaffold + ActionBar + Text("Coming soon")`, `stringResource(Res.string.premium_account)` |
| `presentation/.../App.kt` | NavHost entries for both routes + callbacks in Screen.Main | ✓ VERIFIED | `composable<Screen.TournamentChooser>` wires real `TournamentChooserScreen`; `composable<Screen.Premium>` wires `PremiumScreen`; `openTournamentChooser` and `openPremium` in Screen.Main block |
| `presentation/.../screens/main/MainScreen.kt` | `openTournamentChooser` and `openPremium` callbacks, real lambdas to MoreScreen | ✓ VERIFIED | Both params in signature (lines 46-47); `onOpenPayments = openPremium` and `onOpenTournamentChooser = openTournamentChooser` at lines 69-70 — no empty lambdas |
| `data/.../tournament/TournamentProvider.kt` | Persistent storage via multiplatform-settings | ✓ VERIFIED | `class TournamentProvider` with `Settings()`, `getCurrentKey()`, `setCurrentKey()` |
| `domain/.../repository/TournamentRepository.kt` | Synchronous get/set interface | ✓ VERIFIED | Interface with non-suspend `getCurrentTournament()` and `setCurrentTournament()` |
| `data/.../repository/TournamentRepositoryImpl.kt` | Implementation delegating to TournamentProvider | ✓ VERIFIED | Delegates to `tournamentProvider.getCurrentKey()`/`setCurrentKey(type.key)` |
| `domain/.../usecase/GetCurrentTournamentUseCase.kt` | `OutUseCase<TournamentType>` | ✓ VERIFIED | `interface GetCurrentTournament : OutUseCase<TournamentType>` + implementation |
| `domain/.../usecase/SetCurrentTournamentUseCase.kt` | `InUseCase<TournamentType>` | ✓ VERIFIED | `interface SetCurrentTournament : InUseCase<TournamentType>` + implementation |
| `data/.../service/HttpClientFactory.kt` | Dynamic TRNMT header from TournamentProvider | ✓ VERIFIED | `class HttpClientFactory(private val tournamentProvider: TournamentProvider)`, `header("TRNMT", tournamentProvider.getCurrentKey())`, no hardcoded `"mba"` |
| `presentation/.../screens/tournamentchooser/TournamentChooserScreen.kt` | Full-screen with radio buttons | ✓ VERIFIED | `LaunchedEffect` effect collector, `RadioButton(selected = ...)` per tournament, `ActionBar` with back button, 3 localized string resources |
| `presentation/.../screens/tournamentchooser/TournamentChooserViewModel.kt` | Manages selection and RestartApp effect | ✓ VERIFIED | `MutableSharedFlow<TournamentChooserEffect>`, `onTournamentSelected` with no-op guard, `setCurrentTournament` before emit |

---

### Key Link Verification

| From | To | Via | Status | Details |
|------|----|-----|--------|---------|
| `MoreScreen.kt` | `MoreConstants.kt` | `MoreUrls.` references | ✓ WIRED | 6 `MoreUrls.` references confirmed (grep count = 6) |
| `MoreScreen.kt` | `LocalUriHandler` | `uriHandler.openUri(url)` | ✓ WIRED | 7 `uriHandler.openUri` calls confirmed (6 URLs + 1 mailto) |
| `MainScreen.kt` | `App.kt` | `openTournamentChooser` and `openPremium` callbacks | ✓ WIRED | Both params in MainScreen signature, both passed from App.kt NavHost Screen.Main block |
| `App.kt` | `Screen.TournamentChooser` / `Screen.Premium` | `navController.navigate` in composable entries | ✓ WIRED | Both `composable<Screen.TournamentChooser>` and `composable<Screen.Premium>` entries in NavHost |
| `HttpClientFactory.kt` | `TournamentProvider.kt` | Constructor injection, `getCurrentKey()` for TRNMT header | ✓ WIRED | `HttpClientFactory(private val tournamentProvider: TournamentProvider)` + `header("TRNMT", tournamentProvider.getCurrentKey())` |
| `TournamentRepositoryImpl.kt` | `TournamentProvider.kt` | Delegates get/set to provider | ✓ WIRED | `tournamentProvider.getCurrentKey()` and `tournamentProvider.setCurrentKey(type.key)` |
| `TournamentChooserViewModel.kt` | `GetCurrentTournament` / `SetCurrentTournament` | Use case injection | ✓ WIRED | Constructor params used in `_viewState` init and `onTournamentSelected` |
| `App.kt composable<Screen.TournamentChooser>` | `TournamentChooserScreen` | NavHost entry with `onSwitchAndRestart` callback | ✓ WIRED | `TournamentChooserScreen(viewModel = koinViewModel<TournamentChooserViewModel>(), onSwitchAndRestart = { navController.navigate(Screen.Main) { popUpTo(0) { inclusive = true } } })` |
| `DataModule.kt` | `TournamentProvider` + `HttpClientFactory` | Koin single registrations | ✓ WIRED | `single { TournamentProvider() }`, `single<TournamentRepository> { TournamentRepositoryImpl(get()) }`, `HttpClientFactory(get()).create()` |

---

### Requirements Coverage

| Requirement | Source Plan | Description | Status | Evidence |
|-------------|-------------|-------------|--------|----------|
| MORE-01 | 07-01 | User can see a list of 9 navigation items in the More tab | ✓ SATISFIED | 9 `NavigationItem(` in MoreScreen.kt |
| MORE-02 | 07-01 | User can tap "About Us" to open the about page in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.ABOUT_US)` with correct URL |
| MORE-03 | 07-01 | User can tap "Donate" to open the donation page in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.DONATE)` with correct URL |
| MORE-04 | 07-01 | User can tap "Terms of Use" to open terms page in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.TERMS)` with correct URL |
| MORE-05 | 07-01 | User can tap "Privacy Policy" to open privacy page in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.PRIVACY)` with correct URL |
| MORE-06 | 07-01 | User can tap "Write to Us" to open email client with pre-filled recipient and subject | ✓ SATISFIED | `uriHandler.openUri("mailto:kontakt@basketkrk.pl?subject=...")` |
| MORE-07 | 07-01 | User can tap "Check Facebook" to open Facebook page in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.FACEBOOK)` with correct URL |
| MORE-08 | 07-01 | User can tap "Check Instagram" to open Instagram profile in browser | ✓ SATISFIED | `uriHandler.openUri(MoreUrls.INSTAGRAM)` with correct URL |
| MORE-09 | 07-01 | User can tap "Change Tournament" to navigate to Tournament Chooser screen | ✓ SATISFIED | `onOpenTournamentChooser()` → `navController.navigate(Screen.TournamentChooser)` |
| MORE-10 | 07-01 | User can tap "Buy Premium" to navigate to Premium screen | ✓ SATISFIED | `onOpenPayments()` → `navController.navigate(Screen.Premium)` |
| TRNT-01 | 07-02 | User can see a list of available tournaments | ✓ SATISFIED | `TournamentChooserScreen` renders `TournamentType.entries` (MBA, WMBA, KNBA) as `TournamentRadioItem` list |
| TRNT-02 | 07-02 | User can select a tournament to switch the active tournament | ✓ SATISFIED | `onTournamentSelected` → `setCurrentTournament(tournament)` → `TournamentProvider.setCurrentKey()` → Settings.putString |
| TRNT-03 | 07-02 | App updates the API tournament header after switching and reloads data | ✓ SATISFIED | `popUpTo(0) { inclusive = true }` forces full ViewModel recreation; Koin rebuilds `HttpClientFactory(get())` reading new key from `TournamentProvider` |

All 13 phase-7 requirement IDs (MORE-01 through MORE-10, TRNT-01 through TRNT-03) accounted for. No orphaned requirements for Phase 7 in REQUIREMENTS.md.

---

### Anti-Patterns Found

| File | Line | Pattern | Severity | Impact |
|------|------|---------|----------|--------|
| None | — | — | — | — |

Zero TODO/FIXME/HACK/PLACEHOLDER comments in any modified file. No empty lambdas passed to MoreScreen. Hardcoded `"mba"` header removed from HttpClientFactory. Plan 02 placeholder text removed from App.kt. All implementation is substantive.

---

### Human Verification Required

#### 1. More Tab External Links

**Test:** Open the app on device, navigate to the More tab, tap each of: About Us, Donate, Terms of Use, Privacy Policy, Check Facebook, Check Instagram.
**Expected:** Each tap opens the correct URL in the device browser. About Us → basketkrk.pl/about_me, Donate → zrzutka.pl/4x4fwg, Terms → basketkrk.pl/terms_of_use, Privacy → basketkrk.pl/privacy_policy, Facebook → facebook.com profile, Instagram → instagram.com/_u/basket_krk.
**Why human:** `LocalUriHandler.openUri` side effect cannot be observed programmatically.

#### 2. Tournament Switch and App Restart

**Test:** Navigate to More → Change Tournament. Note currently selected tournament. Tap a different tournament.
**Expected:** App immediately navigates back to the Main (Matches) tab with no back stack. Verify the action bar or behavior reflects the new tournament context. Navigate back to More → Change Tournament and confirm the new tournament is now the pre-selected radio button.
**Why human:** `popUpTo(0) { inclusive = true }` + ViewModel recreation and TRNMT header behavior require runtime observation.

#### 3. Tournament Persistence Across Restarts

**Test:** Change the tournament to WMBA or KNBA, then force-close and relaunch the app. Navigate to More → Change Tournament.
**Expected:** The tournament changed in the previous session is still selected (radio button pre-filled with WMBA or KNBA, not defaulting back to MBA).
**Why human:** `Settings()` (SharedPreferences/NSUserDefaults) persistence requires physical device or emulator restart cycle.

---

### Gaps Summary

No gaps. All 13 requirements satisfied, all 14 artifacts exist and are substantive, all 9 key links wired. The only items deferred to human verification are runtime/UI behaviors that cannot be confirmed statically.

---

_Verified: 2026-03-19T20:30:00Z_
_Verifier: Claude (gsd-verifier)_
