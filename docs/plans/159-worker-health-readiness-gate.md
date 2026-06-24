# Plan 159: Worker Health Readiness Gate

## Goal

Promote queue worker heartbeat from a diagnostic panel to an explicit demo-readiness gate. Operators should know whether the worker is fresh enough to run a live issue-to-PR smoke test and what action to take when it is not.

## Scope

- Add a configurable worker heartbeat stale threshold to task queue configuration.
- Extend `GET /api/task-queue/worker-health` with `lastPollAgeMs`, `readinessStatus`, and `operatorAction`.
- Mark worker health as `NEEDS_ATTENTION` when the worker has not started, most recently errored, or has not polled within the stale threshold.
- Add a `Worker heartbeat` check to `GET /api/demo/readiness`.
- Surface worker readiness and last poll age in the queue panel.
- Add worker heartbeat to the operator setup checklist.
- Show the stale threshold in configuration summary and dashboard configuration health.

## Out of Scope

- Persisting worker heartbeat records in MySQL.
- Coordinating multiple worker instances.
- Restarting, pausing, or scaling workers from the dashboard.
- Changing task execution or queue claim semantics.

## Verification

- Worker-health service tests cover not-started, ready, error, and stale readiness.
- Task queue controller tests cover the new response fields.
- Demo readiness tests cover worker heartbeat as a readiness check.
- Configuration tests cover the new stale-threshold field.
- Frontend API, queue panel, operator checklist, configuration panel, and app integration tests cover rendering and setup gating.
