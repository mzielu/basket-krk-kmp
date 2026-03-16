# Basket KRK — Flutter to KMP Migration (PlayerDetails & TeamDetails)

## What This Is

A Kotlin Multiplatform (Android + iOS) basketball league app for the Basket KRK amateur basketball community in Krakow. The app displays match results, player statistics, team standings, and league records. This milestone focuses on migrating the PlayerDetails and TeamDetails screens from the existing Flutter app to complete the core feature set.

## Core Value

Users can drill into any player or team to see detailed game logs, statistics, and records — the core "deep dive" experience that makes the app useful beyond just checking scores.

## Requirements

### Validated

- Match list with season/round filtering — existing
- Match details with per-team stat tables — existing
- All-time leaders with pagination and stat filtering — existing
- League standings with season/league filtering — existing
- Search for players and teams — existing (basic)
- Navigation between screens (match → player/team) — partially existing

### Active

- [ ] PlayerDetails screen with 3 tabs (Game Logs, Stats, Records)
- [ ] TeamDetails screen with 3 tabs (Results, Roster, Records)
- [ ] Player game logs: scrollable stat table with season/team filtering, sortable columns
- [ ] Player stats: aggregated stats per season/team with avg/total toggle
- [ ] Player records: list of record achievements with navigation to match
- [ ] Team results: match result list with season filtering, W/L display
- [ ] Team roster: player stat table with season filtering, avg/total toggle, sortable
- [ ] Team records: filtered by range (all-time/season/match) and stat category (PTS, AST, REB, etc.)
- [ ] Navigation integration: player/team clicks from existing screens navigate to detail screens
- [ ] Search integration: search results navigate to PlayerDetails/TeamDetails

### Out of Scope

- iOS-specific UI customizations — using shared Compose UI for both platforms
- Ads/monetization — not part of this migration milestone
- Firebase analytics/crashlytics — defer to later
- New features not in Flutter app — 1:1 migration with minor improvements only
- Other remaining Flutter screens — will be separate milestones

## Context

- **Source of truth**: Existing Flutter app at `~/Documents/Development/flutter/basket_krk` — screens should match behavior
- **API**: Same backend API (http://130.61.230.255:8000/) already used by existing KMP screens
- **Existing patterns**: MVVM + StateFlow, Koin DI, Ktor client, Arrow Either error handling, Compose Multiplatform
- **Reusable components**: `MatchDetailsTeamTable` (scrollable stat table with fixed columns), `ViewStateData`, stat models, season/league models
- **API endpoints to integrate**:
  - `/player/{id}/` — player details (name, seasons, team)
  - `/player/{id}/logs?season_id={id}` — game logs per season
  - `/player/{id}/stats/` — aggregated statistics
  - `/player/{id}/records/` — player records
  - `/team/{id}/` — team details (name, logo, seasons)
  - `/team/{id}/results?season_id={id}` — match results per season
  - `/team/{id}/players?season_id={id}` — roster with stats per season
  - `/team/{id}/records?cat={category}` — team records by category
- **Shared models already exist**: Stat, Season, League, PlayerWithStat, StatOption, SearchItem.Player, SearchItem.Team

## Constraints

- **Tech stack**: Kotlin Multiplatform with Compose Multiplatform — must follow existing project patterns
- **API compatibility**: Must use existing API endpoints unchanged (same as Flutter app)
- **Architecture**: Must follow established Clean Architecture layers (domain/data/presentation)
- **1:1 migration**: Feature parity with Flutter app, minor improvements allowed but no new features

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Reuse MatchDetailsTeamTable for stat tables | Already handles synchronized scrolling, fixed columns, stat display | — Pending |
| Follow existing MVVM+StateFlow pattern | Consistency with MatchDetails, Standings, AllTimeLeaders screens | — Pending |
| Use ViewStateData wrapper per tab | Each tab loads independently, matching Flutter BLoC pattern | — Pending |

---
*Last updated: 2026-03-16 after initialization*
