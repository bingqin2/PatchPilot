# 315 External Exposure Readiness Gate

## Goal

Build one operator-facing security gate that answers whether a local PatchPilot instance is safe enough to expose through a temporary public URL such as Cloudflare Tunnel.

## Scope

- Add a read-only backend service and endpoint at `GET /api/security/external-exposure-readiness`.
- Reuse `ConfigurationSummaryService` and `AdminApiSecurityProperties` instead of adding new durable state.
- Check public-exposure safeguards:
  - Admin token is configured.
  - Dashboard admin token bootstrap is disabled.
  - GitHub webhook secret is configured.
  - Public webhook base URL is configured.
  - Trigger user and repository allowlists are configured.
  - Trigger rate limiting is enabled.
  - Rejected-trigger quarantine is enabled.
  - Review approval allowlist is configured.
  - Generated diff risk gate is enabled.
- Add typed frontend API support and a dashboard panel with copyable Markdown evidence.
- Update README, product spec, and execution log.

## Non-Goals

- Do not create tasks, call the model, run tests, probe the network, mutate Git, create branches, open Pull Requests, write GitHub comments, or archive records.
- Do not replace live launch gate, webhook setup readiness, or GitHub publish readiness.
- Do not expose secret values.

## Acceptance Criteria

- The API returns `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- Missing admin token, enabled dashboard token bootstrap, missing webhook secret, or missing public webhook URL blocks public exposure.
- Open trigger-user/repository allowlists, disabled rate limits, disabled quarantine, missing review approvers, or disabled generated-diff risk gate need attention.
- The response includes checks, counts, next actions, side-effect contract, generated timestamp, and Markdown report.
- The dashboard renders status, counts, checks, next actions, and a copy action.

## Verification

- Backend RED/GREEN tests:
  - `ExternalExposureReadinessServiceTests`
  - `ExternalExposureReadinessControllerTests`
- Frontend RED/GREEN tests:
  - `ExternalExposureReadinessPanel.test.tsx`
  - `api.test.ts`
  - `App.test.tsx`
- Final checks:
  - `mvn -q -pl PatchPilot test`
  - `npm --prefix frontend test -- --reporter=dot`
  - `npm --prefix frontend run build`
  - `git diff --check`
