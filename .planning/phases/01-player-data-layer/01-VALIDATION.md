---
phase: 1
slug: player-data-layer
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-16
---

# Phase 1 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | None detected — no test files in project |
| **Config file** | None — Wave 0 gap |
| **Quick run command** | Manual smoke test on device/emulator |
| **Full suite command** | Manual full checklist |
| **Estimated runtime** | ~60 seconds (manual) |

---

## Sampling Rate

- **After every task commit:** Manual smoke test — open PlayerDetails, verify no crash, verify loading states appear
- **After every plan wave:** Full manual checklist: header renders, all 3 tabs load, season sort correct, records parse correctly
- **Before `/gsd:verify-work`:** All success criteria green
- **Max feedback latency:** ~60 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|-----------|-------------------|-------------|--------|
| 01-01-01 | 01 | 1 | PLYR-01 | Smoke (manual) | Manual — run app, open PlayerDetails | N/A | ⬜ pending |
| 01-01-02 | 01 | 1 | PLYR-01 | Unit (deferred) | `./gradlew :domain:test` | ❌ W0 gap | ⬜ pending |
| 01-01-03 | 01 | 1 | PLYR-01 | Unit (deferred) | `./gradlew :data:test` | ❌ W0 gap | ⬜ pending |
| 01-01-04 | 01 | 1 | PLYR-02 | Smoke (manual) | Manual — tap through tabs | N/A | ⬜ pending |
| 01-01-05 | 01 | 1 | PLYR-02 | Unit (deferred) | `./gradlew :presentation:test` | ❌ W0 gap | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- No test framework detected in project. Unit tests deferred.
- Existing infrastructure does NOT cover phase requirements.
- Recommendation: Accept manual validation for Phase 1. Defer test framework setup.

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Player header renders name, team, seasons | PLYR-01 | Requires device/emulator with live API | Open PlayerDetails for known player ID, verify header shows correct data |
| Three tabs open without crash | PLYR-02 | UI rendering requires runtime | Tap each tab, verify no crash and loading indicator appears |
| Season sort descending | PLYR-01 | Requires visual confirmation of dropdown order | Check season dropdown shows most recent first |
| Tab data cached on switch-back | PLYR-02 | Requires observing network calls | Switch tabs back and forth, verify no re-fetch |

---

## Validation Sign-Off

- [ ] All tasks have automated verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 60s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
