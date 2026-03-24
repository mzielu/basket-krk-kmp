---
phase: 8
slug: premium
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-03-24
---

# Phase 8 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Kotlin Test / JUnit 4.13.2 |
| **Config file** | `data/build.gradle.kts`, `presentation/build.gradle.kts` |
| **Quick run command** | `./gradlew :domain:jvmTest :data:testDebugUnitTest` |
| **Full suite command** | `./gradlew testDebugUnitTest` |
| **Estimated runtime** | ~30 seconds |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew :domain:jvmTest :data:testDebugUnitTest`
- **After every plan wave:** Run `./gradlew testDebugUnitTest`
- **Before `/gsd:verify-work`:** Full suite must be green
- **Max feedback latency:** 30 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|-----------|-------------------|-------------|--------|
| 08-01-01 | 01 | 1 | PREM-05 | manual | Build + run | N/A | ⬜ pending |
| 08-01-02 | 01 | 1 | PREM-02 | manual | Build + run | N/A | ⬜ pending |
| 08-01-03 | 01 | 1 | PREM-05 | manual | Build + run | N/A | ⬜ pending |
| 08-02-01 | 02 | 2 | PREM-01 | manual | Build + run | N/A | ⬜ pending |
| 08-02-02 | 02 | 2 | PREM-02,03 | manual | Build + run | N/A | ⬜ pending |
| 08-02-03 | 02 | 2 | PREM-04 | manual | Build + run | N/A | ⬜ pending |
| 08-03-01 | 03 | 2 | PREM-06 | manual | Build + run | N/A | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

*Existing infrastructure covers all phase requirements. No new test framework setup needed.*

*Note: IAP features are inherently platform-specific and require device/emulator testing. Automated unit tests are limited to domain-layer logic (use cases, state management). Platform billing flows require manual testing on real devices or sandbox environments.*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Purchase flow on Android | PREM-02 | BillingClient requires Play Store sandbox | 1. Open Premium screen 2. Tap Subscribe 3. Complete Google Play flow 4. Verify green check appears |
| Purchase flow on iOS | PREM-02 | StoreKit requires App Store sandbox | 1. Open Premium screen 2. Tap Subscribe 3. Complete App Store flow 4. Verify green check appears |
| Premium state restore | PREM-05 | Requires app cold restart after purchase | 1. Purchase premium 2. Kill app 3. Relaunch 4. Verify premium still active |
| Manage subscription link | PREM-04 | Platform URL launch | 1. With active premium 2. Tap "Manage subscription" 3. Verify correct store page opens |
| All-Time Leaders gating | PREM-06 | Requires scrolling through paginated list | 1. Without premium 2. Scroll All-Time Leaders past page 2 3. Verify premium indicator card appears 4. Tap "Upgrade to Premium" 5. Verify navigates to Premium screen |
| Price/period display | PREM-01 | Platform-specific product details | 1. Open Premium screen 2. Verify price and period shown correctly (e.g., "$4.99 / 6 months") |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 30s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
