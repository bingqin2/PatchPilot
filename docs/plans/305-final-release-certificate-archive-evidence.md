# 305 - Final Release Certificate Archive Evidence

## Goal

Promote the archived final external-review release-bundle delivery certificate into the top-level evidence bundle, copied runbook, and dashboard evidence panel. This makes the durable terminal reviewer handoff proof visible without opening the final acceptance archive history.

## Scope

- Add a top-level evidence VO for the latest release-bundle delivery certificate archive.
- Aggregate archive readiness, certification state, linked finalization/archive/receipt ids, Pull Request, archived time, next action, and download actions in `DemoEvidenceBundleService`.
- Repeat the same terminal archive proof in `GET /api/demo/runbook`.
- Render the evidence in `DemoEvidenceBundlePanel` with a legacy fallback for older backend payloads.
- Cover backend aggregation, runbook export, frontend rendering, and legacy fallback behavior with tests.

## Out of Scope

- Adding new certificate archive create/list/download endpoints; those are covered by plan 304.
- Changing archive persistence schemas or protected audit behavior.
- Sending external messages or writing to GitHub.

## Verification

- `mvn -q -pl PatchPilot test`
- `cd frontend && npm test -- --reporter=dot`
- `cd frontend && npm run build`
- `git diff --check`
