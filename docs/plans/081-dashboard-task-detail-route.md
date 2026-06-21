# Dashboard Task Detail Route Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace query-only selected task links with a cleaner `/tasks/{taskId}` dashboard route while keeping old `?taskId=` links compatible.

**Architecture:** Keep the dashboard as a single React app without adding a routing library. Parse selected task ids from `/tasks/{taskId}` first, then fall back to `?taskId=...`; write selected task links back as path routes while preserving unrelated query parameters and hash fragments.

**Tech Stack:** React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Do not add a router dependency for this small route upgrade.
- Keep `?taskId=` backward-compatible for previously copied links.
- Preserve existing status/search query parameters when selecting or copying a task link.
- Keep all backend APIs unchanged.

---

### Task 1: Path-Based Task Detail Links

**Files:**

- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/TaskDetailPanel.tsx`
- Modify: `frontend/src/dashboard/components/TaskDetailPanel.test.tsx`
- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`

**Behavior:**

- Loading `/tasks/task-2` selects `task-2`.
- Loading `/?taskId=task-2` still selects `task-2`.
- Selecting a task writes `/tasks/task-2` and removes `taskId` from the query string.
- Copying a task link returns `/tasks/task-1` while preserving unrelated query parameters and hash fragments.

**Validation:**

- `cd frontend && npm test -- src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx -t "task detail route|taskId URL parameter|selected task route|shareable task link|deep link"`
- `cd frontend && npm test`
- `cd frontend && npm run build`
