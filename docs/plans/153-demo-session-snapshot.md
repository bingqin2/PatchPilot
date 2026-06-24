# Plan 153: Demo Session Snapshot

## Goal

Add a read-only demo session snapshot so an operator can capture the current demo status, evidence, script, runbook, checklist, and health contract from one API and one dashboard panel before or after a live `/agent fix` run.

## Backend Scope

- Add `GET /api/demo/session-snapshot`.
- Reuse one current demo evidence bundle to derive the script and runbook for a coherent snapshot.
- Return a stable session id, status, summary, generated time, evidence bundle, script, runbook, operator checklist, health contract, share summary, and next actions.
- State that the endpoint is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Frontend Scope

- Add `getDemoSessionSnapshot`.
- Add `DemoSessionSnapshotPanel` near the existing demo evidence and script panels.
- Render session id, status, share summary, recent task and Pull Request evidence, script step count, operator checklist, health contract, and next actions.
- Keep snapshot loading independent so a snapshot API failure does not hide the rest of the dashboard.

## Validation

- Backend service and controller tests for snapshot generation, endpoint exposure, health contract, and reuse of current evidence.
- Frontend API, component, and App tests for snapshot loading and rendering.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
