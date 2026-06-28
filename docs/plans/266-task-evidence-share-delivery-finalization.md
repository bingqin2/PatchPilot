# Plan 266: Task Evidence Share Delivery Finalization

## Goal

Turn task evidence share readiness into an auditable local delivery workflow. Operators should be able to record that a share-ready task evidence package was delivered outside PatchPilot, then download a finalization report that proves the current shareable archive has a fresh delivery receipt.

## Scope

- Add a persistent task evidence delivery receipt model with archive id, task id, repository, issue, Pull Request URL, delivery channel, target, operator, notes, delivered time, created time, and Markdown report.
- Add local delivery receipt APIs:
  - `POST /api/tasks/evidence-packages/share-delivery-receipts`
  - `GET /api/tasks/evidence-packages/share-delivery-receipts`
  - `GET /api/tasks/evidence-packages/share-delivery-receipts/{receiptId}/report/download`
- Add finalization APIs:
  - `GET /api/tasks/evidence-packages/finalization`
  - `GET /api/tasks/evidence-packages/finalization/report/download`
- Require a share-ready archive and a fresh receipt for the current archive/task before reporting `READY`.
- Extend the dashboard task evidence archive review panel with finalization status, checks, evidence notes, receipt form, recent receipts, and report downloads.

## Out of Scope

- Sending email, Slack, GitHub comments, or any external message.
- Creating tasks, calling the model, running tests, mutating Git, pushing branches, opening Pull Requests, or writing to GitHub from receipt/finalization endpoints.
- Replacing the demo launch/handoff receipt flows.

## Validation

- Backend RED/GREEN coverage for controller, service, converter, in-memory repository, MyBatis repository, and Flyway migration.
- Frontend RED/GREEN coverage for API helpers, dashboard component behavior, App refresh wiring, receipt creation, and report downloads.
- Full backend and frontend regression before merge.
