# Basket KRK — KMP Basketball App

## What This Is

A Kotlin Multiplatform (Android + iOS) basketball league app for the Basket KRK amateur basketball community in Krakow. The app displays match results, player statistics, team standings, league records, and detailed player/team profiles with game logs, stats, and records. Migrated from Flutter with full feature parity on core screens.

## Core Value

Users can drill into any player or team to see detailed game logs, statistics, and records — the core "deep dive" experience that makes the app useful beyond just checking scores.

## Requirements

### Validated

- ✓ Match list with season/round filtering — existing
- ✓ Match details with per-team stat tables — existing
- ✓ All-time leaders with pagination and stat filtering — existing
- ✓ League standings with season/league filtering — existing
- ✓ Search for players and teams — existing
- ✓ PlayerDetails screen with 3 tabs (Game Logs, Stats, Records) — v1.0
- ✓ TeamDetails screen with 3 tabs (Results, Roster, Records) — v1.0
- ✓ Player game logs: scrollable stat table with season/team filtering, sortable columns — v1.0
- ✓ Player stats: aggregated stats per season/team with avg/total toggle — v1.0
- ✓ Player records: list of record achievements with navigation to match — v1.0
- ✓ Team results: match result list with season filtering, W/L display — v1.0
- ✓ Team roster: player stat table with season filtering, avg/total toggle, sortable — v1.0
- ✓ Team records: filtered by range and stat category — v1.0
- ✓ Full navigation integration: all existing screens link to PlayerDetails/TeamDetails — v1.0
- ✓ Search results navigate to PlayerDetails/TeamDetails — v1.0

### Active

- [ ] Season Leaders screen with season/league/category filtering and player navigation
- [ ] MoreScreen with all 9 items (Tournament Chooser, About Us, Donate, Premium, Terms, Privacy, Email, Facebook, Instagram)
- [ ] Tournament Chooser screen for switching between tournaments
- [ ] Premium/subscription screen with in-app purchase integration
- [ ] Premium gating on All-Time Leaders pagination (page 3+)

### Out of Scope

- iOS-specific UI customizations — using shared Compose UI for both platforms
- Firebase analytics/crashlytics — defer to infrastructure milestone
- Offline caching — defer to future milestone
- Pull-to-refresh — defer to enhancement milestone
- Deep linking to player/team from external URLs — defer to future
- Ad banners — defer to separate monetization phase

## Context

- **Codebase**: ~10,400 LOC Kotlin across domain/data/presentation/shared modules
- **Tech stack**: KMP 2.2.21, Compose Multiplatform 1.9.3, Ktor 3.3.3, Koin 4.1.1, Arrow 2.2.0, Coil 3.3.0
- **API**: Backend at http://130.61.230.255:8000/ — 12 endpoints integrated (4 player, 4 team, 4 existing)
- **Architecture**: Clean Architecture (domain/data/presentation) with MVVM + StateFlow
- **Shared components**: `MatchDetailsTeamTable`, `StatDisplayTypeToggle`, `SortableTopRowCell`, `DropdownFormField`, `ViewStateData`
- **Source of truth**: Flutter app at `~/Documents/Development/flutter/basket_krk` — KMP now has feature parity on PlayerDetails, TeamDetails, and navigation

## Constraints

- **Tech stack**: Kotlin Multiplatform with Compose Multiplatform
- **API compatibility**: Same backend API endpoints as Flutter app
- **Architecture**: Clean Architecture layers (domain/data/presentation)

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Reuse MatchDetailsTeamTable scroll pattern for new tables | Proven synchronized scroll, avoid reimplementation | ✓ Good — created PlayerGameLogsTable, PlayerStatsTable, TeamRosterTable following same 4-layer Box pattern |
| Follow MVVM+StateFlow pattern | Consistency with existing screens | ✓ Good — all ViewModels follow same pattern |
| ViewStateData wrapper per tab | Each tab loads independently, cached until screen exit | ✓ Good — matching Flutter BLoC pattern |
| Extract shared composables (StatDisplayTypeToggle, SortableTopRowCell) | Avoid duplication between player and team screens | ✓ Good — reused across 4 table components |
| Client-side W-L computation | Not returned by API, computed from results list | ✓ Good — matches Flutter behavior |
| Composite `cat` parameter for team records | `"{stat}_{range}"` format matching API | ✓ Good — enums with apiKey + displayName |
| toTeam() extension on existing TeamDto | Avoid TeamDto redeclaration conflict with SearchResultDto | ✓ Good — clean resolution |

## Current Milestone: v1.1 Season Leaders, More & Premium

**Goal:** Add Season Leaders screen, complete MoreScreen with all navigation items including Tournament Chooser, and integrate premium subscription with in-app purchases.

**Target features:**
- Season Leaders with season/league/category filtering
- Full MoreScreen (9 items: tournament chooser, external links, email, premium)
- Tournament Chooser for switching between leagues
- Premium subscription screen with platform-specific in-app purchase
- Premium gating on All-Time Leaders pagination

---
*Last updated: 2026-03-18 after v1.1 milestone start*
