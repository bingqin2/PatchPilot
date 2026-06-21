# 080 Dashboard Manual Task Creation

## Goal

Allow operators to create a PatchPilot task directly from the dashboard for local demos and debugging, without needing to post a GitHub issue comment manually.

## Scope

- Add a backend `POST /api/tasks` endpoint for manual task creation.
- Validate repository owner, repository name, issue number, trigger user, and trigger comment.
- Reuse normal task persistence, timeline recording, and queue dispatch.
- Reject manual creation when the same issue already has an active task.
- Add a dashboard form that submits a task and refreshes the task list.

## Non-Goals

- Do not add authentication or multi-user permissions.
- Do not bypass the existing queue or worker.
- Do not create GitHub issue comments from manual creation.
- Do not add a full issue browser or repository picker.

## Validation

- Backend controller tests for successful manual task creation, validation failure, and active-task conflict.
- Frontend API tests for `createTask`.
- Dashboard tests for submitting the manual task form, refreshing data, and showing errors.
- Full backend and frontend test suites before handoff.
