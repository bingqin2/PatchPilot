# 076 Task Detail Aggregate API

## Goal

Load selected task detail through one backend read-model endpoint instead of five separate dashboard requests.

## Scope

- Add `GET /api/tasks/{taskId}/detail`.
- Return task audit summary, timeline events, test runs, tool calls, and model calls in one response.
- Keep existing detail endpoints for direct debugging and backward compatibility.
- Add a frontend `getTaskDetail()` helper.
- Switch the dashboard selected-task loader to the aggregate endpoint.

## Non-Goals

- Change task execution, queue behavior, persistence schema, or audit record ordering.
- Remove existing `/summary`, `/timeline`, `/test-runs`, `/tool-calls`, or `/model-calls` endpoints.
- Add a dedicated task detail route.

## Validation

- Add backend controller coverage for successful and missing aggregate task detail requests.
- Add frontend API coverage for `getTaskDetail()`.
- Add dashboard coverage that selected task detail loads from `/api/tasks/{taskId}/detail`.
- Run backend tests.
- Run the full frontend test suite and production build.
