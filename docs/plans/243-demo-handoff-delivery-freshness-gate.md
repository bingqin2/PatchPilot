# 243 Demo Handoff Delivery Freshness Gate

## Goal

Prevent stale handoff share delivery receipts from being treated as proof that the current demo handoff package was delivered.

## Problem

Plan 242 surfaces the latest delivery receipt, but the newest receipt may belong to an older handoff archive or session after the operator archives a newer handoff package. The dashboard should distinguish current delivery evidence from historical delivery evidence.

## Scope

- Add delivery receipt freshness fields to the handoff share center and top-level demo evidence bundle.
- Classify the latest receipt as `MISSING`, `FRESH`, or `STALE`.
- Treat a receipt as fresh only when its archive id and session id match the current latest handoff package archive summary.
- Update share-center next actions, download actions, evidence notes, and Markdown report fields when a receipt is stale.
- Render receipt freshness in the demo evidence bundle and handoff share center panels.
- Keep the feature read-only over existing receipt and archive state; it must not send messages, create tasks, call the model, mutate Git, or write to GitHub.

## Out of Scope

- Changing receipt persistence schema or receipt creation API.
- Sending or validating external delivery.
- Blocking receipt creation for historical archive ids; stale detection is a read-model gate.

## Validation

- Backend RED: focused share-center and evidence-bundle tests fail because freshness fields and stale receipt behavior do not exist.
- Frontend RED: focused dashboard tests fail because receipt freshness is not rendered.
- GREEN: focused backend and frontend tests pass after implementing freshness calculation and display.
- Final verification: full backend tests, full frontend tests, frontend production build, and `git diff --check`.
