# 149 Repository Preflight Scope Readiness

## Goal

Surface repository-preflight scope readiness before an operator runs a local preflight. The previous scope policy protects the API, but operators still need clear setup feedback when demo fixture paths are not covered by configured roots.

## Backend Scope

- Add a `Repository preflight scope` check to `GET /api/demo/readiness`.
- Mark the check ready when normalized allowed roots include `docs/demo-repositories` or a parent project root that covers it.
- Mark the check as needs attention when roots are empty or demo fixture paths are outside the configured scope.
- Include `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS` in the remediation action.

## Frontend Scope

- Add `Repository preflight scope` to the operator setup checklist.
- Show the configured allowed roots directly in the repository preflight panel.
- Preserve the existing preflight form and result behavior.

## Validation

- Backend readiness tests cover ready and misconfigured scope states.
- Frontend component tests cover setup checklist readiness and preflight-panel root visibility.
- App-level tests verify dashboard wiring.
- Full backend and frontend verification should run before merge.
