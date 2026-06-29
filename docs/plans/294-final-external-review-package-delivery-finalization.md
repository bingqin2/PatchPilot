# 294 Final External Review Package Delivery Finalization

## Goal

Close the last local evidence loop for the self-hosted demo: after a READY final external-review evidence package is frozen and a delivery receipt is recorded, PatchPilot should answer whether that receipt actually finalizes the current frozen package.

## Scope

- Add a read-only backend finalization model for the latest final external-review evidence package archive and latest package delivery receipt.
- Return `READY` only when the latest archive is READY and the latest delivery receipt matches that archive and its key evidence ids.
- Return `NEEDS_ATTENTION` for missing or stale receipts, and `BLOCKED` when the latest archive is missing or not ready.
- Expose JSON and Markdown download endpoints.
- Repeat the finalization in the top-level evidence bundle and copied runbook.
- Show the finalization status, checks, evidence notes, and download action in the dashboard.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run tests, mutate Git, push branches, create Pull Requests, or write GitHub comments.
- Do not replace existing archive or receipt write paths.

## Verification

- Backend service and controller tests cover READY, missing, stale, and blocked states.
- Frontend API and dashboard tests cover loading, rendering, and downloading the finalization report.
- Run full backend and frontend test suites before merge.
