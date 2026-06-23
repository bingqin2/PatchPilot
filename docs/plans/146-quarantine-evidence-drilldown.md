# 146 Quarantine Evidence Drilldown

## Goal

Make each trigger quarantine explainable from the dashboard. Operators should be able to inspect one quarantine and see the rejected `/agent fix` attempts and manual safety actions that justify or changed it.

## Backend Scope

- Add `TriggerQuarantineEvidenceVo` as a read model containing:
  - the quarantine record,
  - matching rejected-trigger audit rows,
  - matching operator safety audit rows.
- Add `TriggerQuarantineEvidenceService` to compose evidence from existing quarantine, rejected-trigger, and operator-audit services.
- Add `GET /api/trigger-quarantines/{id}/evidence?limit=20`.
- Return `404` when the quarantine id is unknown and `400` for invalid limits.
- Add targeted query methods for:
  - quarantine lookup by id,
  - rejected-trigger audits by trigger-user or repository quarantine target,
  - operator safety audits by resource type/id.

## Frontend Scope

- Add `getTriggerQuarantineEvidence` and `TriggerQuarantineEvidence` typing.
- Add an `Inspect evidence` action to active quarantine rows.
- Render a compact detail section with matching rejected-trigger evidence and operator actions.
- Keep evidence loading user-initiated so the dashboard refresh path does not fan out into per-quarantine requests.

## Validation

- Backend service/controller tests cover evidence aggregation, not-found behavior, in-memory queries, and MyBatis query wrappers.
- Frontend API, component, and App tests cover the new endpoint, Inspect action, and rendered evidence detail.
