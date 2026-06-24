# Plan 150: Demo Evidence Bundle

## Goal

Provide one read-only endpoint and dashboard panel that answers whether PatchPilot is ready for a controlled issue-to-PR demo. The bundle should reduce pre-demo checking across readiness, smoke checklist, queue, webhook, safety, adapter, and recent Pull Request panels.

## Backend Scope

- Add `GET /api/demo/evidence-bundle`.
- Aggregate existing read models only: demo readiness, smoke checklist, non-sensitive configuration, adapter fixture verification, queue summary, recent tasks, webhook delivery diagnostics, rejected-trigger summary, and active trigger quarantines.
- Return a single `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status, summary counts, latest evidence records, generated timestamp, and next actions.

## Frontend Scope

- Add a dashboard evidence panel near the existing setup/readiness area.
- Show status, adapter fixture counts, recent task count, failed queue count, rejected-trigger count, active quarantine count, latest webhook delivery, recent task, recent Pull Request link, and next actions.
- Keep the panel read-only and tolerant of endpoint failure so the rest of the dashboard can still load.

## Non-Goals

- Do not create tasks, run tests, mutate queue state, call the model, or write to GitHub.
- Do not replace the detailed readiness, smoke checklist, task detail, or safety panels.

## Validation

- Backend unit and controller tests for the aggregate endpoint.
- Frontend API, component, and app tests for loading and rendering the bundle.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
