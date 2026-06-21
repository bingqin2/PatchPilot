# Dashboard Status Filter Counts Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show per-status task counts on the dashboard status filter buttons so operators can see the current task distribution before switching filters.

**Architecture:** Add `GET /api/tasks/status-counts` with the same search, repository, and created-time filter inputs as `GET /api/tasks`, but without the active status, sort, limit, or offset. The backend computes counts by reusing `FixTaskListQuery` and `FixTaskService.countTasks`. The React dashboard loads counts alongside the task page and renders count badges on the status filter buttons.

**Tech Stack:** Java 17, Spring Boot, React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Implement one complete feature slice across backend, frontend, tests, and docs.
- Do not change `GET /api/tasks` response shape.
- Counts must reflect the current search, repository, and created-time scope.
- Counts must not be narrowed by the currently selected status.
- Preserve status button accessible names as the status labels.

---

### Task 1: Backend Status Count Endpoint

Status: Complete

**Files:**

- Add: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskStatusCountsVo.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/TaskController.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`

**Behavior:**

- `GET /api/tasks/status-counts` returns total plus counts for `PENDING`, `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, `FAILED`, and `CANCELLED`.
- `query`, `repositoryOwner`, `repositoryName`, `createdAfter`, and `createdBefore` narrow the count scope.
- Invalid created-time values return HTTP 400 with the same parameter-specific messages as task listing.
- The endpoint does not accept or apply a status filter.

**Validation:**

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_count_tasks_by_status_for_filtered_scope+should_return_bad_request_for_invalid_status_count_created_time_filter test`

### Task 2: Dashboard Status Count Badges

Status: Complete

**Files:**

- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/TaskListPanel.tsx`
- Modify: `frontend/src/styles.css`

**Behavior:**

- `getTaskStatusCounts()` sends search, repository, and created-time filters but omits status, sort, limit, and offset.
- The dashboard refresh loads status counts with the task page.
- Each status filter button shows a count badge.
- The active status remains keyboard and screen-reader friendly by keeping the accessible button name as the status label.
- Changing search, repository, or created-time filters refreshes count badges for the new scope.

**Validation:**

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "status count|status filter counts"`

### Task 3: Documentation and Final Verification

Status: Complete

**Files:**

- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`
- Add: `docs/plans/087-dashboard-status-filter-counts.md`

**Behavior:**

- README documents status count badges and the backend count endpoint.
- Frontend design notes explain scoped status count behavior.
- Execution log records red-green evidence and final verification.

**Validation:**

- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
