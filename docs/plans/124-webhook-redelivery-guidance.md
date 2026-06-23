# 124 Webhook Redelivery Guidance

## Goal

Turn webhook delivery diagnostics into actionable redelivery guidance so operators know when to fix configuration and click GitHub's `Redeliver`, and when redelivery would be wrong or duplicate work.

## Scope

- Add derived redelivery guidance to `WebhookDeliveryDiagnosticVo`.
- Mark invalid signatures, malformed requests, and internal failed processing as redelivery candidates after the underlying issue is fixed.
- Mark ignored, rejected, duplicate, active-task, and task-created outcomes as non-redelivery cases with a clearer next step.
- Render the guidance in the dashboard `WebhookDeliveryPanel`.
- Update README, frontend design notes, and execution logs.

## Non-Goals

- Do not call GitHub's webhook delivery API.
- Do not store raw payloads, signatures, or response bodies.
- Do not change webhook signature validation, trigger safety decisions, or duplicate-delivery idempotency.
- Do not create tasks from diagnostics directly.

## Verification

- Backend tests cover derived guidance serialization and status-specific redelivery decisions.
- Frontend tests cover rendering redelivery guidance for recent deliveries.
- Full backend and frontend checks pass before handoff.
