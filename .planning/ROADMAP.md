# Roadmap: Basket KRK — PlayerDetails & TeamDetails Migration

## Overview

This milestone completes the core deep-dive experience by migrating PlayerDetails and TeamDetails from the Flutter app to KMP. The work proceeds feature by feature: build the player data layer and screen first, then the team data layer and screen, then wire all navigation entry points across the existing screens. Each phase delivers a coherent, independently verifiable capability on top of established MVVM + StateFlow patterns.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 1: Player Data Layer** - DTOs, Ktor endpoints, and repositories for all four player API endpoints (completed 2026-03-16)
- [x] **Phase 2: PlayerDetails Screen** - Full three-tab PlayerDetails Compose screen with ViewModel and all interactions (completed 2026-03-16)
- [ ] **Phase 3: Team Data Layer** - DTOs, Ktor endpoints, and repositories for all four team API endpoints
- [ ] **Phase 4: TeamDetails Screen** - Full three-tab TeamDetails Compose screen with ViewModel and all interactions
- [ ] **Phase 5: Navigation Integration** - Wire all click paths from existing screens into PlayerDetails and TeamDetails

## Phase Details

### Phase 1: Player Data Layer
**Goal**: The app can fetch, decode, and expose all player data needed by the PlayerDetails screen
**Depends on**: Nothing (first phase)
**Requirements**: PLYR-01, PLYR-02
**Success Criteria** (what must be TRUE):
  1. Player info header (name, current team, seasons played) renders correctly from live API data
  2. The PlayerDetails screen shell with three tabs opens without crashing when given a player ID
  3. Each tab shows a loading state while its data is being fetched, confirming the repository calls are wired up
**Plans:** 2/2 plans complete
Plans:
- [x] 01-01-PLAN.md — Data layer: domain models, DTOs, service, repository
- [x] 01-02-PLAN.md — Presentation layer: use cases, ViewModel, screen, DI, navigation

### Phase 2: PlayerDetails Screen
**Goal**: Users can open any player and explore their game logs, aggregated stats, and records across all seasons
**Depends on**: Phase 1
**Requirements**: PLOG-01, PLOG-02, PLOG-03, PLOG-04, PLOG-05, PSTA-01, PSTA-02, PSTA-03, PSTA-04, PREC-01, PREC-02
**Success Criteria** (what must be TRUE):
  1. User can view game logs as a scrollable stat table, filter by season and team, sort by any column header
  2. User can view aggregated stats per season/team, toggle between average and total, and see a totals row
  3. User can view a list of record achievements and click any record to open the associated match details
  4. User can tap a team name in the stats tab to open TeamDetails for that team
**Plans:** 2/2 plans complete
Plans:
- [ ] 02-01-PLAN.md — Domain extensions, ViewModel state/handlers, navigation wiring
- [ ] 02-02-PLAN.md — Game Logs/Stats/Records tab composables and PlayerDetailsScreen integration

### Phase 3: Team Data Layer
**Goal**: The app can fetch, decode, and expose all team data needed by the TeamDetails screen
**Depends on**: Phase 2
**Requirements**: TEAM-01, TEAM-02, TEAM-03
**Success Criteria** (what must be TRUE):
  1. Team info header (name, logo, seasons played) renders correctly from live API data
  2. Team W-L record and point differential display for the selected season
  3. The TeamDetails screen shell with three tabs opens without crashing when given a team ID
**Plans**: TBD

### Phase 4: TeamDetails Screen
**Goal**: Users can open any team and browse their results, roster with stats, and team records
**Depends on**: Phase 3
**Requirements**: TRES-01, TRES-02, TRES-03, TROS-01, TROS-02, TROS-03, TROS-04, TROS-05, TREC-01, TREC-02, TREC-03, TREC-04
**Success Criteria** (what must be TRUE):
  1. User can view match results with date, opponent, score, and W/L status, and filter by season
  2. User can view roster as a scrollable stat table, filter by season, toggle avg/total, sort by column, and tap a player to open PlayerDetails
  3. User can view team records filtered by stat category (PTS, AST, REB, STL, BLK, EFF, FT, FG, 3FG) and range (All-Time, Season, Match)
  4. User can tap a record entry to navigate to the associated player or match
**Plans**: TBD

### Phase 5: Navigation Integration
**Goal**: Users can reach PlayerDetails and TeamDetails from every existing entry point in the app
**Depends on**: Phase 4
**Requirements**: NAV-01, NAV-02, NAV-03, NAV-04, NAV-05
**Success Criteria** (what must be TRUE):
  1. Tapping a player name in MatchDetails stat table opens that player's PlayerDetails screen
  2. Tapping a team name in Standings opens that team's TeamDetails screen
  3. Tapping a player entry in AllTimeLeaders opens that player's PlayerDetails screen
  4. PlayerDetails and TeamDetails link to each other (player stat row links to team, team roster row links to player)
  5. Selecting a player or team from search results opens their respective detail screen
**Plans**: TBD

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Player Data Layer | 2/2 | Complete    | 2026-03-16 |
| 2. PlayerDetails Screen | 2/2 | Complete   | 2026-03-16 |
| 3. Team Data Layer | 0/TBD | Not started | - |
| 4. TeamDetails Screen | 0/TBD | Not started | - |
| 5. Navigation Integration | 0/TBD | Not started | - |
