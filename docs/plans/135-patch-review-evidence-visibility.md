# 135 Patch Review Evidence Visibility

## Goal

Persist and display the post-edit model review result introduced in plan 134 so operators can inspect why PatchPilot approved or blocked model-generated edits.

This moves PatchPilot closer to a credible self-hosted issue-to-PR agent: a failed task should show whether it failed because tests failed, tools failed, or the review gate intentionally blocked unsafe or irrelevant edits.

## Scope

- Add a task-level patch review record with decision, reason, confidence, required follow-up, edited files, and timestamp.
- Persist patch review evidence in both default in-memory mode and MySQL-backed `local`/`docker`/`idea` modes.
- Record review evidence before writing model-generated files, including rejected reviews that stop execution.
- Add the latest patch review to task detail API responses and markdown task reports.
- Render patch review evidence in the dashboard task detail panel with distinct approved and blocked states.

## Non-Goals

- Do not automatically regenerate a rejected patch.
- Do not change the human risk-review approval flow.
- Do not review manual `/agent fix replace <path> <content>` smoke commands.
- Do not create a separate dashboard page for patch reviews.

## Validation

- `mvn -pl PatchPilot -Dtest=PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,TaskControllerTests,FixTaskPatchReviewConvertTests,InMemoryFixTaskPatchReviewServiceTests,MyBatisFixTaskPatchReviewServiceTests,FixTaskPatchReviewMigrationTests test`
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
