# 202 Demo Launch Command Composer

## Goal

Add a read-only launch command composer so an operator can build the exact `/agent fix` GitHub issue comment from structured fields, copy it, and apply it to launch preflight before posting anything on GitHub.

## Why This Matters

PatchPilot's live demo path now has readiness, smoke-check, evidence bundle, session report, and launch preflight coverage. The remaining operator risk is typing the final GitHub comment by hand. A structured composer reduces typo risk, keeps supported demo operations explicit, and connects directly to the existing dry-run preflight gate.

## Scope

- Add `POST /api/demo/launch-command`.
- Support initial demo operations:
  - `replace`: `/agent fix replace <targetPath> <replacementText>`
  - `touch`: `/agent fix touch <targetPath>`
- Return:
  - Generated `triggerComment`.
  - `preflightInput` that can be submitted to `/api/demo/launch-preflight`.
  - GitHub issue URL.
  - Summary and next actions.
- Reject unsafe or ambiguous command inputs before composing:
  - Missing repository, issue, user, operation, or target path.
  - Unsupported operations.
  - Absolute paths, `..` segments, empty path segments, whitespace in target paths, and protected repository metadata paths such as `.git` or `.github`.
  - Blank replacement text for `replace`.
- Add a dashboard panel that composes the command, copies it, and applies it to the launch preflight form.
- Update README, product spec, frontend design, and execution log.

## Non-Goals

- Do not create tasks.
- Do not post GitHub issue comments.
- Do not mutate GitHub, Git, queue, task, or rejected-trigger state.
- Do not expand the supported command grammar beyond the controlled demo operations.

## Validation

- Backend focused tests:
  - `mvn -pl PatchPilot -Dtest=DemoLaunchCommandServiceTests,DemoReadinessControllerTests test`
- Frontend focused tests:
  - `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchCommandPanel.test.tsx src/dashboard/components/DemoLaunchPreflightPanel.test.tsx src/App.test.tsx --reporter=basic`
- Full regression:
  - `mvn -pl PatchPilot test`
  - `npm test -- --reporter=basic`
  - `npm run build`
  - `git diff --check`
