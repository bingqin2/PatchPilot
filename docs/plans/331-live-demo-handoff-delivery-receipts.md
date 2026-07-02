# Live Demo Handoff Delivery Receipts

## Goal

Record local proof that the final live demo handoff package was delivered to a reviewer. This completes the reviewer handoff loop after the package is generated and downloaded.

## Scope

- Add an admin-protected backend endpoint at `POST /api/demo/live-demo-handoff-package/delivery-receipts`.
- Add an admin-protected listing endpoint at `GET /api/demo/live-demo-handoff-package/delivery-receipts`.
- Add an admin-protected Markdown download endpoint at `GET /api/demo/live-demo-handoff-package/delivery-receipts/{receiptId}/report/download`.
- Surface receipt creation, recent receipts, and receipt report downloads in the live launch gate dashboard.
- Keep the receipt local-only: no messages are sent, no GitHub state is changed, no tasks are created, no model calls run, and no Git/repository mutation occurs.

## Backend Contract

A receipt can be recorded only when the current live demo handoff package is `READY`, marked ready for review, and linked to an evidence bundle archive. The receipt captures delivery channel, target, operator, optional notes, delivery time, package evidence, task and Pull Request metadata, created time, and a Markdown report.

Invalid or incomplete delivery inputs fail fast with field-specific validation errors. Non-ready handoff packages fail before a receipt is persisted.

## Frontend Contract

The live launch gate panel shows:

- delivery channel, target, operator, and notes inputs
- `Record live demo handoff delivery receipt`
- recent delivery receipts with status, channel, target, and delivery time
- per-receipt Markdown downloads
- a dedicated delivery receipt error banner

## Verification

- Focused backend tests: `DemoLiveDemoHandoffDeliveryReceiptServiceTests`, `DemoLiveDemoHandoffDeliveryReceiptControllerTests`, `DemoLiveDemoHandoffPackageControllerTests`
- Focused frontend tests: `src/api.test.ts`, `src/dashboard/components/LiveLaunchGatePanel.test.tsx`
- App integration test: `src/App.test.tsx`
- Full backend tests: `mvn -pl PatchPilot test`
- Full frontend tests and build: `npm --prefix frontend test -- --reporter=dot`, `npm --prefix frontend run build`
