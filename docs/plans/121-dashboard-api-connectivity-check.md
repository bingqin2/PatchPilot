# 121 Dashboard API Connectivity Check

## Goal

Make dashboard startup failures diagnosable without terminal access by separating backend health, browser admin-token state, and protected API reachability.

## Scope

- Add a top-of-page connectivity panel.
- Load `/health` before the protected dashboard API batch so backend availability remains visible even when protected APIs return `Admin token is required`.
- Show backend status, browser token state, protected API state, and a single corrective next action.
- Keep the existing admin-token manager and alert recovery flow.
- Update frontend docs and execution logs.

## Verification

- App-level tests cover healthy connectivity.
- App-level tests cover a backend-up but admin-token-missing protected API failure.
- Full frontend tests and production build pass.
