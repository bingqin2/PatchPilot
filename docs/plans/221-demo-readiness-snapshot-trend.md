# 221 - Demo Readiness Snapshot Trend

## Goal

Compare the two most recent demo readiness snapshots so an operator can see whether demo readiness is improving, stable, or regressing before a live `/agent fix` run.

## Scope

- Add admin-protected `GET /api/demo/readiness-snapshots/summary`.
- Compare the latest two archived readiness snapshots.
- Return trend status, latest and previous snapshot ids, readiness statuses, ready/warning/blocked check-count deltas, next action, and Markdown report.
- Show the trend in the dashboard `Demo readiness` panel with a copyable Markdown report.
- Refresh the trend after an operator archives a new readiness snapshot.
- Update README, product docs, frontend design docs, AI infrastructure target, and execution log.

## Non-Goals

- Do not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.
- Do not replace session archives or fixture baseline regression summaries.
- Do not infer trends from live readiness; only compare stored readiness snapshots.

## Validation

- Backend focused tests for trend service and controller serialization.
- Frontend focused tests for API helper, panel rendering/copy action, and App wiring.
- Full backend test suite, full frontend test suite, production frontend build, and `git diff --check`.
