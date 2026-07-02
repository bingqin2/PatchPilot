# 335 Live Demo Artifact Chain Report

## Goal

Create a read-only final traceability report for the live demo proof chain. The report should show whether the latest archived launch package, outcome closeout, evidence bundle, handoff finalization, and completion certificate form one complete and consistent end-to-end demo record.

## Scope

- Add a backend `DemoLiveDemoArtifactChainReportService` that reads the latest local archive records only.
- Add a typed report VO with artifact steps, consistency checks, evidence notes, download actions, side-effect contract, generated time, and Markdown report.
- Add admin-protected endpoints:
  - `GET /api/demo/live-demo-handoff-package/artifact-chain-report`
  - `GET /api/demo/live-demo-handoff-package/artifact-chain-report/download`
- Extend the dashboard live launch gate with refresh/download controls, error feedback, and an artifact chain report panel.
- Refresh the report after archiving a completion certificate and clear stale report state when upstream artifacts change.

## Status Model

- `READY`: all five artifact types exist, are ready/certified/finalized as appropriate, and their archive ids reference one consistent chain.
- `BLOCKED`: one or more required archives are missing.
- `NEEDS_ATTENTION`: all required archives exist but at least one archive is not ready or references a different artifact id.

## Safety Contract

The report endpoint is read-only. It must not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub. It only aggregates local archive state.

## Validation

- Backend service tests cover ready, missing, and inconsistent artifact chains.
- Controller tests cover admin-protected JSON and Markdown download endpoints.
- Frontend API and live launch gate panel tests cover loading, rendering, downloading, and error feedback.
- Full backend/frontend test suites and build must pass before merge.
