# 079 Task Report Copy

## Goal

Make a selected task easy to share by generating a Markdown diagnostic report from backend task records and exposing a dashboard copy action.

## Scope

- Add `GET /api/tasks/{taskId}/report` returning a Markdown string.
- Build the report from aggregate task detail data: task metadata, failure reason, latest queue state, queue history count, timeline events, test runs, tool calls, and model calls.
- Add a frontend `getTaskReport(taskId)` API helper.
- Add a `Copy report` action in `TaskDetailPanel`.
- Wire the dashboard action through `App` so the report is fetched from the backend and copied to the clipboard.

## Non-Goals

- Persist generated reports.
- Add report editing or preview UI.
- Include full test output or raw model/tool payloads in the first report version.

## Validation

- Add backend coverage for successful and missing task report requests.
- Add frontend API, component, and App-level coverage for copying reports.
- Run backend tests.
- Run the frontend test suite and production build.
