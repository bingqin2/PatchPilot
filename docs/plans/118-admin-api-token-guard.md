# 118 Admin API Token Guard

## Goal

Protect PatchPilot operator APIs when the backend is exposed through a temporary Cloudflare URL, while keeping GitHub webhook delivery and health checks usable without the operator token.

## Scope

- Add optional `PATCHPILOT_ADMIN_TOKEN` configuration.
- When configured, require `X-PatchPilot-Admin-Token` or `Authorization: Bearer <token>` for `/api/**` operator endpoints.
- Keep `/health`, actuator health, and `/api/github/webhook` outside the admin-token guard.
- Report whether the admin token is configured in configuration summary and demo readiness.
- Let the frontend send a locally stored admin token without changing existing request shapes when no token is stored.
- Document curl and dashboard usage for temporary URL deployments.

## Verification

- Backend security filter tests cover missing, header, bearer, and public health behavior.
- Configuration and readiness tests cover the new configuration signal.
- Frontend API tests cover localStorage-backed header injection.
- Full backend and frontend test/build commands run before handoff.
