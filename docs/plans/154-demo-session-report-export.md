# Plan 154: Demo Session Report Export

## Goal

Add a read-only Markdown export for the current demo session snapshot so an operator can copy one complete session report before or after a live `/agent fix` run.

## Backend Scope

- Add `GET /api/demo/session-report`.
- Reuse `DemoSessionSnapshotService` so the report is generated from the same evidence, script, runbook, checklist, health contract, share summary, and next actions shown in the dashboard.
- Format session id, status, summary, generated time, share summary, recent Pull Request, recent task, operator checklist, script steps, health contract, next actions, and embedded runbook as Markdown.
- State that the endpoint is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Frontend Scope

- Add `getDemoSessionReport`.
- Add a `Copy session report` action to `DemoSessionSnapshotPanel`.
- Fetch the Markdown only when the operator clicks the copy action.
- Show copy success or failure feedback without hiding the existing snapshot data.

## Validation

- Backend service and controller tests for report formatting, missing evidence, empty lists, health contract, and endpoint exposure.
- Frontend API, component, and App tests for report loading and clipboard copy wiring.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
