# 211 - Evaluation Case Catalog

## Goal

Expose a read-only evaluation case catalog that shows which issue-to-PR scenarios PatchPilot can prove across supported languages and safety gates.

## Why This Matters

PatchPilot already has adapter fixtures, readiness checks, and demo handoff reports. The next maturity step is proving capability beyond one happy-path demo. A catalog of evaluation cases gives operators and reviewers a concrete benchmark map: supported language scenarios, expected verification commands, expected changed files, success criteria, and examples that must be rejected before execution.

## Scope

- Add backend evaluation case records with stable ids, categories, repository fixture paths, issue text, expected adapter metadata, expected verification commands, expected changed files, success criteria, and safety expectations.
- Add a read-only `GET /api/evaluation/cases` endpoint.
- Start with an in-memory catalog; do not add database tables in this slice.
- Cover Java/Maven, Node/npm, Python/pytest, Go, and rejected unsafe/vague trigger cases.
- Add frontend API types and a dashboard panel that summarizes case count, language coverage, safety rejection coverage, commands, expected files, and success criteria.
- Wire the panel into the dashboard without creating tasks, running model calls, mutating GitHub, or executing benchmark commands.
- Update README, product docs, AI infrastructure target, frontend design, and execution log.

## Out of Scope

- Running evaluation cases.
- Persisting evaluation runs.
- Comparing model, prompt, cost, or latency across runs.
- Creating Pull Requests from evaluation cases.
- Adding new language adapters.

## Validation

- RED backend tests should fail because the evaluation catalog service and endpoint do not exist.
- RED frontend tests should fail because the API helper and dashboard panel do not exist.
- Focused backend tests should cover catalog content and API JSON shape.
- Focused frontend tests should cover API parsing, panel rendering, copied Markdown reports, and App-level loading.
- Full frontend/backend regression verification should run before merge.
