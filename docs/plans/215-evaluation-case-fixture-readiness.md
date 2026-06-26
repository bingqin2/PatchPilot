# 215 - Evaluation Case Fixture Readiness

## Goal

Verify that the read-only evaluation catalog is backed by real checked-in repository fixtures before operators use it as demo or benchmark evidence. This closes the gap between "the catalog says Java/Maven or Node/npm is covered" and "the fixture directories still exist, detect as the expected adapters, and contain the expected changed files."

## Scope

- Add a backend fixture-readiness service and `GET /api/evaluation/case-readiness` endpoint.
- Check supported-fix cases with the real `LanguageAdapterRegistry`.
- Mark safety-rejection cases as `NO_FIXTURE_REQUIRED` because they validate trigger gates rather than repository files.
- Return per-case readiness fields, aggregate counts, side-effect contract, next action, and a copyable Markdown report.
- Show fixture readiness in the dashboard evaluation panel.
- Align evaluation case fixtures with the current checked-in Java/Maven and Node/npm demo repositories.

## API Contract

`GET /api/evaluation/case-readiness` returns:

- Overall status: `READY` or `NEEDS_ATTENTION`.
- Case counts: total, passing, no-fixture-required, failing.
- Per-case fixture path, expected versus actual adapter metadata, expected changed files, missing files, reason, and next action.
- Markdown report for demo handoff.

The endpoint is read-only. It does not create tasks, call the model, run verification commands, mutate Git, or write to GitHub.

## Validation

- Backend focused tests for service, controller, and catalog fixture metadata.
- Frontend focused tests for API loading, dashboard rendering, stale-data error handling, and report copy.
- Full backend and frontend regression checks before merging.
