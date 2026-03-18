# Roadmap: Basket KRK

## Milestones

- ✅ **v1.0 PlayerDetails & TeamDetails Migration** — Phases 1-5 (shipped 2026-03-17)
- 🚧 **v1.1 Season Leaders, More & Premium** — Phases 6-8 (in progress)

## Phases

<details>
<summary>✅ v1.0 PlayerDetails & TeamDetails Migration (Phases 1-5) — SHIPPED 2026-03-17</summary>

- [x] Phase 1: Player Data Layer (2/2 plans) — completed 2026-03-16
- [x] Phase 2: PlayerDetails Screen (2/2 plans) — completed 2026-03-16
- [x] Phase 3: Team Data Layer (2/2 plans) — completed 2026-03-16
- [x] Phase 4: TeamDetails Screen (3/3 plans) — completed 2026-03-17
- [x] Phase 5: Navigation Integration (1/1 plan) — completed 2026-03-17

See: `.planning/milestones/v1.0-ROADMAP.md` for full details.

</details>

### 🚧 v1.1 Season Leaders, More & Premium (In Progress)

**Milestone Goal:** Add Season Leaders screen, complete MoreScreen with Tournament Chooser, and integrate premium subscription with in-app purchases.

- [x] **Phase 6: Season Leaders** — Full data layer and screen with filtering and player navigation (completed 2026-03-18)
- [ ] **Phase 7: More Screen & Tournament Chooser** — Complete MoreScreen with 9 items including Tournament Chooser sub-screen
- [ ] **Phase 8: Premium** — In-app purchase integration, premium screen, and All-Time Leaders gating

## Phase Details

### Phase 6: Season Leaders
**Goal**: Users can browse season leaders with full filtering and navigate to any player
**Depends on**: Phase 5 (navigation graph exists)
**Requirements**: SLDR-01, SLDR-02, SLDR-03, SLDR-04, SLDR-05, SLDR-06
**Success Criteria** (what must be TRUE):
  1. User can open Season Leaders and see a ranked list with position, team logo, player name, and stat value
  2. User can switch season, league, and stat category via dropdowns and the list updates accordingly
  3. User can see shooting details (made/attempts) for FT%, FG%, 3FG% leaders and games played for counting stats
  4. User can tap any leader entry and land on the correct PlayerDetails screen
**Plans:** 2/2 plans complete

Plans:
- [ ] 06-01-PLAN.md — Use case and ViewModel with cascading filter state management
- [ ] 06-02-PLAN.md — Screen UI, leader item component, navigation wiring, and DI registration

### Phase 7: More Screen & Tournament Chooser
**Goal**: Users can access all secondary app functions from the More tab including tournament switching
**Depends on**: Phase 6
**Requirements**: MORE-01, MORE-02, MORE-03, MORE-04, MORE-05, MORE-06, MORE-07, MORE-08, MORE-09, MORE-10, TRNT-01, TRNT-02, TRNT-03
**Success Criteria** (what must be TRUE):
  1. User can see all 9 items listed in the More tab (Tournament Chooser, About Us, Donate, Premium, Terms, Privacy, Email, Facebook, Instagram)
  2. User can tap any external link item (About Us, Donate, Terms, Privacy, Facebook, Instagram) and the correct URL opens in the browser
  3. User can tap "Write to Us" and the email client opens pre-filled with the correct recipient and subject
  4. User can tap "Change Tournament", see a list of available tournaments, select one, and the app switches its active tournament context and reloads data
  5. User can tap "Buy Premium" and land on the Premium screen
**Plans**: TBD

Plans:
- [ ] 07-01: MoreScreen UI with all 9 items and external link/email actions
- [ ] 07-02: Tournament Chooser screen and tournament switching logic

### Phase 8: Premium
**Goal**: Users can purchase and manage a premium subscription, and non-premium users see a gate on All-Time Leaders pagination
**Depends on**: Phase 7
**Requirements**: PREM-01, PREM-02, PREM-03, PREM-04, PREM-05, PREM-06
**Success Criteria** (what must be TRUE):
  1. User can open the Premium screen and see available subscription options with price and duration
  2. User can initiate a purchase via the native platform flow (Google Play on Android, App Store on iOS) and the app updates to reflect active premium status
  3. User with active premium sees a green confirmation on the Premium screen and has no pagination restriction on All-Time Leaders
  4. User without premium sees a premium indicator instead of page 3+ content in All-Time Leaders
  5. User can tap "Manage subscription" and is taken to the platform's subscription management page
**Plans**: TBD

Plans:
- [ ] 08-01: Premium data layer (BillingClient/StoreKit integration, purchase state stream, repository)
- [ ] 08-02: Premium screen UI and purchase flow
- [ ] 08-03: Premium gating on All-Time Leaders pagination

## Progress

**Execution Order:**
Phases execute in numeric order: 6 → 7 → 8

| Phase | Milestone | Plans Complete | Status | Completed |
|-------|-----------|----------------|--------|-----------|
| 1. Player Data Layer | v1.0 | 2/2 | Complete | 2026-03-16 |
| 2. PlayerDetails Screen | v1.0 | 2/2 | Complete | 2026-03-16 |
| 3. Team Data Layer | v1.0 | 2/2 | Complete | 2026-03-16 |
| 4. TeamDetails Screen | v1.0 | 3/3 | Complete | 2026-03-17 |
| 5. Navigation Integration | v1.0 | 1/1 | Complete | 2026-03-17 |
| 6. Season Leaders | 2/2 | Complete   | 2026-03-18 | - |
| 7. More Screen & Tournament Chooser | v1.1 | 0/2 | Not started | - |
| 8. Premium | v1.1 | 0/3 | Not started | - |
