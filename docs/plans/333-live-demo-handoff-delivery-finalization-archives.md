# Live Demo Handoff Delivery Finalization Archives

## Goal

Freeze a `READY` live demo handoff delivery finalization into an immutable local archive. This gives the operator a stable reviewer-facing proof bundle after the handoff package and delivery receipt have both been confirmed fresh.

## Scope

- Add an admin-protected backend endpoint at `POST /api/demo/live-demo-handoff-package/delivery-finalization/archives`.
- Add an admin-protected list endpoint at `GET /api/demo/live-demo-handoff-package/delivery-finalization/archives`.
- Add an admin-protected Markdown download endpoint at `GET /api/demo/live-demo-handoff-package/delivery-finalization/archives/{archiveId}/report/download`.
- Reject archive creation unless the current handoff delivery finalization is `READY` and finalized.
- Store only the latest twenty in-memory archives, newest first.
- Surface archive creation, archive history, archive download, and archive error feedback in the live launch gate dashboard.
- Keep archive creation local-only: no GitHub writes, no model calls, no task creation, no test execution, and no repository mutation.

## Backend Contract

An archive freezes the finalization status, delivery receipt id, evidence bundle archive id, repository and issue metadata, task and PR metadata, receipt freshness, checks, evidence notes, download actions, side-effect contract, finalization generation time, archive time, and Markdown report.

The archive report includes a top-level archive summary and the frozen finalization report so a reviewer can inspect both the archive metadata and the exact final readiness proof.

## Frontend Contract

The live launch gate panel shows:

- `Archive live demo handoff delivery finalization`
- recent live demo handoff delivery finalization archives
- per-archive Markdown download actions
- a dedicated archive error banner

The archive button is enabled only after the current finalization is finalized.

## Verification

- Focused backend tests: `DemoLiveDemoHandoffDeliveryFinalizationArchiveServiceTests`, `DemoLiveDemoHandoffDeliveryFinalizationControllerTests`, `DemoLiveDemoHandoffPackageControllerTests`, `DemoLiveDemoHandoffDeliveryReceiptControllerTests`
- Focused frontend tests: `src/api.test.ts`, `src/dashboard/components/LiveLaunchGatePanel.test.tsx`
- App integration test: `src/App.test.tsx`
- Full backend tests: `mvn -pl PatchPilot test`
- Full frontend tests and build: `npm --prefix frontend test -- --reporter=dot`, `npm --prefix frontend run build`
