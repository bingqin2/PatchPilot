# 212 - Evaluation Case Readiness Summary

## Goal

Expose a read-only evaluation readiness summary so operators can see whether the current evaluation catalog has enough language and safety coverage for demo or review evidence.

## Why This Matters

The evaluation case catalog lists benchmark-ready scenarios, but operators still have to infer whether coverage is ready. A summary turns the catalog into a quick evidence gate: how many cases exist, which languages and build systems are covered, how many supported fixes and safety rejections exist, and what action is needed next.

## Scope

- Add backend summary record with readiness status, counts, covered languages, covered build systems, rejection categories, and next action.
- Add `GET /api/evaluation/summary`.
- Derive the summary from the same in-memory catalog used by `GET /api/evaluation/cases`.
- Add frontend API types and helper for the summary endpoint.
- Update `EvaluationCaseCatalogPanel` to render the backend summary and include it in copied Markdown.
- Keep the feature read-only: no task creation, model calls, test execution, Git mutation, or GitHub writes.
- Update README, product docs, frontend design, and execution log.

## Out of Scope

- Running evaluation cases.
- Persisting evaluation run records.
- Comparing model or prompt performance.
- Adding new case fixtures or language adapters.

## Validation

- RED backend tests should fail because the summary record and endpoint do not exist.
- RED frontend tests should fail because the summary API helper, type, and panel summary prop do not exist.
- Focused backend tests should verify summary counts, coverage lists, status, and endpoint JSON shape.
- Focused frontend tests should verify summary rendering, copied Markdown, stale-summary error handling, and App-level loading.
- Full backend and frontend verification should pass before handoff.
