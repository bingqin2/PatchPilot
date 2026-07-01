# 318 External Exposure Session Tracker

## Goal

Record and close temporary public URL exposure sessions after the external exposure handoff package is ready, so operators have a durable local audit trail for Cloudflare Tunnel or similar public URL sharing.

## Scope

- Add a backend session tracker under `/api/security/external-exposure-sessions`.
- Require the current external exposure handoff package to be `READY` before a session can start.
- Record URL, webhook URL, purpose, operator, expected shutdown time, linked readiness archive, and linked handoff status.
- Close an active session with operator and notes, preserving shutdown evidence.
- List recent sessions and download per-session Markdown reports.
- Add default in-memory storage plus MySQL/Flyway/MyBatis storage for `local`, `docker`, and `idea` profiles.
- Extend the operations dashboard exposure panel with session start/close controls, active session evidence, history, and report downloads.

## Non-Goals

- Do not probe the public URL over the network.
- Do not mutate GitHub webhook settings.
- Do not create tasks, call the model, run tests, mutate Git, open Pull Requests, or write GitHub comments.

## Backend API

- `POST /api/security/external-exposure-sessions`
  - Body: `publicUrl`, `webhookUrl`, `purpose`, `operator`, optional `expectedShutdownAt`, optional `notes`.
  - Returns the created session.
- `POST /api/security/external-exposure-sessions/{sessionId}/close`
  - Body: `closedBy`, optional `closedAt`, optional `closeNotes`.
  - Returns the closed session.
- `GET /api/security/external-exposure-sessions`
  - Returns recent sessions, newest first.
- `GET /api/security/external-exposure-sessions/{sessionId}/report/download`
  - Downloads Markdown evidence for one session.

## Acceptance Criteria

- Starting a session fails when the handoff package is not ready.
- Starting a session validates required fields and stores local-only evidence.
- Closing a session changes status from `ACTIVE` to `CLOSED` and appends closure evidence.
- Listing returns the newest sessions first and limits stored in-memory records.
- Dashboard can start, close, list, and download session reports.
- README and product/frontend/architecture docs describe how this fits temporary URL operation.

## Validation

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureSessionServiceTests,ExternalExposureSessionControllerTests,ExternalExposureSessionConvertTests,ExternalExposureSessionMigrationTests test`
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`
- `mvn -q -pl PatchPilot test`
- `npm --prefix frontend test -- --reporter=dot`
- `npm --prefix frontend run build`
- `git diff --check`
