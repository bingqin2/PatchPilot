# 167 - Stable Failure Category Metrics

## Goal

Use one stable failure taxonomy across failed-task issue feedback, backend metrics, and dashboard operations so operators can diagnose repeated failures without reading raw exception text.

## Scope

- Reuse `TaskFailureFeedback` as the backend source of truth for failed-task metrics categories.
- Keep the failure metrics API backward-compatible with `cause` and `count`, and add `nextAction` for operator guidance.
- Replace older metrics-only categories such as `MAVEN_TESTS`, `GITHUB_AUTH`, and `MODEL_ERROR` with issue-facing categories such as `VERIFICATION_FAILED`, `GITHUB_OPERATION_FAILED`, `MODEL_FAILED`, and `TASK_FAILED`.
- Show stable category labels and next actions in the dashboard failure-cause panel.
- Update tests and documentation for the new API contract.

## Backend Behavior

`GET /api/tasks/metrics/failure-causes` now groups failed tasks by `TaskFailureFeedback.from(failureReason).category()`. The endpoint returns each category with:

- `cause`: the stable category id.
- `count`: the number of failed tasks in the current metrics scope.
- `nextAction`: the same guidance used by issue-facing failed-task feedback comments.

The endpoint still respects the same repository, adapter, search, and created-time metrics filters.

## Frontend Behavior

`FailureCausePanel` renders human-readable labels for stable failure categories and displays the backend-provided next action below each category. This keeps the dashboard useful even when a failure category is new or rare, because the operator can act without opening each task.

## Validation

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_stable_failure_category test`
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_failure_cause_summary test`
- `npm test -- --run src/dashboard/components/FailureCausePanel.test.tsx`
- `npm test -- --run src/dashboard/components/FailureCausePanel.test.tsx src/api.test.ts src/App.test.tsx`
