# 290 Final External Review Evidence Package

## Goal

Provide one read-only, reviewer-facing evidence package that aggregates the accepted final demo summary, finalized reviewer package, completion evidence bundle, completion delivery finalization, final completion closeout, and the latest frozen closeout archive.

## Scope

- Add a backend final external-review evidence package VO and service.
- Add read-only API and Markdown download endpoints under `/api/demo/final-external-review-evidence-package`.
- Require all final acceptance and completion gates plus a `READY` closed frozen closeout archive before the package reports `READY`.
- Render the package in the final demo acceptance dashboard panel with check, evidence note, download action, and side-effect contract visibility.
- Refresh the package after completion archive, completion delivery receipt, or completion closeout archive state changes.
- Update README, product spec, frontend design notes, and execution log.

## Behavior

- `READY`: final demo acceptance is accepted, the reviewer package and completion delivery are finalized, the completion bundle is share-ready, the completion closeout is closed, and the latest frozen closeout archive is `READY` and closed.
- `NEEDS_ATTENTION`: the final chain is otherwise valid but the frozen closeout archive is missing or not closed.
- `BLOCKED`: final demo acceptance, completion evidence, delivery finalization, closeout, or archive evidence is blocked.
- The endpoint and report are read-only; they do not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.

## Implementation Notes

- Backend service: `DemoFinalExternalReviewEvidencePackageService`.
- Backend read model: `DemoFinalExternalReviewEvidencePackageVo`.
- Controller endpoints: `GET /api/demo/final-external-review-evidence-package` and `/report/download`.
- Frontend API/type wiring: `frontend/src/api.ts` and `frontend/src/types.ts`.
- Dashboard rendering: `DemoAcceptanceSummaryPanel`, loaded and refreshed by `App.tsx`.
- No database migration is required; this package reads existing final acceptance, completion, delivery, closeout, and closeout archive evidence.

## Validation

- Backend focused tests for READY, missing archive, blocked acceptance, not-closed archive, REST serialization, and Markdown download.
- Frontend focused tests for API helpers, final acceptance panel rendering/downloads, and full App data loading.
- Full backend tests, full frontend tests, production build, and `git diff --check`.
