# 176 Admin Audit Event Trail

## Goal

Make protected operator mutations visible in one audit stream. Admin-token guarded actions should answer who triggered the change, what resource changed, when it happened, and why, without reading backend logs.

## Scope

- Reuse the existing `operator_safety_audit` storage as the durable admin event table.
- Keep `GET /api/operator-safety-audits` for compatibility and add `GET /api/admin-audit-events` as the clearer read endpoint.
- Record audit events after successful manual task creation, task cancel, task retry, risk-review approval, rejected-trigger retry, trigger quarantine create/release, and demo session archive.
- Add a dashboard `Admin audit trail` panel that shows the unified stream.
- Keep rejected-trigger quarantine evidence and the rejected-trigger panel focused on quarantine-related operator actions.

## Backend Design

- `OperatorSafetyAuditService` remains the write/read service for audit rows.
- `OperatorSafetyAuditController` exposes both `/api/operator-safety-audits` and `/api/admin-audit-events`.
- `TaskController` records `MANUAL_TASK_CREATED`, `TASK_CANCELLED`, `TASK_RETRIED`, and `RISK_REVIEW_APPROVED` after the underlying service call succeeds.
- `RejectedTriggerAuditController` records `REJECTED_TRIGGER_RETRIED` after a safe retry creates a manual task.
- `DemoReadinessController` records `DEMO_SESSION_ARCHIVED` after an archive is stored.
- Failed validation, missing resources, and conflict responses do not write audit rows.

## Frontend Design

- `frontend/src/api.ts` adds `listAdminAuditEvents`.
- `App.tsx` loads admin audit events during refresh, renders the full stream in `AdminAuditPanel`, and filters quarantine audit rows for the existing rejected-trigger panel.
- `AdminAuditPanel` shows action, resource, repository/local scope, operator, time, and reason.

## Validation

- Backend controller tests cover the alias endpoint and successful mutation audit writes.
- Frontend API, component, and App tests cover loading and rendering admin audit events.
- Full backend and frontend regression runs should pass before merge.
