# 148 Repository Preflight Scope Policy

## Goal

Limit the local repository preflight diagnostic to configured backend-local roots. This keeps the operator-only diagnostic useful for demo fixtures and prepared workspaces without turning it into a broad filesystem inspection endpoint.

## Backend Scope

- Add `patchpilot.repository-preflight.allowed-root-dirs`, backed by `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS`.
- Default to the project working directory and `docs/demo-repositories`.
- Normalize relative roots from the backend working directory, with the same module-friendly parent fallback used by repository path resolution.
- Reject `POST /api/repository-preflight` requests whose resolved path is outside every allowed root.
- Expose normalized allowed roots through `GET /api/configuration/summary`.

## Frontend Scope

- Add repository-preflight allowed roots to the configuration summary type.
- Show the configured roots in the dashboard configuration panel.
- Warn operators when no repository-preflight roots are configured.

## Validation

- Backend service and controller tests cover allowed paths and rejected outside paths.
- Configuration-summary tests verify normalized roots are returned without exposing secrets.
- Frontend tests verify configuration rendering and advisory behavior.
- Full backend and frontend verification should run before merge.
