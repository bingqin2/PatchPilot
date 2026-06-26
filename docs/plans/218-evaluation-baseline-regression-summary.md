# 218 - Evaluation Baseline Regression Summary

## Goal

Turn archived evaluation fixture baseline runs into a regression signal that operators can inspect before using the latest fixture run as demo evidence.

## Scope

- Add a read-only backend summary endpoint for recent archived fixture baseline runs.
- Compare the latest archive with the previous archive.
- Surface pass/fail/skip deltas, latest failed cases, newly failed cases, recovered cases, next action, side-effect contract, and a copyable Markdown report.
- Display the same summary in the dashboard evaluation panel.

## Backend

- `GET /api/evaluation/fixture-baseline-runs/summary`
- `EvaluationFixtureBaselineRunRegressionSummaryService`
- `EvaluationFixtureBaselineRunRegressionSummaryVo`
- `EvaluationFixtureBaselineRunDigestVo`

The summary reads only archived baseline runs. It does not execute fixture commands, create tasks, call the model, mutate Git, or write to GitHub. Failed case ids are extracted from the existing archived Markdown report format so older archived rows remain usable.

## Frontend

The dashboard loads the regression summary during refresh and after `Run and archive fixture baseline`. `EvaluationCaseCatalogPanel` shows the regression status, latest and previous runs, deltas, newly failed cases, recovered cases, next action, and a `Copy fixture baseline regression report` action.

## Validation

- Backend RED: focused tests first failed because the summary service and endpoint did not exist.
- Backend GREEN: focused Maven tests passed after implementing the service and controller endpoint.
- Frontend RED: focused Vitest tests first failed because the API helper and dashboard summary section did not exist.
- Frontend GREEN: focused Vitest tests passed after wiring API, App refresh, and the panel UI.
- Regression RED/GREEN: App-level Vitest coverage first showed that a post-archive summary refresh failure was also surfaced as a fixture-baseline failure; after splitting the error boundary, the archived baseline evidence stays visible and only the regression summary section reports the refresh error.
- Full backend: `mvn -pl PatchPilot test` passed, 758 tests run, 0 failures.
- Full frontend: `npm test -- --reporter=basic` passed, 26 test files and 265 tests run.
- Build: `npm run build` passed.
- Diff hygiene: `git diff --check` passed.
