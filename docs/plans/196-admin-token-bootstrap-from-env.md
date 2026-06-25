# Admin Token Bootstrap From Env

## Goal

Let trusted localhost dashboard sessions load the configured admin API token from the backend `.env` without manual browser console setup, while keeping the behavior disabled by default for public or shared URLs.

## Scope

- Add an explicit backend flag: `PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED`.
- Expose `GET /api/dashboard/bootstrap` as a public bootstrap endpoint.
- Return the admin token only when both `PATCHPILOT_ADMIN_TOKEN` is configured and the bootstrap flag is `true`.
- Keep all existing operator APIs protected by `PATCHPILOT_ADMIN_TOKEN`.
- Let the React dashboard call bootstrap before protected dashboard API requests and store the token only when no browser token already exists.
- Document that this is localhost-only convenience and must stay disabled for Cloudflare Tunnel or public demo URLs.

## Safety Rules

- Default is disabled.
- The endpoint returns non-sensitive status and operator guidance when disabled.
- The endpoint is intentionally unauthenticated because it exists to bootstrap the dashboard before protected calls.
- Operators must not enable the flag on public temporary URLs.
- Existing saved browser tokens are not overwritten.

## Validation

- Backend service tests cover disabled and enabled bootstrap decisions.
- Admin security filter tests prove the bootstrap endpoint stays public while other operator APIs remain protected.
- Frontend API tests cover typed bootstrap loading.
- Dashboard integration tests prove bootstrap happens before protected API calls.
