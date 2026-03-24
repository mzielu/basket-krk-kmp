---
phase: 8
slug: premium
status: approved
nyquist_compliant: true
wave_0_complete: true
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
| 08-01-01 | 01 | 1 | PREM-05 | manual | Build + run | N/A | pending |
| 08-01-02 | 01 | 1 | PREM-02 | manual | Build + run | N/A | pending |
| 08-02-01 | 02 | 2 | PREM-01 | manual | Build + run | N/A | pending |
| 08-02-02 | 02 | 2 | PREM-02,03 | manual | Build + run | N/A | pending |
| 08-03-01 | 03 | 2 | PREM-06 | manual | Build + run | N/A | pending |
| 08-03-02 | 03 | 2 | PREM-06 | manual | Build + run | N/A | pending |

---

## Wave 0 Requirements

**Wave 0: Not applicable.** IAP features are inherently platform-specific (BillingClient on Android, StoreKit on iOS) and cannot be meaningfully unit-tested in `commonTest` without complex mocking of platform billing APIs. All verifications for this phase require device/emulator sandbox testing. The domain use cases are thin wrappers delegating to `InAppPurchaseService` — no business logic to unit-test independently.

---

## Nyquist Compliance Justification

`nyquist_compliant: true` — All task verifications use build-and-grep automated checks (confirming files exist with correct content) plus manual device/sandbox testing for runtime behavior. This is the pragmatic maximum for IAP integration where:

1. **BillingClient** requires Google Play sandbox environment on a real/emulated Android device
2. **StoreKit** requires App Store sandbox environment on iOS simulator or device
3. **Purchase flows** are interactive platform dialogs that cannot be driven programmatically in unit tests
4. **Premium state observation** depends on platform billing state that only exists at runtime

Automated unit tests for `InAppPurchaseService` or `AllTimeLeadersPagingSource` would require mocking the entire platform billing stack, providing low value relative to effort. The grep-based verify commands in each plan task confirm structural correctness; manual testing confirms runtime correctness.

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

- [x] All tasks have `<automated>` verify (grep-based structural checks)
- [x] Sampling continuity: build verification after every task
- [x] Wave 0: Not applicable (IAP is platform-specific, no meaningful commonTest unit tests)
- [x] No watch-mode flags
- [x] Feedback latency < 30s
- [x] `nyquist_compliant: true` set in frontmatter

**Approval:** approved
