# 120 Dashboard Admin Token Management

## Goal

Make admin-token state visible and controllable from the dashboard header so operators do not need browser DevTools to manage local credentials during temporary URL demos or protected local sessions.

## Scope

- Show whether the current browser has a saved dashboard admin token.
- Let operators save or replace the local token in `localStorage` under `patchpilot.adminToken`.
- Let operators clear the local token when rotating credentials or testing unauthenticated behavior.
- Refresh dashboard data after token save or clear so the current credential state is exercised immediately.
- Keep backend security unchanged and reuse the existing `X-PatchPilot-Admin-Token` frontend API helper.

## Verification

- Add App-level coverage for saved-token display, replacement, request header refresh, and token clearing.
- Run focused frontend coverage for the new token manager.
- Run the full frontend test suite.
- Run the production frontend build.
