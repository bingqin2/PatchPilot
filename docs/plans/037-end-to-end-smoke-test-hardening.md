# 037 End-To-End Smoke Test Hardening

## Goal

Run the self-hosted smoke test against the current backend and fix any issues that prevent a documented local demo from working.

## Scope

- Validate the backend test suite.
- Validate Docker Compose configuration and runtime startup.
- Validate local health, task, and queue APIs.
- Validate the temporary Cloudflare Tunnel guide when a local tunnel is available.
- Validate a deterministic GitHub issue trigger using `/agent fix replace docs/demo.md PatchPilot smoke test`.
- Fix code or documentation issues discovered during the smoke test.
- Do not add new product features unless required to unblock the smoke test.

## Required Local Inputs

The smoke test needs a local `.env` file with real values:

- `PATCHPILOT_GITHUB_WEBHOOK_SECRET`
- `PATCHPILOT_GITHUB_TOKEN`
- `PATCHPILOT_AGENT_API_KEY`

The `.env` file is ignored by Git and must not be committed.

## Tasks

- [x] Confirm local `.env` status without exposing secret values.
- [x] Run `docker compose --env-file .env.example config` for placeholder structure.
- [x] Run `docker compose --env-file .env config`.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Start Docker Compose backend and MySQL.
- [x] Verify local `/health`, `/api/tasks`, and `/api/task-queue/summary`.
- [x] Start or verify a temporary Cloudflare Tunnel.
- [x] Trigger `/agent fix replace docs/demo.md PatchPilot smoke test` from a GitHub issue.
- [x] Inspect task detail, timeline, test runs, tool calls, and model calls.
- [x] Verify the GitHub Pull Request or record the failure reason.
- [x] Update docs or code for any issue discovered.

## Acceptance Criteria

- [x] Backend tests pass.
- [x] Docker Compose runtime starts from documented commands.
- [x] Local API checks return success responses.
- [x] A GitHub issue comment creates a task through the webhook.
- [x] The task reaches `COMPLETED` with a Pull Request or fails with a clear actionable reason.
- [x] Smoke test evidence is recorded in `docs/progress/execution-log.md`.

## Findings

- `/agent fix touch ...` is no longer supported by the production planned workflow; documented smoke tests now use `/agent fix replace docs/demo.md PatchPilot smoke test`.
- The first real GitHub-triggered task reached the worker, model call, planned patch, and diff stages.
- Verification failed because `mvn test` ran inside the backend container while inheriting backend runtime environment variables.
- `SPRING_PROFILES_ACTIVE=docker` caused target repository Spring tests to use Docker runtime profile instead of the test/default profile.
- `MavenTestRunner` now removes PatchPilot runtime variables and `SPRING_PROFILES_ACTIVE` from child Maven processes.
- `MavenTestRunner` now reads Maven output while the process is running, preventing large test output from filling the process pipe and being misreported as a timeout.
- The second real smoke test reached Maven output persistence, then failed because full Maven output exceeded the MySQL `text` column.
- Test run output and task failure reasons are now truncated before persistence and status-comment updates.
- The third real smoke test passed Maven verification and failed at `git commit` because the container workspace had no Git author identity.
- `GitCommandRunner` now supplies a fixed PatchPilot author identity for commits with command-scoped `git -c user.name=PatchPilot -c user.email=patchpilot@example.com`.
- `CommandExecutionGuard` allows only that exact command-scoped commit identity, avoiding arbitrary Git configuration commands.
- The fourth real smoke test completed end to end and created Pull Request `https://github.com/bingqin2/PatchPilot/pull/7`.

## Follow-Up

- Review the generated smoke-test Pull Request and then merge or close it from GitHub.
