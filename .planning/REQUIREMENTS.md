# Requirements: Basket KRK — v1.1 Season Leaders, More & Premium

**Defined:** 2026-03-18
**Core Value:** Users can drill into any player or team to see detailed game logs, statistics, and records

## v1 Requirements

Requirements for this milestone. Each maps to roadmap phases.

### Season Leaders

- [x] **SLDR-01**: User can view season leaders as a ranked list showing position, team logo, player name, and stat value
- [x] **SLDR-02**: User can filter season leaders by season using a dropdown selector
- [x] **SLDR-03**: User can filter season leaders by league using a dropdown selector (leagues update based on selected season)
- [x] **SLDR-04**: User can filter season leaders by stat category (PTS, AST, REB, STL, BLK, FT%, FG%, 3FG%)
- [x] **SLDR-05**: User can see additional info per leader (made/attempts for shooting stats, games played for others)
- [x] **SLDR-06**: User can tap a leader entry to navigate to PlayerDetails

### More Screen

- [x] **MORE-01**: User can see a list of 9 navigation items in the More tab
- [x] **MORE-02**: User can tap "About Us" to open the about page in browser
- [x] **MORE-03**: User can tap "Donate" to open the donation page in browser
- [x] **MORE-04**: User can tap "Terms of Use" to open terms page in browser
- [x] **MORE-05**: User can tap "Privacy Policy" to open privacy page in browser
- [x] **MORE-06**: User can tap "Write to Us" to open email client with pre-filled recipient and subject
- [x] **MORE-07**: User can tap "Check Facebook" to open Facebook page in browser
- [x] **MORE-08**: User can tap "Check Instagram" to open Instagram profile in browser
- [x] **MORE-09**: User can tap "Change Tournament" to navigate to Tournament Chooser screen
- [x] **MORE-10**: User can tap "Buy Premium" to navigate to Premium screen

### Tournament Chooser

- [x] **TRNT-01**: User can see a list of available tournaments
- [x] **TRNT-02**: User can select a tournament to switch the active tournament
- [x] **TRNT-03**: App updates the API tournament header after switching and reloads data

### Premium

- [ ] **PREM-01**: User can view the Premium screen showing subscription options with price and duration
- [x] **PREM-02**: User can purchase a subscription via platform-specific in-app purchase (Google Play / App Store)
- [ ] **PREM-03**: User can see active premium status with green confirmation when subscribed
- [ ] **PREM-04**: User can tap "Manage subscription" to open platform subscription management
- [x] **PREM-05**: Premium status is checked and observed across the app (premium active stream)
- [ ] **PREM-06**: All-Time Leaders pagination is gated at page 3+ for non-premium users (shows premium indicator)

## Future Requirements

### Enhancements

- **ENH-01**: Pull-to-refresh on all screens
- **ENH-02**: Offline caching of previously viewed data
- **ENH-03**: Deep linking to player/team from external URLs
- **ENH-04**: Ad banner integration for non-premium users

## Out of Scope

| Feature | Reason |
|---------|--------|
| Ad banners | Separate monetization phase |
| Firebase analytics/crashlytics | Separate infrastructure milestone |
| iOS-specific UI | Using shared Compose UI |
| Offline caching | Future enhancement milestone |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| SLDR-01 | Phase 6 | Complete |
| SLDR-02 | Phase 6 | Complete |
| SLDR-03 | Phase 6 | Complete |
| SLDR-04 | Phase 6 | Complete |
| SLDR-05 | Phase 6 | Complete |
| SLDR-06 | Phase 6 | Complete |
| MORE-01 | Phase 7 | Complete |
| MORE-02 | Phase 7 | Complete |
| MORE-03 | Phase 7 | Complete |
| MORE-04 | Phase 7 | Complete |
| MORE-05 | Phase 7 | Complete |
| MORE-06 | Phase 7 | Complete |
| MORE-07 | Phase 7 | Complete |
| MORE-08 | Phase 7 | Complete |
| MORE-09 | Phase 7 | Complete |
| MORE-10 | Phase 7 | Complete |
| TRNT-01 | Phase 7 | Complete |
| TRNT-02 | Phase 7 | Complete |
| TRNT-03 | Phase 7 | Complete |
| PREM-01 | Phase 8 | Pending |
| PREM-02 | Phase 8 | Complete |
| PREM-03 | Phase 8 | Pending |
| PREM-04 | Phase 8 | Pending |
| PREM-05 | Phase 8 | Complete |
| PREM-06 | Phase 8 | Pending |

**Coverage:**
- v1 requirements: 25 total
- Mapped to phases: 25
- Unmapped: 0

---
*Requirements defined: 2026-03-18*
*Last updated: 2026-03-19 after Phase 07 Plan 02 completion (TRNT-01, TRNT-02, TRNT-03 complete)*
