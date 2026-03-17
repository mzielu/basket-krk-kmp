# Requirements: Basket KRK — PlayerDetails & TeamDetails Migration

**Defined:** 2026-03-16
**Core Value:** Users can drill into any player or team to see detailed game logs, statistics, and records

## v1 Requirements

Requirements for this milestone. Each maps to roadmap phases.

### Player Details — General

- [x] **PLYR-01**: User can view player info header showing name, current team, and list of seasons played
- [x] **PLYR-02**: User can navigate between 3 tabs: Game Logs, Stats, Records

### Player Details — Game Logs

- [x] **PLOG-01**: User can view game logs as a scrollable stat table with fixed player/opponent column and scrollable stat columns
- [x] **PLOG-02**: User can filter game logs by season using a dropdown selector
- [x] **PLOG-03**: User can filter game logs by team (when player played for multiple teams in a season)
- [x] **PLOG-04**: User can sort game logs by clicking any stat column header
- [x] **PLOG-05**: User can click a game log row to navigate to the match details

### Player Details — Stats

- [x] **PSTA-01**: User can view aggregated stats per season/team/league in a scrollable stat table
- [x] **PSTA-02**: User can toggle between average and total stat display
- [x] **PSTA-03**: User can see a totals row at the bottom of the stats table
- [x] **PSTA-04**: User can click a team name in stats to navigate to TeamDetails

### Player Details — Records

- [x] **PREC-01**: User can view a list of player record achievements (type, value, times, date)
- [x] **PREC-02**: User can click a record to navigate to the associated match

### Team Details — General

- [x] **TEAM-01**: User can view team info header showing name, logo, seasons played
- [x] **TEAM-02**: User can see team W-L record and point differential for selected season
- [x] **TEAM-03**: User can navigate between 3 tabs: Results, Roster, Records

### Team Details — Results

- [x] **TRES-01**: User can view match results as a list showing date, opponent, score, W/L status
- [x] **TRES-02**: User can filter results by season using a dropdown selector
- [x] **TRES-03**: User can click a result to navigate to match details

### Team Details — Roster

- [ ] **TROS-01**: User can view team roster as a scrollable stat table with player names and stat columns
- [x] **TROS-02**: User can filter roster by season using a dropdown selector
- [x] **TROS-03**: User can toggle between average and total stat display
- [x] **TROS-04**: User can sort roster by clicking any stat column header
- [ ] **TROS-05**: User can click a player name to navigate to PlayerDetails

### Team Details — Records

- [x] **TREC-01**: User can view team records showing position, player name, value
- [x] **TREC-02**: User can filter records by stat category (PTS, AST, REB, STL, BLK, EFF, FT, FG, 3FG)
- [x] **TREC-03**: User can filter records by range (All-Time, Season, Match)
- [x] **TREC-04**: User can click a record entry to navigate to the player or match

### Navigation Integration

- [ ] **NAV-01**: User can navigate to PlayerDetails from MatchDetails (clicking player name in stat table)
- [ ] **NAV-02**: User can navigate to TeamDetails from Standings (clicking team name)
- [ ] **NAV-03**: User can navigate to PlayerDetails from AllTimeLeaders (clicking player entry)
- [ ] **NAV-04**: User can navigate from PlayerDetails to TeamDetails and vice versa (cross-navigation)
- [ ] **NAV-05**: User can navigate to PlayerDetails or TeamDetails from search results

## v2 Requirements

### Enhanced Features

- **ENH-01**: Pull-to-refresh on all detail screen tabs
- **ENH-02**: Offline caching of previously viewed player/team data
- **ENH-03**: Deep linking to player/team from external URLs

## Out of Scope

| Feature | Reason |
|---------|--------|
| Ad banners | Not part of this migration milestone |
| Firebase analytics/crashlytics | Defer to separate infrastructure milestone |
| iOS-specific UI customizations | Using shared Compose Multiplatform UI |
| New features not in Flutter app | This is a 1:1 migration |
| Player/team comparison | Not in Flutter app |
| Player photo/avatar display | Not in current API |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| PLYR-01 | Phase 1 | Complete |
| PLYR-02 | Phase 1 | Complete |
| PLOG-01 | Phase 2 | Complete |
| PLOG-02 | Phase 2 | Complete |
| PLOG-03 | Phase 2 | Complete |
| PLOG-04 | Phase 2 | Complete |
| PLOG-05 | Phase 2 | Complete |
| PSTA-01 | Phase 2 | Complete |
| PSTA-02 | Phase 2 | Complete |
| PSTA-03 | Phase 2 | Complete |
| PSTA-04 | Phase 2 | Complete |
| PREC-01 | Phase 2 | Complete |
| PREC-02 | Phase 2 | Complete |
| TEAM-01 | Phase 3 | Complete |
| TEAM-02 | Phase 3 | Complete |
| TEAM-03 | Phase 3 | Complete |
| TRES-01 | Phase 4 | Complete |
| TRES-02 | Phase 4 | Complete |
| TRES-03 | Phase 4 | Complete |
| TROS-01 | Phase 4 | Pending |
| TROS-02 | Phase 4 | Complete |
| TROS-03 | Phase 4 | Complete |
| TROS-04 | Phase 4 | Complete |
| TROS-05 | Phase 4 | Pending |
| TREC-01 | Phase 4 | Complete |
| TREC-02 | Phase 4 | Complete |
| TREC-03 | Phase 4 | Complete |
| TREC-04 | Phase 4 | Complete |
| NAV-01 | Phase 5 | Pending |
| NAV-02 | Phase 5 | Pending |
| NAV-03 | Phase 5 | Pending |
| NAV-04 | Phase 5 | Pending |
| NAV-05 | Phase 5 | Pending |

**Coverage:**
- v1 requirements: 33 total
- Mapped to phases: 33
- Unmapped: 0

---
*Requirements defined: 2026-03-16*
*Last updated: 2026-03-16 after roadmap creation — all 33 requirements mapped*
