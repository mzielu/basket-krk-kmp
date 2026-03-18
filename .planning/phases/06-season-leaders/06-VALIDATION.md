---
phase: 6
slug: season-leaders
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-18
---

# Phase 6 — Validation Strategy

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
- **Max feedback latency:** ~30 seconds

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Leader list shows position, logo, name, value | SLDR-01 | UI rendering | Open Season Leaders, verify list items |
| Season dropdown filters leagues | SLDR-02 | Cascading API call | Change season, verify leagues update |
| League dropdown filters leaders | SLDR-03 | API call | Change league, verify leaders update |
| Category dropdown filters leaders | SLDR-04 | API call | Change category, verify leaders update |
| Shooting stats show made/attempts | SLDR-05 | UI rendering | Select FT%/FG%/3FG%, verify info |
| Tap leader → PlayerDetails | SLDR-06 | Navigation | Tap leader item |

---

## Validation Sign-Off

- [ ] All tasks compile successfully
- [ ] Feedback latency < 30s

**Approval:** pending
