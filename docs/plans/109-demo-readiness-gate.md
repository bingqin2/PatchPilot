# Demo Readiness Gate

## Goal

Add a single backend and dashboard readiness gate that tells an operator whether PatchPilot is ready for a controlled issue-to-PR demo before posting a real `/agent fix` comment.

This feature reduces demo risk by aggregating the setup checks that were previously scattered across configuration, queue, adapter fixture, task history, and terminal smoke commands.

## Scope

- Add `GET /api/demo/readiness`.
- Aggregate readiness from backend reachability, configured credentials, adapter fixture verification, queue health, and recent Pull Request evidence.
- Return one top-level status: `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- Return named checks with messages and actionable next steps.
- Add a dashboard `DemoReadinessPanel` near the top of the operations page.
- Keep readiness API failures local to the panel so the rest of the dashboard remains usable.
- Update README, architecture notes, frontend design notes, and execution log.

## Readiness Rules

- Missing required credentials block the demo.
- Adapter fixture failures block the demo because supported repository detection may be drifting.
- Missing model cost configuration needs attention but does not block execution.
- Failed, delayed, or running queue items need attention before a clean demo.
- A recent completed task with a Pull Request URL is the strongest success signal; if absent, the operator should run a controlled smoke task first.

## Out of Scope

- Auto-fixing readiness failures.
- Editing configuration from the frontend.
- Deploying separate API and worker health checks.
- Replacing detailed task, queue, or adapter panels.

## Validation

- Backend service and controller tests for ready, blocked, and warning states.
- Frontend API, component, and dashboard integration tests.
- Full backend and frontend test suites.
- Production frontend build.
