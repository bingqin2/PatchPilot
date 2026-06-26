# 206 - Demo Launch Tracker

## Goal

Add a dashboard-only launch tracker that connects browser-local prepared demo launch commands with existing webhook delivery, task, and Pull Request evidence after the operator posts the live `/agent fix` issue comment.

## Why This Matters

Plans 201-205 made the pre-launch path reliable: compose the comment, reuse recent commands, preflight the exact trigger, and copy a final launch package. The next demo gap is post-launch confirmation. Operators need to know whether the prepared command reached PatchPilot, created a task, completed execution, or opened a Pull Request without manually comparing command history, delivery rows, task rows, and PR links.

## Scope

- Add a `DemoLaunchTrackerPanel` to the dashboard.
- Read prepared commands from the same browser-local command history context already used by the session report and launch package.
- Match prepared commands to recent tasks by repository, issue number, trigger user, and trigger comment.
- Match prepared commands to webhook deliveries by the same trigger fields, or by the matched task id when delivery trigger text differs from the task record.
- Show status for webhook receipt, task state, Pull Request availability, task detail link, Pull Request link, and the next operator action.
- Keep the feature read-only: no new backend endpoint, no task creation, no GitHub mutation, no queue mutation, and no localStorage writes.

## Out of Scope

- Server-side launch session persistence.
- Posting GitHub comments from the dashboard.
- Polling GitHub directly for delivery status.
- Replaying or redelivering prepared commands automatically.

## Validation

- App-level test proving a saved prepared command correlates with webhook, task, and Pull Request evidence.
- Component tests for successful, waiting-for-webhook, and failed-task launch states.
- Full frontend test/build verification.
- Backend regression check to keep the repository baseline clean even though this slice is frontend-only.
