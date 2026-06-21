# Dashboard Sort Control Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a complete task-list sort feature so operators can switch between newest-first and oldest-first task history from the dashboard and share that view through the URL.

**Architecture:** Add task-list sort direction to the backend query object and `GET /api/tasks` API, keeping newest-first as the default. The React dashboard owns sort state alongside status and search, writes it to the URL only when non-default, sends it to `listTasks`, and exposes a compact sort control in `TaskListPanel`.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, JUnit 5, React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Implement one complete feature slice across backend, frontend, tests, and docs.
- Do not add frontend or backend dependencies.
- Keep default API/dashboard behavior newest-first.
- Preserve existing status, search, task route, unrelated query parameter, hash, pagination, and clear-filter behavior.
- Treat invalid sort parameters as `400 Bad Request` on the backend and default them to newest-first when restoring frontend URL state.

---

### Task 1: Backend Task List Sort

Status: Complete

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/bo/FixTaskListQuery.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskSort.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/TaskController.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskServiceTests.java`

**Behavior:**

- `GET /api/tasks` defaults to newest-first.
- `GET /api/tasks?sort=createdAtAsc` returns oldest-first.
- `GET /api/tasks?sort=createdAtDesc` returns newest-first.
- Sorting is applied before offset/limit pagination.
- Invalid sort values return `400` with `sort must be createdAtDesc or createdAtAsc`.

**Validation:**

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_sort_tasks_oldest_first,TaskControllerTests#should_return_bad_request_for_invalid_task_list_sort test`
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests#should_list_tasks_oldest_first_when_requested,MyBatisFixTaskServiceTests#should_list_tasks_oldest_first_when_requested test`

### Task 2: Dashboard Sort Control and URL State

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

- Task list renders a sort control with `Newest first` and `Oldest first`.
- The default `Newest first` state does not add `sort` to the URL.
- Selecting `Oldest first` writes `sort=createdAtAsc` to the URL and requests `GET /api/tasks?limit=50&sort=createdAtAsc`.
- Selecting `Newest first` removes `sort` from the URL and requests newest-first data.
- Initial dashboard load restores valid `sort=createdAtAsc` from the URL.
- Invalid frontend URL sort values fall back to newest-first.
- `Clear filters` continues to clear only status/search and preserves sort.
- `Load more` includes the active sort parameter.

**Validation:**

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "sort"`
- `cd frontend && npm test`
- `cd frontend && npm run build`

### Task 3: Documentation and Final Verification

Status: Complete

**Files:**

- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/084-dashboard-sort-control.md`

**Behavior:**

- README describes task-list sorting as part of dashboard behavior.
- Frontend design notes explain URL-backed sort state and default behavior.
- Execution log records red-green evidence and final verification.

**Validation:**

- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
