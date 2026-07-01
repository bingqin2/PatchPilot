# 317 External Exposure Handoff Package

## Goal

Create a read-only handoff package for the final step before sharing a temporary public URL. The package combines the current external exposure readiness gate with the latest archived exposure evidence so an operator can see whether the public URL is ready to share and download one Markdown report.

## Scope

- Add `GET /api/security/external-exposure-handoff-package`.
- Add `GET /api/security/external-exposure-handoff-package/report/download`.
- Derive package status from current readiness, latest archive presence, archived safe-to-expose state, and archive freshness.
- Render package status, archive freshness, evidence notes, and report download in the operations dashboard external exposure panel.
- Refresh the package after external exposure readiness refreshes or a new readiness archive is created.

## Non-Goals

- No new task trigger path.
- No model calls.
- No Git, GitHub, Pull Request, webhook, network probe, or queue mutations.
- No new persistence table; this package is a read model over current readiness and existing readiness archives.

## Verification

- Backend service and controller tests cover ready and missing-archive handoff behavior.
- Frontend API, panel, and App smoke tests cover initial loading, display, and report downloads.
- Full backend/frontend test suites and build must pass before merge.
