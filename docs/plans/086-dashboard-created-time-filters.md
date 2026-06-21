# Dashboard Created Time Filters Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add created time range filters to backend task listing and the dashboard so operators can narrow task history by when work was queued.

**Architecture:** Extend `GET /api/tasks` with optional `createdAfter` and `createdBefore` ISO-8601 instant query parameters. The backend applies inclusive range filtering in both in-memory and MyBatis task services. The React dashboard owns these filters beside status, search, repository, and sort state, writes them to the URL only when non-empty, and carries them through refresh and pagination.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Implement one complete feature slice across backend, frontend, tests, and docs.
- Do not change task-list response shape.
- Accept only ISO-8601 instant values such as `2026-06-20T01:00:00Z`.
- Preserve selected task routes, unrelated query parameters, hash fragments, sort state, and pagination behavior.

---

### Task 1: Backend Created Time Filtering

Status: Complete

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/TaskController.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/bo/FixTaskListQuery.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskServiceTests.java`

**Behavior:**

- `createdAfter` includes tasks whose `createdAt` is equal to or after the provided instant.
- `createdBefore` includes tasks whose `createdAt` is equal to or before the provided instant.
- Invalid instant values return HTTP 400 with a parameter-specific message.
- Existing status, repository, search, sort, limit, and offset behavior remains unchanged.

**Validation:**

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_filter_tasks_by_created_time_range+should_return_bad_request_for_invalid_created_time_filter,InMemoryFixTaskServiceTests#should_list_tasks_with_created_time_range,MyBatisFixTaskServiceTests#should_list_tasks_with_created_time_range test`

### Task 2: Dashboard Created Time Controls

Status: Complete

**Files:**

- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/TaskListPanel.tsx`
- Modify: `frontend/src/styles.css`

**Behavior:**

- The dashboard task list renders `Filter created after` and `Filter created before` inputs.
- `listTasks()` sends trimmed `createdAfter` and `createdBefore` values when provided.
- Initial dashboard load restores created time filters from the URL.
- Updating either time filter writes it to the URL while preserving status, search, repository, sort, selected task route, and hash state.
- `Clear filters` resets status, search, repository, and created time filters while preserving active sort.
- `Load more` includes active created time filters.

**Validation:**

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "created time filter|backend task search sort|offset pagination"`

### Task 3: Documentation and Final Verification

Status: Complete

**Files:**

- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`
- Add: `docs/plans/086-dashboard-created-time-filters.md`

**Behavior:**

- README documents created time filters as part of task-list behavior.
- Frontend design notes explain URL-backed created time filter state and reset behavior.
- Execution log records red-green evidence and final verification.

**Validation:**

- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
