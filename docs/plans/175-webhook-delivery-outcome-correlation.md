# Webhook Delivery Outcome Correlation

## Goal

Make each recorded GitHub webhook delivery explain what happened after the HTTP request reached PatchPilot: a task was created, a rejected-trigger audit row was recorded, the delivery was ignored, a duplicate was detected, or an error requires redelivery.

## Why This Matters

GitHub can show a webhook delivery as successful while PatchPilot intentionally ignores or rejects the comment. Operators need one dashboard row that connects the GitHub delivery id to the resulting task, rejection record, or next action instead of manually comparing task lists, rejected-trigger audit rows, and delivery diagnostics.

## Scope

- Add outcome fields to `WebhookDeliveryDiagnosticVo`.
- Persist outcome fields for MySQL-backed delivery diagnostics.
- Derive outcome fields when recording new delivery diagnostics.
- Link rejected webhook deliveries to the rejected-trigger audit id created during the same webhook handling path.
- Show delivery outcomes in the dashboard webhook delivery panel.
- Keep old records readable when outcome fields are null.

## Non-Goals

- Do not change webhook task creation, safety, rate-limit, or quarantine behavior.
- Do not replay webhook payloads.
- Do not create new task-triggering endpoints.
- Do not change rejected-trigger retry behavior.

## Verification

- Backend tests must prove task-created, rejected, ignored, and persisted delivery diagnostics expose the correct outcome.
- Frontend tests must prove the dashboard renders outcome type and target identifiers.
- Run focused backend/frontend tests, then full backend test, full frontend test, frontend build, and whitespace check before handoff.
