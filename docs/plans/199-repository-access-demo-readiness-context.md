# 199 Repository Access Demo Readiness Context

## Goal

Make demo readiness validate the same GitHub repository that will be used in a live `/agent fix` run. The previous repository access probe existed as a standalone API and dashboard row, but the one-call demo readiness, evidence bundle, runbook, and session snapshot could still appear ready without proving that the configured token can read the demo target repository.

## Scope

- Add demo repository configuration through `PATCHPILOT_DEMO_REPOSITORY_OWNER` and `PATCHPILOT_DEMO_REPOSITORY_NAME`.
- Wire `DemoReadinessService` to call the existing GitHub repository access readiness probe with the configured demo repository.
- Add a `GitHub repository access` readiness check:
  - `READY` when the token can read the repository.
  - `NEEDS_ATTENTION` when the demo repository target is not configured.
  - `BLOCKED` when the repository is configured but the probe fails.
- Make the operator setup checklist prefer the demo readiness result for repository access, keeping the standalone endpoint as fallback.
- Document the new environment variables in `.env.example`, Docker Compose, and README.

## Out of Scope

- No new GitHub API mutation.
- No task creation, model call, Git operation, or repository content read.
- No change to the standalone `/api/github/repository-access-readiness` contract.

## Verification

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`
- Full backend and frontend regression suites before merge.
