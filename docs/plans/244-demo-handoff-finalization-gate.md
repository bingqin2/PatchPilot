# 244 Demo Handoff Finalization Gate

## Goal

Close the post-demo handoff loop with one final acceptance gate that proves the current handoff package was actually delivered and can be treated as complete handoff evidence.

## Problem

Plan 243 distinguishes missing, fresh, and stale delivery receipts, but operators still need a single readout that answers whether the whole post-demo handoff is finalized. The share center says whether a package can be sent; the finalization gate should say whether the package has been sent and accepted as current evidence.

## Scope

- Add a read-only finalization service and `GET /api/demo/handoff-finalization`.
- Add `GET /api/demo/handoff-finalization/report/download` for Markdown acceptance evidence.
- Derive finalization from the current handoff share center and require both share readiness and a fresh delivery receipt.
- Classify finalization as `READY`, `NEEDS_ATTENTION`, or `BLOCKED`, with check rows, evidence notes, and next action guidance.
- Surface finalization status, receipt freshness, and latest receipt id in the top-level demo evidence bundle.
- Render a handoff finalization panel in the demo session snapshot with a report download action.
- Refresh finalization after recording a handoff delivery receipt or archiving a new handoff package.

## Out of Scope

- Sending the handoff package externally.
- Editing historical delivery receipts.
- Auto-closing issues, auto-merging Pull Requests, or posting finalization evidence to GitHub.

## Validation

- Backend RED: focused finalization, controller, and evidence-bundle tests fail because the finalization read model and endpoints do not exist.
- Frontend RED: focused API/App tests fail because the dashboard cannot load, render, refresh, or download finalization evidence.
- GREEN: focused backend and frontend tests pass after implementing finalization aggregation and UI wiring.
- Final verification: full backend tests, full frontend tests, frontend production build, and `git diff --check`.
