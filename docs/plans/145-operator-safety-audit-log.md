# 145 Operator Safety Audit Log

## Goal

Make manual safety changes traceable. After operators can create and release trigger quarantines, PatchPilot must also record who changed safety state, what resource changed, when it happened, and why.

## Scope

- Add a durable `operator_safety_audit` table for safety mutations.
- Record audit rows when operators create or release trigger quarantines.
- Expose recent safety audit rows through `GET /api/operator-safety-audits`.
- Load and render recent safety audit rows in the dashboard's rejected-trigger panel.
- Keep the feature focused on safety operations; do not turn it into a general event bus.

## Backend Design

- `OperatorSafetyAuditService` owns audit recording and recent list queries.
- The default profile uses an in-memory implementation for lightweight tests and local no-database runs.
- `local`, `docker`, and `idea` profiles use MyBatis and Flyway-backed persistence.
- `TriggerQuarantineController` records `MANUAL_QUARANTINE_CREATED` and `TRIGGER_QUARANTINE_RELEASED` audit rows after successful mutations.

## Frontend Design

- `frontend/src/api.ts` adds `listOperatorSafetyAudits`.
- `App.tsx` loads the audit list with rejected-trigger and quarantine data during dashboard refresh.
- `RejectedTriggerPanel` displays a compact recent operator safety audit list above rejected-trigger rows.

## Validation

- Backend unit/controller/migration tests cover the new service, API, migration, and quarantine write integration.
- Frontend API, component, and App tests cover loading and rendering operator safety audits.
