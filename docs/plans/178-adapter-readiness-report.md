# 178 Adapter Readiness Report

## Goal

Make multi-language adapter readiness visible as one operator-facing report. Operators should be able to confirm supported languages, fixed verification commands, and fixture drift before a live `/agent fix` run without comparing separate dashboard panels or raw API responses.

## Scope

- Add a dashboard report panel derived from `GET /api/language-adapters` and `GET /api/language-adapters/fixtures`.
- Summarize adapter count, language coverage, fixture pass rate, and fixture failures.
- List allowlisted verification commands for each language/build-system adapter.
- Provide a copyable Markdown report for demo handoff or pull-request review context.
- Preserve the existing supported-adapter matrix and fixture verification matrix for detailed inspection.

## Frontend Design

- `AdapterReadinessReportPanel` renders a compact summary above the detailed adapter panels.
- The report shows `Ready` only when all fixture verifications pass and the adapter APIs loaded successfully.
- If either adapter API fails, the panel keeps any stale loaded data visible and marks the report as needing attention.
- The copied Markdown includes status, adapter count, language list, fixture pass rate, allowlisted commands, and fixture failures.

## Validation

- Component tests cover ready/attention summaries, fixture-failure rendering, API-error rendering, and copied Markdown.
- App integration tests cover dashboard wiring from the existing adapter APIs to the report panel.
- Full frontend tests and production build should pass.
- Backend tests should continue to pass because this feature reuses existing read-only APIs.
