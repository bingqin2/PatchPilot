# Dashboard Detail Evidence Summary

## Goal

Make selected task evidence visible before operators scroll through timeline, test, tool, and model records.

## Scope

- Keep using the existing task detail response.
- Add a frontend-only evidence strip to `TaskDetailPanel`.
- Show record counts for timeline events, test runs, tool calls, and model calls.
- Show the latest test result as `PASS`, `FAIL`, or `None`.
- Do not change backend endpoints or database fields.

## Frontend Design

Render a compact `Execution evidence` strip below the existing detail summary cards:

- `Timeline {count}`
- `Tests {count}`
- `Tools {count}`
- `Model calls {count}`
- `Latest test PASS|FAIL|None`

The strip should remain readable on mobile by wrapping chips instead of resizing the detail layout.

## Testing

- Component test for a selected task with timeline, test, tool, and model evidence.
- Component test for a selected task with no latest test result.
- Run the full frontend test suite and production build.

## Acceptance Criteria

- Selecting a task shows execution evidence before the detailed record sections.
- Latest test status is visible without reading Maven output.
- Existing detail sections and task actions remain unchanged.
