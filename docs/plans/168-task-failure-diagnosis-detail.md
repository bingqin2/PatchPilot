# 168 - Task Failure Diagnosis Detail

## Goal

Expose the same safe failure diagnosis used by issue feedback and metrics inside individual task inspection surfaces, so operators can understand why one task failed without opening raw logs first.

## Scope

- Add a nullable `failureDiagnosis` object to `GET /api/tasks/{taskId}/detail` for failed tasks.
- Reuse `TaskFailureFeedback` for category, next action, and sanitized failure reason.
- Include the diagnosis in copyable Markdown task reports.
- Render a `Failure diagnosis` section in the dashboard selected-task detail panel.
- Keep successful, pending, cancelled, and non-failed tasks unchanged.

## Backend Behavior

For failed tasks with a stored `failureReason`, the task detail endpoint returns:

- `category`: stable category such as `VERIFICATION_FAILED` or `GITHUB_OPERATION_FAILED`.
- `nextAction`: operator guidance from the shared failure taxonomy.
- `safeReason`: the failure reason after secret-like value redaction.

The Markdown report includes the same values in a `Failure Diagnosis` section. The endpoint does not mutate task state or create GitHub comments.

## Frontend Behavior

`TaskDetailPanel` renders failed-task diagnosis above issue context and queue evidence. The section shows a human-readable category label, the backend next action, and the sanitized reason. It does not render raw secret-like tokens from the original failure reason.

## Validation

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_failure_diagnosis_for_failed_task_detail+TaskControllerTests#should_get_task_report_by_task_id test`
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts src/App.test.tsx`
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`
- `npm test`
- `npm run build`
- `mvn -pl PatchPilot test`
