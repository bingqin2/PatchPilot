# 200 Demo Target Policy Alignment

## Goal

Make demo readiness catch a common live-demo failure before the operator posts `/agent fix`: the configured GitHub token can read the demo repository, but the safety policy would still reject the repository or trigger user.

## Scope

- Add a `Demo target policy` check to `GET /api/demo/readiness`.
- Compare the configured demo repository from repository-access readiness with `PATCHPILOT_ALLOWED_REPOSITORIES` when the repository allowlist is enabled.
- Compare the most recent task trigger user with `PATCHPILOT_ALLOWED_TRIGGER_USERS` when the trigger-user allowlist is enabled.
- Return clear setup actions that name the missing allowlist value.
- Show the same check in the dashboard operator setup checklist.
- Document the new readiness behavior in README, product spec, frontend design notes, and the execution log.

## Out of Scope

- No new GitHub API mutation.
- No task creation, model call, Git operation, or repository content read.
- No new demo trigger-user environment variable; recent task history remains the source of trigger-user evidence.
- No change to standalone GitHub credential or repository access readiness contracts.

## Verification

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`
- Full backend and frontend regression suites before merge.
