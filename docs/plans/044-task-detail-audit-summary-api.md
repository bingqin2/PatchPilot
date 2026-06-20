# Task Detail Audit Summary API Plan

**Goal:** Add a single read-only task detail summary endpoint for dashboard and operator troubleshooting.

**Scope:** This phase only aggregates existing task, timeline, test-run, tool-call, and model-call records. It does not add tables, change execution behavior, call GitHub, or alter existing detail endpoints.

## Tasks

- [x] Add controller coverage for `GET /api/tasks/{taskId}/summary`.
- [x] Add controller coverage for missing task summary responses.
- [x] Add `FixTaskAuditSummaryVo` for API output.
- [x] Add `FixTaskAuditSummaryService` and default implementation.
- [x] Expose the summary endpoint in `TaskController`.
- [x] Document the endpoint and validation evidence.

## Acceptance Criteria

- Existing task summaries return task info plus timeline, test-run, tool-call, and model-call counts.
- Summary includes total model tokens.
- Summary includes the latest timeline event.
- Summary includes latest test-run exit code and duration.
- Missing task summaries return HTTP `404` with `Task not found`.
