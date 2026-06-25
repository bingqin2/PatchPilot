# Task Pre-Execution Safety Snapshot

## Goal

Expose a compact, structured snapshot of the checks that allowed a task to execute. Operators should not need to infer the accepted path from raw timeline text when reviewing why PatchPilot moved from `/agent fix` input into repository work.

## Scope

- Add a task detail field for accepted tasks that summarizes source, final decision, safety-gate result, quarantine state, rate-limit state, issue-context state, model trigger-classification result, and evidence timestamp.
- Derive the first version from durable accepted-trigger timeline evidence so existing tasks remain readable without a schema migration.
- Include the same snapshot in copied Markdown task reports.
- Render the snapshot in the dashboard task detail panel next to trigger intent evidence.
- Cover backend task detail/report behavior, frontend API parsing, and dashboard rendering with tests.
- Update product and progress documentation.

## Non-Goals

- Do not add a new persisted snapshot table in this slice.
- Do not change task creation ordering or the existing safety-gate policy.
- Do not expose raw unsafe trigger text or model prompts.
- Do not replace rejected-trigger audit, quarantine audit, or admin mutation audit surfaces.

## Validation

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
