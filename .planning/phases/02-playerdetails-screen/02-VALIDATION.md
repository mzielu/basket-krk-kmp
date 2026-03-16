---
phase: 2
slug: playerdetails-screen
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-16
---

# Phase 2 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | None detected — KMP project has no existing test files |
| **Config file** | none — see Wave 0 |
| **Quick run command** | `./gradlew :domain:compileCommonMainKotlinMetadata` |
| **Full suite command** | `./gradlew :presentation:compileCommonMainKotlinMetadata` |
| **Estimated runtime** | ~30 seconds |

---

## Sampling Rate

- **After every task commit:** `./gradlew :domain:compileCommonMainKotlinMetadata`
- **After every plan wave:** `./gradlew :presentation:compileCommonMainKotlinMetadata`
- **Before `/gsd:verify-work`:** Full compile green
- **Max feedback latency:** ~30 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|-----------|-------------------|-------------|--------|
| 02-01-01 | 01 | 1 | PLOG-01..05 | Compile + Manual | `./gradlew :presentation:compileCommonMainKotlinMetadata` | N/A | ⬜ pending |
| 02-01-02 | 01 | 1 | PSTA-01..04 | Compile + Manual | `./gradlew :presentation:compileCommonMainKotlinMetadata` | N/A | ⬜ pending |
| 02-01-03 | 01 | 1 | PREC-01..02 | Compile + Manual | `./gradlew :presentation:compileCommonMainKotlinMetadata` | N/A | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- No test framework detected. Unit tests deferred.
- Recommendation: Accept compile-time + manual validation for Phase 2.

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Game logs table scrolls with fixed left column | PLOG-01 | UI rendering | Open PlayerDetails, tap Game Logs, scroll horizontally/vertically |
| Season dropdown filters game logs | PLOG-02 | Requires live API + UI | Change season, verify table data updates |
| Team dropdown filters game logs | PLOG-03 | Requires live API + UI | Change team filter, verify rows change |
| Column header sort | PLOG-04 | UI interaction | Tap stat header, verify sort indicator + reordered rows |
| Game log row click → MatchDetails | PLOG-05 | Navigation | Tap game log row, verify MatchDetails opens |
| Stats table with avg/total toggle | PSTA-01, PSTA-02 | UI + calculation | Toggle button, verify values change |
| Stats totals row | PSTA-03 | UI rendering | Scroll to bottom, verify totals row exists |
| Team name click → TeamDetails | PSTA-04 | Navigation | Tap team name, verify navigation fires |
| Records list items display | PREC-01 | UI rendering | Tap Records tab, verify items show type/value/date |
| Record click → MatchDetails | PREC-02 | Navigation | Tap record, verify MatchDetails opens |

---

## Validation Sign-Off

- [ ] All tasks have automated verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 30s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
