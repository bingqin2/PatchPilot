# 234 - Handoff Package Archive Summary

## Goal

Make the latest archived demo handoff package easier to validate before sharing. Operators should not need to inspect the archive list row by row to know whether a final handoff package exists, whether the latest package is share-ready, and what action remains.

## Scope

- Add a read-only backend summary endpoint for handoff package archives.
- Report archive count, latest archive id, latest session id, latest handoff readiness status, latest capture time, share-ready flag, summary, next action, and Markdown evidence.
- Render the summary in the dashboard above the recent handoff package archive list.
- Refresh the summary after the dashboard archives a new handoff package.

## API

- `GET /api/demo/handoff-package-archives/summary`

The endpoint is read-only over stored handoff package archives. It must not create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub.

## Frontend Behavior

`DemoSessionSnapshotPanel` shows:

- `Share-ready` when the latest archived package has `READY` handoff readiness.
- The number of archived packages.
- The latest archive id, if one exists.
- The next action from the latest archive or a capture-first action when no archive exists.

## Verification

- Backend service and controller tests cover ready and no-archive summaries.
- Frontend API, app, and component tests cover fetching and rendering the summary.
- Full backend and frontend suites must pass before merge.

## Non-Goals

- Do not add a new archive write path.
- Do not publish handoff packages externally.
- Do not change handoff readiness calculation rules.
