# Dashboard Filter URL State Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist dashboard status and search filters in the browser URL so shared task views reopen with the same filtering context.

**Architecture:** Keep routing dependency-free. Parse `status` and `query` from `window.location.search` during App initialization, validate status against known task filters, and update the current URL whenever filters change while preserving `/tasks/{taskId}` routes, unrelated query params, and hash fragments.

**Tech Stack:** React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Do not add a routing or query-state dependency.
- Treat invalid `status` query values as `ALL`.
- Remove `status=ALL` and empty `query` from the URL.
- Preserve selected task path routes and unrelated query parameters.
- Keep backend API behavior unchanged.

---

### Task 1: URL-Backed Dashboard Filters

**Files:**

- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`

**Behavior:**

- Loading `/?status=FAILED&query=broken` initializes the FAILED filter and search input, then calls `GET /api/tasks?limit=50&query=broken&status=FAILED`.
- Loading `/tasks/task-2?status=FAILED&query=broken` selects `task-2` and applies the same filters.
- Clicking a status filter writes `status={value}` to the URL, except `ALL` removes `status`.
- Typing a search query writes `query={value}` to the URL; clearing search removes `query`.
- Selecting a task keeps current filter query parameters when moving to `/tasks/{taskId}`.

**Validation:**

- `cd frontend && npm test -- src/App.test.tsx -t "filter URL state|task detail route with filters|syncs status filter|syncs search query|removes cleared search"`
- `cd frontend && npm test`
- `cd frontend && npm run build`
