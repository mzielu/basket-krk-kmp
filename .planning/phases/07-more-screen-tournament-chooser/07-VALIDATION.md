---
phase: 7
slug: more-screen-tournament-chooser
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-19
---

# Phase 7 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Manual verification (KMP Compose UI — no test framework for UI) |
| **Config file** | none |
| **Quick run command** | `./gradlew :presentation:compileKotlinIosArm64 :presentation:compileKotlinJvm` |
| **Full suite command** | `./gradlew build` |
| **Estimated runtime** | ~60 seconds |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew :presentation:compileKotlinIosArm64 :presentation:compileKotlinJvm`
- **After every plan wave:** Run `./gradlew build`
- **Before `/gsd:verify-work`:** Full suite must be green
- **Max feedback latency:** 60 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|-----------|-------------------|-------------|--------|
| 07-01-01 | 01 | 1 | MORE-01 | compile | `./gradlew :presentation:compileKotlinJvm` | N/A | ⬜ pending |
| 07-01-02 | 01 | 1 | MORE-02..08 | compile+manual | `./gradlew :presentation:compileKotlinJvm` | N/A | ⬜ pending |
| 07-01-03 | 01 | 1 | MORE-09,10 | compile | `./gradlew :presentation:compileKotlinJvm` | N/A | ⬜ pending |
| 07-02-01 | 02 | 1 | TRNT-01,02 | compile | `./gradlew :data:compileKotlinJvm` | N/A | ⬜ pending |
| 07-02-02 | 02 | 1 | TRNT-03 | compile | `./gradlew :data:compileKotlinJvm` | N/A | ⬜ pending |
| 07-02-03 | 02 | 1 | TRNT-01,02 | compile+manual | `./gradlew :presentation:compileKotlinJvm` | N/A | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

Existing infrastructure covers all phase requirements. No additional test framework needed — phase is primarily UI wiring and service integration verified via compilation and manual testing.

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| URL opens in browser | MORE-02..08 | Platform URL handler — no UI test framework | Tap each MoreScreen item, verify correct URL opens |
| Email client opens | MORE-06 | Platform mailto handler | Tap "Write to Us", verify email pre-filled |
| Tournament radio selection | TRNT-01,02 | Compose UI interaction | Open Tournament Chooser, verify 3 items with radio, tap to switch |
| App reloads after switch | TRNT-03 | Navigation stack clear + rebuild | Switch tournament, verify all tabs reload with new data |
| Premium placeholder | MORE-10 | Navigation flow | Tap "Buy Premium", verify placeholder screen appears |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 60s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
