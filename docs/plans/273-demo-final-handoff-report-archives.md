# 273 Demo Final Handoff Report Archives

## Goal

Persist the final demo handoff report package as a durable local archive so operators can create, list, reopen, and download the exact closeout artifact after the live demo state changes or the backend restarts.

## Why This Matters

The final handoff report package currently aggregates the latest evidence on demand. That is useful for immediate download, but a demo closeout needs a stable snapshot that can be referenced later. Archiving the package makes the final handoff evidence auditable, repeatable, and easier to share with reviewers.

## Scope

- Add a backend archive VO, entity, converter, mapper, repository interface, in-memory repository, MyBatis repository, Flyway migration, and archive service.
- Expose `POST /api/demo/final-handoff-report-package/archives`, `GET /api/demo/final-handoff-report-package/archives`, and archived Markdown download endpoints.
- Record an operator safety audit when an archive is created.
- Add frontend API helpers, types, dashboard archive controls, recent archive list, and archived report downloads in the demo session panel.
- Update README and execution log.
- Cover the slice with backend service/controller/repository/migration tests and frontend API/panel/App tests.

## Non-Goals

- Do not send external messages or write GitHub comments.
- Do not create tasks, call the model, run tests, mutate Git, push, or open Pull Requests from these endpoints.
- Do not replace the current live final handoff report package endpoint; archives are additive snapshots.

## Validation

- Focused backend tests for archive service, controller endpoints, conversion, repository behavior, and migration text.
- Focused frontend tests for API helpers and demo session panel archive actions.
- Full backend test suite, full frontend test suite, frontend build, and `git diff --check` before merge.
