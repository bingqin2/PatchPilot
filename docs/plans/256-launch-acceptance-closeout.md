# Plan 256: Launch Acceptance Closeout

## Goal

Add one final launch acceptance closeout readout that combines pre-launch readiness, launch evidence sharing, delivery receipt, and finalization status into a copyable/downloadable operator report.

## Scope

- Add a backend `DemoLaunchAcceptanceCloseoutService` and read model that composes:
  - self-hosted launch readiness
  - launch evidence package
  - launch evidence share center
  - launch evidence finalization gate
- Add read-only endpoints:
  - `GET /api/demo/launch-acceptance-closeout`
  - `GET /api/demo/launch-acceptance-closeout/report/download`
- The closeout is `READY` only when launch readiness is ready, launch evidence is share-ready, the share center is share-ready, and launch finalization is finalized.
- Include key evidence identifiers: session id, task id, Pull Request URL, webhook delivery id, evaluation run id, archive id, delivery receipt id, delivery target/channel/time, receipt freshness, and generated time.
- Render a dashboard closeout section inside the existing launch evidence package panel with status, final accepted/not-accepted state, evidence identifiers, next action, copy report, and download report.
- Update product docs, README, frontend design docs, and execution log.

## API Contract

`GET /api/demo/launch-acceptance-closeout` returns:

- `status`
- `accepted`
- `summary`
- `nextAction`
- `sessionId`
- `latestTaskId`
- `latestPullRequestUrl`
- `latestWebhookDeliveryId`
- `evaluationRunId`
- `latestArchiveId`
- `latestDeliveryReceiptId`
- `latestDeliveryTarget`
- `latestDeliveryChannel`
- `latestDeliveredAt`
- `deliveryReceiptFreshness`
- `generatedAt`
- `checks`
- `evidenceNotes`
- `downloadActions`
- `markdownReport`

The endpoint is read-only. It must not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.

## Validation

- Backend tests first fail because no closeout service or controller endpoint exists, then pass after implementation.
- Frontend tests first fail because no closeout API type/helper or launch panel section exists, then pass after UI wiring.
- Run targeted backend/frontend tests, full backend tests, full frontend tests, production build, and `git diff --check` before merge.
