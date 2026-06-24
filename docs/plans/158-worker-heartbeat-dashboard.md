# Plan 158: Worker Heartbeat Dashboard

## Goal

Make the background queue worker observable from the API and dashboard. Operators should be able to tell whether the worker poller has started, is polling, is idle, is executing work, or most recently recorded an execution failure without reading backend logs.

## Scope

- Add an in-memory worker heartbeat read model for the local backend process.
- Record heartbeat updates from `FixTaskQueuePoller` when polling starts, no item is available, an item is claimed, an item completes, or execution fails.
- Expose `GET /api/task-queue/worker-health`.
- Render worker state, poll count, claimed count, latest task id, and latest worker error in the dashboard queue panel.
- Keep existing queue summary and item APIs unchanged.

## Out of Scope

- Persisting worker health in MySQL.
- Multi-instance worker coordination.
- Changing queue claim, retry, cancellation, or task execution behavior.
- Restarting or pausing workers from the dashboard.

## Verification

- Poller tests cover heartbeat recording for idle, completed, and failed polls.
- Worker-health service tests cover initial, idle, claimed, completed, and failed state.
- Controller tests cover the new `worker-health` endpoint.
- Frontend API, queue panel, and app integration tests cover loading and rendering worker health.
