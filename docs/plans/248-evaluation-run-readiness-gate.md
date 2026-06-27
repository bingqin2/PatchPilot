# 248 - Evaluation Run Readiness Gate

## Goal

Turn archived full evaluation runs into an operator-visible readiness gate. After `247-evaluation-run-execution-archive`, operators can run and archive full local evaluation evidence, but demo readiness still only consumes fixture baseline regression. This slice makes the latest full evaluation archive part of the demo decision.

## Scope

- Add a full evaluation run readiness summary API:
  - `GET /api/evaluation/runs/summary`
  - Reads recent archived full evaluation runs only.
  - Reports latest and previous run digests, pass/fail/skip deltas, coverage, status, next action, side-effect contract, and Markdown evidence.
- Add a demo readiness check named `Evaluation run archive`.
  - `NEEDS_ATTENTION` when no full evaluation run is archived.
  - `BLOCKED` when the latest archived full evaluation run needs attention or contains failed fixture cases.
  - `READY` when the latest archived full evaluation run is ready and contains safety rejection coverage.
- Update the dashboard evaluation catalog panel to show the full evaluation run readiness summary and refresh it after `Run evaluation`.
- Update docs and progress logs with the new gate and verification evidence.

## Non-Goals

- Do not call the model or run hosted benchmark tasks.
- Do not create PatchPilot tasks, mutate Git, push branches, open Pull Requests, or write to GitHub.
- Do not replace the existing fixture baseline regression gate; this gate complements it with combined fixture and safety coverage.

## Validation

- Add service, controller, frontend API, panel, and App integration tests using RED/GREEN.
- Run targeted backend and frontend tests.
- Run full backend tests, full frontend tests, production build, and `git diff --check` before merging.
