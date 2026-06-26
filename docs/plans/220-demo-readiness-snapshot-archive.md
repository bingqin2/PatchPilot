# 220 - Demo Readiness Snapshot Archive

## Goal

Archive the current demo readiness result as local Markdown evidence so operators can prove why a live `/agent fix` demo was ready, warned, or blocked at a specific time.

## Scope

- Add admin-protected demo readiness snapshot endpoints:
  - `POST /api/demo/readiness-snapshots`
  - `GET /api/demo/readiness-snapshots`
  - `GET /api/demo/readiness-snapshots/{snapshotId}/report/download`
- Store snapshot id, readiness status, summary, check counts, created time, and Markdown report.
- Provide in-memory and MySQL-backed storage using the existing demo archive repository pattern.
- Add a dashboard action in `Demo readiness` to archive the current readiness and inspect/copy recent snapshots.
- Update README, product docs, frontend design docs, and execution log.

## Non-Goals

- Do not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.
- Do not replace the full demo session archive; this is a narrower readiness-only evidence artifact.

## Validation

- Backend focused tests for service, controller, repository, and migration.
- Frontend focused tests for API helpers, App wiring, and panel snapshot actions.
- Full backend test suite, full frontend test suite, production frontend build, and `git diff --check`.
