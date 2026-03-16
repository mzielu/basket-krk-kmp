# Requirements: Basket KRK — PlayerDetails & TeamDetails Migration

**Defined:** 2026-03-16
**Core Value:** Users can drill into any player or team to see detailed game logs, statistics, and records

## v1 Requirements

Requirements for this milestone. Each maps to roadmap phases.

### Player Details — General

- [ ] **PLYR-01**: User can view player info header showing name, current team, and list of seasons played
- [ ] **PLYR-02**: User can navigate between 3 tabs: Game Logs, Stats, Records

### Player Details — Game Logs

- [ ] **PLOG-01**: User can view game logs as a scrollable stat table with fixed player/opponent column and scrollable stat columns
- [ ] **PLOG-02**: User can filter game logs by season using a dropdown selector
- [ ] **PLOG-03**: User can filter game logs by team (when player played for multiple teams in a season)
- [ ] **PLOG-04**: User can sort game logs by clicking any stat column header
- [ ] **PLOG-05**: User can click a game log row to navigate to the match details

### Player Details — Stats

- [ ] **PSTA-01**: User can view aggregated stats per season/team/league in a scrollable stat table
- [ ] **PSTA-02**: User can toggle between average and total stat display
- [ ] **PSTA-03**: User can see a totals row at the bottom of the stats table
- [ ] **PSTA-04**: User can click a team name in stats to navigate to TeamDetails

### Player Details — Records

- [ ] **PREC-01**: User can view a list of player record achievements (type, value, times, date)
- [ ] **PREC-02**: User can click a record to navigate to the associated match

### Team Details — General

- [ ] **TEAM-01**: User can view team info header showing name, logo, seasons played
- [ ] **TEAM-02**: User can see team W-L record and point differential for selected season
- [ ] **TEAM-03**: User can navigate between 3 tabs: Results, Roster, Records

### Team Details — Results

- [ ] **TRES-01**: User can view match results as a list showing date, opponent, score, W/L status
- [ ] **TRES-02**: User can filter results by season using a dropdown selector
- [ ] **TRES-03**: User can click a result to navigate to match details

### Team Details — Roster

- [ ] **TROS-01**: User can view team roster as a scrollable stat table with player names and stat columns
- [ ] **TROS-02**: User can filter roster by season using a dropdown selector
- [ ] **TROS-03**: User can toggle between average and total stat display
- [ ] **TROS-04**: User can sort roster by clicking any stat column header
- [ ] **TROS-05**: User can click a player name to navigate to PlayerDetails

### Team Details — Records

- [ ] **TREC-01**: User can view team records showing position, player name, value
- [ ] **TREC-02**: User can filter records by stat category (PTS, AST, REB, STL, BLK, EFF, FT, FG, 3FG)
- [ ] **TREC-03**: User can filter records by range (All-Time, Season, Match)
- [ ] **TREC-04**: User can click a record entry to navigate to the player or match

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
| PLYR-01 | — | Pending |
| PLYR-02 | — | Pending |
| PLOG-01 | — | Pending |
| PLOG-02 | — | Pending |
| PLOG-03 | — | Pending |
| PLOG-04 | — | Pending |
| PLOG-05 | — | Pending |
| PSTA-01 | — | Pending |
| PSTA-02 | — | Pending |
| PSTA-03 | — | Pending |
| PSTA-04 | — | Pending |
| PREC-01 | — | Pending |
| PREC-02 | — | Pending |
| TEAM-01 | — | Pending |
| TEAM-02 | — | Pending |
| TEAM-03 | — | Pending |
| TRES-01 | — | Pending |
| TRES-02 | — | Pending |
| TRES-03 | — | Pending |
| TROS-01 | — | Pending |
| TROS-02 | — | Pending |
| TROS-03 | — | Pending |
| TROS-04 | — | Pending |
| TROS-05 | — | Pending |
| TREC-01 | — | Pending |
| TREC-02 | — | Pending |
| TREC-03 | — | Pending |
| TREC-04 | — | Pending |
| NAV-01 | — | Pending |
| NAV-02 | — | Pending |
| NAV-03 | — | Pending |
| NAV-04 | — | Pending |
| NAV-05 | — | Pending |

**Coverage:**
- v1 requirements: 33 total
- Mapped to phases: 0
- Unmapped: 33

---
*Requirements defined: 2026-03-16*
*Last updated: 2026-03-16 after initial definition*
