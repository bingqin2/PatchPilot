# Manual Trigger Quarantine Controls

## Goal

Let operators manually create and release trigger-user or repository quarantines from the API and dashboard.

## Why This Matters

PatchPilot can now persist automatically detected abuse quarantines, but a live self-hosted demo still needs explicit operator control. Manual quarantine lets an operator block a noisy user or repository before thresholds are crossed. Manual release lets the operator recover from false positives without waiting for the cooldown.

## Scope

- Add write APIs for creating and releasing trigger quarantines.
- Store operator and release metadata with quarantine records.
- Treat released quarantines as inactive even if their original expiry is in the future.
- Keep automatic threshold-created quarantines working as before.
- Add dashboard controls for manual quarantine creation and release.
- Document curl examples and dashboard behavior for private self-hosted operation.

## Non-Goals

- Do not add public user accounts, login screens, or multi-tenant reputation.
- Do not change GitHub webhook signature handling or task execution policy.
- Do not add IP-based blocking.
- Do not delete historical quarantine records when released.

## Acceptance Criteria

- `POST /api/trigger-quarantines` creates or extends a quarantine for `TRIGGER_USER` or `REPOSITORY`.
- `POST /api/trigger-quarantines/{id}/release` marks a quarantine released and removes it from active checks.
- Active quarantine lookup ignores released records.
- Historical list responses include created-by and release metadata.
- The dashboard can create a manual quarantine and release an active quarantine, then refreshes visible state.
- Backend service/controller/migration tests and frontend API/component tests cover the behavior.
