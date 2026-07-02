# Live Demo Handoff Delivery Finalization

## Goal

Provide a final read-only gate that proves the latest live demo handoff delivery receipt matches the current live demo handoff package. This lets an operator confirm that the reviewer handoff is complete before calling the demo finished.

## Scope

- Add an admin-protected backend endpoint at `GET /api/demo/live-demo-handoff-package/delivery-finalization`.
- Add an admin-protected Markdown download endpoint at `GET /api/demo/live-demo-handoff-package/delivery-finalization/report/download`.
- Validate that the current handoff package is `READY`, ready for review, linked to an evidence bundle archive, and matched by the newest delivery receipt.
- Surface finalization refresh, status, checks, evidence notes, next action, and report download in the live launch gate dashboard.
- Keep the finalization endpoint read-only: no messages are sent, no GitHub state is changed, no tasks are created, no model calls run, no test commands run, and no Git/repository mutation occurs.

## Backend Contract

The finalization status is `READY` only when the current handoff package is ready and the latest delivery receipt matches the package evidence, repository, issue, task, task status, and Pull Request URL. It returns `NEEDS_ATTENTION` when the receipt is missing or stale, and `BLOCKED` when the handoff package itself is not ready.

The response includes freshness classification, checks, evidence notes, download actions, a side-effect contract, and a Markdown report suitable for reviewer handoff evidence.

## Frontend Contract

The live launch gate panel shows:

- `Refresh live demo handoff delivery finalization`
- `Download live demo handoff delivery finalization`
- finalization status and delivery receipt freshness
- matched package and receipt metadata
- finalization checks, evidence notes, downloads, and next action
- a dedicated finalization error banner

Recording a new delivery receipt refreshes the finalization result so the operator can immediately see whether the handoff is complete.

## Verification

- Focused backend tests: `DemoLiveDemoHandoffDeliveryFinalizationServiceTests`, `DemoLiveDemoHandoffDeliveryFinalizationControllerTests`, `DemoLiveDemoHandoffPackageControllerTests`, `DemoLiveDemoHandoffDeliveryReceiptControllerTests`
- Focused frontend tests: `src/api.test.ts`, `src/dashboard/components/LiveLaunchGatePanel.test.tsx`
- App integration test: `src/App.test.tsx`
- Full backend tests: `mvn -pl PatchPilot test`
- Full frontend tests and build: `npm --prefix frontend test -- --reporter=dot`, `npm --prefix frontend run build`
