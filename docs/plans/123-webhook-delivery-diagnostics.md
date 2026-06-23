# 123 Webhook Delivery Diagnostics

## Goal

Make recent GitHub webhook deliveries inspectable from PatchPilot itself so operators can diagnose temporary URL, signature, ignored-event, rejection, duplicate, and task-created outcomes without relying only on GitHub's delivery page.

## Scope

- Add a webhook delivery diagnostic read model with in-memory and MyBatis implementations.
- Record delivery id, event, handling status, task id, repository, issue, trigger user/comment, message, and timestamp for webhook service outcomes.
- Expose `GET /api/github/webhook-deliveries?limit=...` for the dashboard and curl debugging.
- Add a dashboard panel that shows recent deliveries and links them to task ids when available.
- Update README, frontend design notes, and execution logs.

## Non-Goals

- Do not call GitHub's deliveries API.
- Do not change webhook signature validation or task safety decisions.
- Do not store raw webhook payloads.
- Do not expose secrets or signatures.

## Verification

- Backend service, controller, migration, and webhook integration tests cover recorded deliveries.
- Frontend API and dashboard tests cover rendering recent delivery diagnostics.
- Full backend and frontend focused test suites pass.
