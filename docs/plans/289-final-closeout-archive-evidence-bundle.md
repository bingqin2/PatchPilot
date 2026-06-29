# 289 Final Closeout Archive Evidence Bundle

## Goal

Promote the final acceptance completion closeout archive into the first demo evidence readout and copied runbook, so the externally reviewable demo baseline points at a frozen READY/closed closeout instead of only the live closeout view.

## Scope

- Add final acceptance completion closeout archive evidence to `DemoEvidenceBundleVo`.
- Wire `DemoEvidenceBundleService` to read recent `DemoFinalAcceptanceCompletionCloseoutArchiveRepository` records.
- Require the latest archive to be `READY` and closed before the top-level evidence bundle reports `READY`.
- Export archive id, linked completion archive, evidence delivery receipt, task, Pull Request, archived timestamp, next action, and download actions in copied runbook Markdown.
- Render a top-level dashboard evidence card with safe fallback behavior for older bundle responses.
- Update README, product spec, frontend design notes, and execution log.

## Behavior

- `READY`: the latest final acceptance completion closeout archive is `READY` and closed.
- `NEEDS_ATTENTION`: no archive exists, the latest archive is not closed, or the latest archive needs attention.
- `BLOCKED`: the latest archive is blocked, which blocks the overall evidence bundle.
- The evidence bundle and copied runbook remain read-only; they do not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.

## Implementation Notes

- Backend aggregation: `DemoEvidenceBundleService`.
- Backend read model: `DemoEvidenceBundleVo.finalAcceptanceCompletionCloseoutArchiveEvidence`.
- Archive evidence VO: `DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo`.
- Runbook export: `DemoRunbookService`.
- Frontend type and card: `frontend/src/types.ts` and `DemoEvidenceBundlePanel`.
- No database migration is required; the bundle reads the archive records introduced in plan 288.

## Validation

- Backend focused tests for aggregate readiness, missing-archive guidance, and runbook Markdown.
- Frontend focused tests for the top-level archive card and legacy missing-field fallback.
- Full backend tests, full frontend tests, production build, and `git diff --check`.
