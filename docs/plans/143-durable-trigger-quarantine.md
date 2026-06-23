# Durable Trigger Quarantine

## Goal

Persist rejected-trigger quarantine state so repeated abusive trigger patterns remain visible and enforceable across worker restarts and operator dashboard refreshes.

## Why This Matters

PatchPilot already rejects repeated bad `/agent fix` attempts, but the current decision is derived from recent rejected-trigger audit rows at runtime. A durable quarantine record makes the safety state explicit: operators can see who or what is quarantined, why, when it expires, and which threshold produced it.

## Scope

- Add a `trigger_quarantine` persistence model for trigger-user and repository quarantine records.
- Keep in-memory support for default tests and MyBatis-backed support for local/docker/idea profiles.
- Check active quarantine records before recomputing thresholds from rejected-trigger audit history.
- Create or extend a quarantine record when recent rejected attempts cross the configured threshold.
- Preserve existing `ABUSE_QUARANTINED` rejected-trigger audit behavior for webhook and manual triggers.
- Add a read-only `GET /api/trigger-quarantines` endpoint with active-only filtering.
- Show active quarantine records in the dashboard alongside rejected-trigger diagnostics.

## Non-Goals

- Do not add manual quarantine creation.
- Do not add manual unquarantine or admin mutation controls.
- Do not add IP reputation, account suspension, billing limits, or hosted-service abuse scoring.
- Do not change non-triggering comment handling.

## Acceptance Criteria

- An active trigger-user quarantine causes matching webhook and manual requests to be rejected before rate limits, model classification, task creation, queueing, cloning, or workspace work.
- An active repository quarantine causes matching repository requests to be rejected before expensive work.
- When audit-derived thresholds are crossed, PatchPilot persists a quarantine record with scope, scope key, reason, category, evidence count, window, start, expiry, and timestamps.
- Restart-capable profiles can query the persisted quarantine records through MyBatis-backed storage.
- Operators can list active and historical quarantine records through `/api/trigger-quarantines`.
- The dashboard displays active quarantine records and their expiry time.
- The feature is covered by backend service/controller/migration tests and frontend API/component tests.
