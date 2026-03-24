---
status: partial
phase: 08-premium
source: [08-VERIFICATION.md]
started: 2026-03-24T20:00:00Z
updated: 2026-03-24T20:00:00Z
---

## Current Test

[awaiting human testing]

## Tests

### 1. Google Play billing sheet on Subscribe tap
expected: Tapping "Subscribe" on Android triggers the Google Play billing sheet with the premium subscription product
result: [pending]

### 2. StoreKit payment sheet on Subscribe tap
expected: Tapping "Subscribe" on iOS triggers the StoreKit payment sheet with the premium subscription product
result: [pending]

### 3. Pagination gate at page 2 for non-premium users
expected: Non-premium user scrolling All-Time Leaders sees at most 2 pages, then PremiumIndicatorCard appears with lock icon and "Upgrade to Premium" button
result: [pending]

### 4. Manage Subscription opens platform manager
expected: Active subscriber tapping "Manage Subscription" is taken to Google Play subscriptions page (Android) or App Store subscriptions page (iOS)
result: [pending]

### 5. Premium gate lifts after mid-session purchase
expected: After completing a purchase on the Premium screen, returning to All-Time Leaders shows all pages (gate removed) without app restart
result: [pending]

## Summary

total: 5
passed: 0
issues: 0
pending: 5
skipped: 0
blocked: 0

## Gaps
