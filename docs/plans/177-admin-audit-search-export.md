# 177 Admin Audit Search Export

## Goal

Make the protected admin mutation stream searchable and exportable. Operators should be able to answer which protected action happened, who performed it, which task or safety resource changed, and copy a Markdown evidence report without parsing raw logs.

## Scope

- Extend `GET /api/admin-audit-events` and the compatibility alias `GET /api/operator-safety-audits` with filters for action, resource type, resource id, scope, scope key, operator, and limit.
- Keep existing recent-list behavior when no filters are supplied.
- Add dashboard controls for applying and clearing admin audit filters.
- Add a copyable Markdown admin audit report for the currently visible audit rows and filters.
- Keep rejected-trigger quarantine evidence sourced from the same audit stream.

## Backend Design

- Add `OperatorSafetyAuditQuery` as the normalized query model.
- Keep `OperatorSafetyAuditService.listSafetyAudits(int)` as a compatibility method and route new behavior through `listSafetyAudits(OperatorSafetyAuditQuery)`.
- `InMemoryOperatorSafetyAuditService` filters the in-memory audit list before sorting and limiting.
- `MyBatisOperatorSafetyAuditService` adds conditional equality predicates before ordering by `created_at DESC` and applying the validated limit.
- `OperatorSafetyAuditController` parses optional query parameters and returns `400 Bad Request` only for invalid limits or invalid enum values.

## Frontend Design

- `frontend/src/api.ts` accepts `AdminAuditFilterOptions` for `listAdminAuditEvents`.
- `App.tsx` owns the current admin audit filters and refreshes the dashboard with those filters.
- `AdminAuditPanel` renders compact filter fields for action, operator, resource type, resource id, and scope key.
- `Copy admin audit report` writes a Markdown report containing filters, event count, and visible event summaries.

## Validation

- Backend controller and service tests cover filtered query behavior for in-memory and MyBatis services.
- Frontend API tests cover filtered URL generation.
- Frontend component tests cover filter submission, clear behavior, and copied Markdown reports.
- App tests cover end-to-end dashboard wiring from filter input to backend query parameters.
