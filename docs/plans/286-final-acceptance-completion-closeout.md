# 286 Final Acceptance Completion Closeout

## Goal

Provide one read-only final closeout for the completed final acceptance evidence delivery loop so operators can prove the self-hosted issue-to-PR demo is externally reviewable without downloading several separate reports.

## Scope

- Add a backend closeout read model for final acceptance completion.
- Aggregate final acceptance summary, final acceptance share finalization, completion evidence bundle, completion archives, completion evidence delivery receipts, and completion delivery finalization.
- Report `READY`, `NEEDS_ATTENTION`, or `BLOCKED` with closed flag, checks, evidence notes, next action, and Markdown report.
- Add API and download endpoints under `/api/demo`.
- Render the closeout in the final demo acceptance dashboard panel.
- Update README, product spec, frontend design notes, and execution log.

## Behavior

- `READY`: final acceptance summary is accepted, share finalization is finalized, completion evidence bundle is share-ready, the latest completion delivery finalization is finalized, and at least one completion evidence delivery receipt exists.
- `NEEDS_ATTENTION`: prerequisites are mostly available but the completion delivery finalization is missing, stale, or not finalized.
- `BLOCKED`: the final acceptance summary is not accepted, share finalization is blocked, or the completion evidence bundle is not share-ready.
- The closeout API is read-only and does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.

## Implementation Notes

- Backend service: `DemoFinalAcceptanceCompletionCloseoutService`.
- Backend VO: `DemoFinalAcceptanceCompletionCloseoutVo` with nested `Check`.
- Controller endpoints:
  - `GET /api/demo/final-acceptance-completion-closeout`
  - `GET /api/demo/final-acceptance-completion-closeout/report/download`
- Frontend API/type helpers and final acceptance dashboard panel section.
- No database migration is required; the closeout is computed from existing read models and receipt history.

## Validation

- Backend focused tests for READY, missing delivery finalization, blocked acceptance summary, blocked bundle, REST serialization, and Markdown download.
- Frontend focused tests for API helper, panel rendering/download, and App fixture loading.
- Full backend tests, full frontend tests, production build, and `git diff --check`.
