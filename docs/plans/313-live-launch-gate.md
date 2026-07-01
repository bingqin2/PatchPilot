# 313 Live Launch Gate

## Goal

Add one final read-only gate before posting a real GitHub `/agent fix ...` issue comment. The operator should paste the exact repository, issue, user, and comment, then see whether PatchPilot is ready to launch the live demo.

## Scope

- Add a backend aggregation API at `POST /api/demo/live-launch-gate`.
- Reuse the existing live trigger dry-run logic from a service instead of duplicating controller code.
- Aggregate self-hosted launch readiness, webhook setup readiness, live GitHub publish preflight, and trigger dry-run result.
- Return a single `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status with checks, next actions, side-effect contract, and copyable Markdown report.
- Add a dashboard panel that runs the gate from the browser and displays the full readiness package.

## Non-Goals

- Do not create tasks, enqueue work, write GitHub comments, push Git branches, create Pull Requests, or record rate-limit usage.
- Do not replace the existing detailed readiness/preflight panels.

## Verification

- Backend: targeted tests for service/controller behavior, then full `mvn -pl PatchPilot test`.
- Frontend: component/API tests, then full `npm test` and `npm run build`.
- Manual smoke: run dashboard against backend and confirm the launch gate reports a clear go/no-go state.
