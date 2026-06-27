# 231 - Structured Demo Handoff Readiness

## Goal

Expose demo handoff readiness as a structured backend read model and render it directly in the dashboard. The dashboard should not maintain a duplicate local readiness algorithm that can drift from Markdown handoff package generation.

## Scope

- Add public backend value objects for handoff readiness and individual readiness checks.
- Add `GET /api/demo/handoff-readiness` for no-context compatibility and `POST /api/demo/handoff-readiness` for dashboard browser context.
- Reuse `DemoSessionReportRequestDto` so prepared launch commands and archived launch outcomes are evaluated by the same backend logic used by session reports and handoff packages.
- Render the returned status, summary, and check list in `DemoSessionSnapshotPanel`.
- Keep copy, download, archive, and report endpoints read-only except for explicit archive creation.

## Non-Goals

- Do not persist browser-local prepared command history on the backend.
- Do not change the handoff readiness rules beyond making the existing backend rules structured.
- Do not create or mutate GitHub state from readiness preview requests.

## Validation

- Backend service and controller tests should cover ready and blocked structured readiness.
- Frontend API and dashboard tests should confirm the panel consumes backend readiness and shows check-level evidence.
- Full backend and frontend regression tests should pass before merge.
