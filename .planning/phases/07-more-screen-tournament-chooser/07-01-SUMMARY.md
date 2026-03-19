---
phase: 07-more-screen-tournament-chooser
plan: 01
subsystem: ui
tags: [compose, navigation, more-screen, kmp]

# Dependency graph
requires:
  - phase: 06-season-leaders
    provides: SeasonLeaders route and NavHost composable pattern

provides:
  - MoreConstants.kt with MoreUrls object (6 external URL constants)
  - Fully wired MoreScreen with all 9 items functional (6 URLs, 1 email, 1 tournament nav, 1 premium nav)
  - Screen.TournamentChooser and Screen.Premium navigation routes
  - PremiumScreen placeholder (ActionBar + Coming soon)
  - MainScreen openTournamentChooser and openPremium callbacks
  - App.kt NavHost entries for TournamentChooser (placeholder) and Premium

affects:
  - 07-02 (TournamentChooser will replace placeholder composable in Screen.TournamentChooser)

# Tech tracking
tech-stack:
  added: []
  patterns:
    - MoreUrls object for URL constants (follows existing constants pattern)
    - Placeholder NavHost composable entry for routes not yet implemented (avoids compile errors while keeping route defined)

key-files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/more/MoreConstants.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/premium/PremiumScreen.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/more/MoreScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/MainScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt

key-decisions:
  - "Screen.TournamentChooser composable entry in App.kt is a placeholder Box with text — Plan 02 will replace it with real TournamentChooserScreen wired to a ViewModel"
  - "PremiumScreen is a placeholder (Coming soon) — full premium feature deferred to Phase 08 due to platform-specific IAP complexity"

patterns-established:
  - "URL constants for external links extracted to MoreUrls object in MoreConstants.kt"
  - "Placeholder NavHost composable entry pattern: add route to NavHost as Box with descriptive text to keep compilation valid while implementation is deferred"

requirements-completed:
  - MORE-01
  - MORE-02
  - MORE-03
  - MORE-04
  - MORE-05
  - MORE-06
  - MORE-07
  - MORE-08
  - MORE-09
  - MORE-10

# Metrics
duration: 8min
completed: 2026-03-19
---

# Phase 7 Plan 01: More Screen & Navigation Wiring Summary

**MoreScreen fully wired with 9 functional items: 6 external URLs via MoreUrls constants, email via existing mailto, plus TournamentChooser and Premium navigation routes with placeholder screens**

## Performance

- **Duration:** 8 min
- **Started:** 2026-03-19T19:11:00Z
- **Completed:** 2026-03-19T19:19:12Z
- **Tasks:** 2
- **Files modified:** 6

## Accomplishments

- Created MoreConstants.kt with MoreUrls object containing 6 typed URL constants (ABOUT_US, DONATE, TERMS, PRIVACY, FACEBOOK, INSTAGRAM)
- Replaced all 7 TODO comments in MoreScreen.kt with live implementations: 6 `uriHandler.openUri(MoreUrls.*)` calls and cleaned up the email item
- Added TournamentChooser and Premium to Screen.kt, wired full navigation chain through MainScreen callbacks to App.kt NavHost composable entries
- Created PremiumScreen placeholder with ActionBar and "Coming soon" text ready for Phase 08 premium implementation

## Task Commits

Each task was committed atomically:

1. **Task 1: Create MoreConstants and wire all MoreScreen URL actions** - `9a55334` (feat)
2. **Task 2: Add navigation routes, Premium placeholder, and wire MainScreen callbacks through App** - `7065b55` (feat)

## Files Created/Modified

- `presentation/.../more/MoreConstants.kt` - MoreUrls object with 6 external URL constants
- `presentation/.../more/MoreScreen.kt` - All 6 URL onClick handlers replaced with uriHandler.openUri(MoreUrls.*), TODO comments removed
- `presentation/.../navigation/Screen.kt` - TournamentChooser and Premium data object routes added
- `presentation/.../screens/premium/PremiumScreen.kt` - Placeholder screen with ActionBar (back button) and "Coming soon" message
- `presentation/.../screens/main/MainScreen.kt` - Added openTournamentChooser and openPremium callback params, wired to MoreScreen instead of empty lambdas
- `presentation/.../App.kt` - Updated Screen.Main block with two new callbacks, added composable entries for TournamentChooser (placeholder) and Premium (real PremiumScreen)

## Decisions Made

- Kept Screen.TournamentChooser composable as a Box placeholder in App.kt (not a separate Composable function) — avoids creating an unnecessary composable that would be deleted in Plan 02
- PremiumScreen uses standard Scaffold+ActionBar pattern matching other detail screens in the project
- Added `Text` import to App.kt for placeholder TournamentChooser composable entry

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None — build succeeded on first attempt (only pre-existing deprecation warning unrelated to this plan's changes).

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- Plan 02 (TournamentChooser screen) can start immediately — Screen.TournamentChooser route exists and placeholder composable is in NavHost
- All MoreScreen items are functional — no remaining TODOs
- PremiumScreen route navigates from More tab correctly
