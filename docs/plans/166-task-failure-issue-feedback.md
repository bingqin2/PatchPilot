# 166 - Task Failure Issue Feedback

## Goal

Make failed task outcomes visible on the originating GitHub issue even when the initial accepted-task status comment was not created, while keeping issue-facing failure text safe and actionable.

## Scope

- Create a failed-task issue feedback comment when a task fails and has no stored status comment.
- Continue updating the existing status comment when one exists.
- Attach the created feedback comment id and URL back to the task.
- Record timeline evidence for feedback creation or feedback failure without changing the durable task failure status.
- Add issue-facing failure categories, next actions, and secret-like value redaction to failed-task comments.
- Label failed-task feedback links as `Failure feedback` in the dashboard.

## Backend Behavior

`FixTaskWorker` now treats failed-task GitHub feedback as best-effort but durable:

- If `statusCommentId` exists, the worker updates the same issue comment to `FAILED`.
- If `statusCommentId` is missing, the worker creates a failed-task issue comment through `IssueCommentTool.commentFailed`.
- Successful creation stores the comment id and URL on the task through `FixTaskService.attachStatusComment`.
- Creation or update failure records `STATUS_COMMENT_FAILED` and leaves the task in `FAILED`.

`TaskFailureFeedback` classifies issue-facing failures into categories such as `VERIFICATION_FAILED`, `GITHUB_OPERATION_FAILED`, `UNSUPPORTED_REPOSITORY`, `MODEL_FAILED`, `WORKSPACE_FAILED`, and `TASK_FAILED`. It also redacts common GitHub token, OpenAI-style key, and assignment-style secret patterns before the reason is posted to GitHub.

## Frontend Behavior

Task list and task detail links now label failed task status-comment URLs as `Failure feedback`. Successful and in-progress tasks keep the generic `Status Comment` label, while pending-review tasks use `Review feedback`.

## Validation

- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_create_failure_status_comment_when_initial_status_comment_is_missing test`
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_keep_failed_status_when_failure_status_comment_creation_fails test`
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_create_failed_status_comment_with_category_and_next_action+IssueCommentToolTests#should_redact_sensitive_values_from_failed_status_comment test`
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`
- `npm test -- --run src/App.test.tsx`
