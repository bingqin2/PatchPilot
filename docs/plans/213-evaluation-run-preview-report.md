# 213 - Evaluation Run Preview Report

## Goal

Expose a read-only evaluation run preview so operators can copy a benchmark-shaped report before automated evaluation execution exists.

## Why This Matters

The catalog and readiness summary prove intended coverage, but they do not yet package that coverage like an evaluation run. A preview report bridges the gap between static fixture metadata and future stored benchmark runs. It gives demo and review evidence now while keeping the product honest that no cases were executed.

## Scope

- Add a backend preview record with status, preview run id, case counts, covered languages, covered build systems, expected verification commands, safety rejection categories, known gaps, next action, read-only flag, side-effect contract, and Markdown report.
- Add `GET /api/evaluation/run-preview`.
- Derive the preview from the existing checked-in evaluation catalog only.
- Add frontend API types and helper for the preview endpoint.
- Update `EvaluationCaseCatalogPanel` to render the preview report, known gaps, side-effect contract, and a `Copy evaluation run preview` action.
- Wire the dashboard to load the preview with the existing evaluation case and summary APIs.
- Keep the feature read-only: no task creation, model calls, repository cloning, verification command execution, Git mutation, or GitHub writes.
- Update README, product docs, frontend design, and execution log.

## Out of Scope

- Running evaluation cases.
- Persisting evaluation run records.
- Comparing model, prompt version, adapter, or code revision performance.
- Verifying repository fixtures during preview generation.
- Adding new evaluation cases or language adapters.

## Validation

- RED backend tests should fail because the preview service method and endpoint do not exist.
- RED frontend tests should fail because the preview API helper, type, dashboard loading, and copy action do not exist.
- Focused backend tests should verify the preview JSON shape, side-effect contract, expected commands, known gaps, and Markdown report.
- Focused frontend tests should verify preview API loading, panel rendering, copied Markdown, error handling, and App-level loading.
- Full backend and frontend verification should pass before handoff.
