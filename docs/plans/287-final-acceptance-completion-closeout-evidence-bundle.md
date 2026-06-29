# 287 Final Acceptance Completion Closeout Evidence Bundle

## Goal

Make the final acceptance completion closeout visible from the first demo evidence readout and copied runbook, so operators do not need to open the final acceptance panel to prove the external-review completion loop is closed.

## Scope

- Add final acceptance completion closeout evidence to `DemoEvidenceBundleVo`.
- Wire `DemoEvidenceBundleService` to read `DemoFinalAcceptanceCompletionCloseoutService`.
- Include closeout status in aggregate bundle readiness and next actions.
- Export closeout proof in copied demo runbook Markdown.
- Render a final acceptance completion closeout card in `DemoEvidenceBundlePanel`.
- Keep legacy frontend responses usable with a safe missing-closeout fallback.
- Update README, product spec, frontend design notes, and execution log.

## Behavior

- `READY`: the evidence bundle remains ready only when the final acceptance completion closeout is ready and closed.
- `NEEDS_ATTENTION`: missing or stale closeout proof downgrades the top-level bundle and adds the closeout next action.
- `BLOCKED`: blocked closeout proof blocks the top-level bundle.
- The evidence bundle remains read-only; it does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.

## Implementation Notes

- Backend service wiring: `DemoEvidenceBundleService`.
- Backend read model: `DemoEvidenceBundleVo.finalAcceptanceCompletionCloseoutEvidence`.
- Runbook export: `DemoRunbookService`.
- Frontend type and panel: `frontend/src/types.ts` and `DemoEvidenceBundlePanel`.
- No database migration is required; the bundle reuses the existing closeout read model.

## Validation

- Backend focused tests for aggregate readiness, missing-closeout guidance, and runbook Markdown.
- Frontend focused tests for the top-level closeout card and missing-field fallback.
- Full backend tests, full frontend tests, production build, and `git diff --check`.
