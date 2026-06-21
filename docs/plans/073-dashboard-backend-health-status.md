# 073 Dashboard Backend Health Status

## Goal

Show whether the dashboard can reach the PatchPilot backend health endpoint.

## Scope

- Add a frontend API helper for `GET /health`.
- Add a typed backend health response in the dashboard frontend.
- Load backend health during dashboard refresh.
- Display backend status, service name, and timestamp in the configuration panel.
- Show a clear unavailable state when no health response is loaded.

## Non-Goals

- Add a new backend health endpoint.
- Expose secrets or low-level runtime internals.
- Change task, queue, or configuration backend APIs.

## Validation

- Add API coverage for `GET /health`.
- Add configuration panel coverage for healthy and unavailable backend states.
- Run the full frontend test suite.
- Run the frontend production build.
