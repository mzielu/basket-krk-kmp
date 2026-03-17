---
phase: 4
slug: teamdetails-screen
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-17
---

# Phase 4 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | None detected |
| **Quick run command** | `./gradlew :presentation:compileCommonMainKotlinMetadata` |
| **Full suite command** | `./gradlew :presentation:compileCommonMainKotlinMetadata` |
| **Estimated runtime** | ~30 seconds |

---

## Sampling Rate

- **After every task commit:** `./gradlew :presentation:compileCommonMainKotlinMetadata`
- **After every plan wave:** Full compile
- **Max feedback latency:** ~30 seconds

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Results list shows date, opponent, score, W/L | TRES-01 | UI rendering | Open TeamDetails, tap Results tab |
| Season filter updates results | TRES-02 | Live API | Change season dropdown |
| Result click → MatchDetails | TRES-03 | Navigation | Tap result item |
| Roster table scrolls with fixed player column | TROS-01 | UI rendering | Scroll horizontally/vertically |
| Roster season filter | TROS-02 | Live API | Change season dropdown |
| Roster avg/total toggle | TROS-03 | UI + calculation | Toggle, verify values change |
| Roster sort by column | TROS-04 | UI interaction | Tap column header |
| Roster player click → PlayerDetails | TROS-05 | Navigation | Tap player name |
| Records show position, player, value | TREC-01 | UI rendering | Open Records tab |
| Records stat category filter | TREC-02 | API call | Change stat dropdown |
| Records range filter | TREC-03 | API call | Change range dropdown |
| Record click → player or match | TREC-04 | Navigation | Tap record entry |

---

## Validation Sign-Off

- [ ] All tasks compile successfully
- [ ] Feedback latency < 30s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
