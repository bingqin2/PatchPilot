# Live Demo Completion Certificate

## Goal

Turn the latest `READY` live demo handoff delivery finalization archive into a terminal completion certificate. This gives the operator one final reviewer-facing artifact that says the live GitHub issue-to-PR demo is complete, delivered, archived, and ready to share.

## Scope

- Add a read-only backend endpoint at `GET /api/demo/live-demo-handoff-package/completion-certificate`.
- Add a read-only Markdown download endpoint at `GET /api/demo/live-demo-handoff-package/completion-certificate/report/download`.
- Add admin-protected archive endpoints to create, list, and download completion certificate archives.
- Certify completion only when the latest handoff finalization archive is `READY`, finalized, and backed by a fresh delivery receipt.
- Store only the latest twenty local completion certificate archives, newest first.
- Surface certificate refresh, report download, archive creation, archive history, per-archive downloads, and error feedback in the live launch gate dashboard.

## Backend Contract

The certificate summarizes the latest finalization archive id, delivery receipt id, evidence bundle archive id, repository, issue URL, task status, Pull Request URL, delivery target, receipt freshness, generation time, download actions, and side-effect contract.

The read-only certificate endpoint must not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub. Archive creation writes only local PatchPilot completion certificate archive records.

## Frontend Contract

The live launch gate panel shows:

- `Refresh live demo completion certificate`
- `Download live demo completion certificate`
- `Archive live demo completion certificate`
- the current certificate status and evidence links
- recent completion certificate archives with per-archive Markdown downloads
- dedicated refresh and archive error banners

The archive button is enabled only after the current certificate is certified.

## Verification

- Focused backend tests: `DemoLiveDemoCompletionCertificateServiceTests`, `DemoLiveDemoHandoffDeliveryFinalizationControllerTests`, `DemoLiveDemoHandoffPackageControllerTests`, `DemoLiveDemoHandoffDeliveryReceiptControllerTests`
- Focused frontend tests: `src/api.test.ts`, `src/dashboard/components/LiveLaunchGatePanel.test.tsx`
- App integration test: `src/App.test.tsx`
- Full backend tests: `mvn -pl PatchPilot test`
- Full frontend tests and build: `npm --prefix frontend test -- --reporter=dot`, `npm --prefix frontend run build`
