# 337 Live Demo Reviewer Delivery Center

## Goal

Add a single reviewer-facing delivery center for completed live demos. Operators should be able to open one dashboard section and know whether the demo can be sent to a reviewer, what is blocking delivery, and which final reports should be downloaded.

## Scope

- Add a backend `DemoLiveDemoReviewerDeliveryCenterService` that derives delivery readiness from:
  - live demo handoff package
  - live demo artifact chain report
  - live demo completion certificate
  - live demo replay package
- Add a typed delivery center VO with status, deliverable flag, summary, next action, readiness cards, blockers, evidence links, download actions, generated time, side-effect contract, and Markdown report.
- Add admin-protected endpoints:
  - `GET /api/demo/live-demo-handoff-package/reviewer-delivery-center`
  - `GET /api/demo/live-demo-handoff-package/reviewer-delivery-center/download`
- Extend the live launch gate dashboard with one delivery center panel, refresh/download controls, error feedback, and App-level loading.

## Status Model

- `READY`: handoff package is ready, artifact chain is complete, completion certificate is certified, and replay package is ready.
- `BLOCKED`: one or more required deliverables are blocked or missing.
- `NEEDS_ATTENTION`: required deliverables exist but are inconsistent or not ready for final reviewer handoff.

## Safety Contract

The delivery center is read-only. It must not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub. It only summarizes local PatchPilot evidence that already exists.

## Validation

- Backend service tests cover ready and blocked delivery center states.
- Controller tests cover JSON and Markdown download endpoints.
- Frontend API and live launch gate panel tests cover loading, rendering, downloading, blockers, and error feedback.
- App-level tests verify the dashboard can load the expanded API batch.
- Full backend/frontend tests, frontend build, diff check, and secret scans must pass before merge.
