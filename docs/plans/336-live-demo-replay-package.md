# 336 Live Demo Replay Package

## Goal

Create a read-only reviewer walkthrough package for a completed live demo. The package should convert the final artifact chain into a concise replay guide with links, replay steps, download actions, and an explicit side-effect contract.

## Scope

- Add a backend `DemoLiveDemoReplayPackageService` that derives replay readiness from the live demo artifact chain report.
- Add a typed replay package VO with sections, evidence links, replay steps, download actions, generated time, and Markdown report.
- Add admin-protected endpoints:
  - `GET /api/demo/live-demo-handoff-package/replay-package`
  - `GET /api/demo/live-demo-handoff-package/replay-package/download`
- Extend the dashboard live launch gate with replay package refresh/download controls, error feedback, and a replay package panel.
- Refresh the replay package after completion certificate archival and clear stale replay state when upstream artifacts change.

## Status Model

- `READY`: artifact chain is complete and consistent, issue and Pull Request evidence are present, and a completion certificate archive exists.
- `BLOCKED`: artifact chain is missing required archives.
- `NEEDS_ATTENTION`: artifact chain exists but has inconsistent or non-ready artifact state.

## Safety Contract

The replay package endpoint is read-only. It must not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub. It only reformats local artifact-chain evidence for reviewer walkthrough.

## Validation

- Backend service tests cover ready and blocked replay packages.
- Controller tests cover admin-protected JSON and Markdown download endpoints.
- Frontend API and live launch gate panel tests cover loading, rendering, downloading, and error feedback.
- App-level tests verify dashboard wiring still loads with the expanded API batch.
- Full backend/frontend test suites and build must pass before merge.
