# Adapter Detection Explainability

## Goal

Persist and display the reason PatchPilot selected a language adapter for a task, so operators can explain why a repository used Maven, Gradle, Bun, npm, pnpm, yarn, tox, nox, hatch, Poetry, uv, or pytest verification.

This feature closes the gap between adapter fixture diagnostics and real task records: a completed or failed task should show not only the selected `language`, `buildSystem`, and `verificationCommand`, but also the detection signal that caused that selection.

## Scope

- Add `adapterDetectionReason` to task persistence and task API responses.
- Store the reason returned by `LanguageAdapterRegistry.detect(...)` when task execution records adapter metadata.
- Keep older task records valid with a nullable detection reason.
- Include the field in task detail responses through the existing task summary read model.
- Show the detection reason in the dashboard selected-task execution evidence strip.
- Keep list views compact by leaving the reason in detail only.
- Update README, architecture notes, frontend design notes, and execution log.

## Out of Scope

- Changing adapter ordering.
- Adding new language adapters.
- Making model calls to explain adapter selection.
- Recomputing detection reasons for historical task rows.

## Validation

- Backend service, converter, mapper migration, controller, and executor tests.
- Frontend API, task detail component, and dashboard integration tests.
- Full backend and frontend test suites.
- Production frontend build.
