# Plan 155: Demo Session Archive

## Goal

Let an operator archive the current demo session report from the dashboard so a live demo has a recent, copyable record even after the underlying task list and evidence panels continue changing.

## Backend Scope

- Add `POST /api/demo/session-archives` to capture the current demo session snapshot as a Markdown report.
- Add `GET /api/demo/session-archives` to list the most recent archived reports.
- Store archives in process memory for local self-hosted demos, capped to the latest 20 entries.
- Reuse the same snapshot when building archive metadata and Markdown report content.
- Keep archive creation local: it must not create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Frontend Scope

- Add `archiveDemoSession` and `listDemoSessionArchives`.
- Load recent archives with the dashboard refresh.
- Add an `Archive session` action to `DemoSessionSnapshotPanel`.
- Show recent archives below the current snapshot with timestamp, share summary, and a copy-report action.
- Keep archive API failures local to the demo session panel.

## Validation

- Backend service and controller tests for archive creation, newest-first ordering, list capping, and endpoint exposure.
- Frontend API, component, and App tests for archive loading, archiving, and copying archived Markdown.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
