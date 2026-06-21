# Dashboard Filter Reset Action Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a clear filter reset action to the dashboard task list so operators can return to the full task view with one click.

**Architecture:** Keep reset state owned by `App` with existing status/search state. `TaskListPanel` receives a `canClearFilters` flag and `onClearFilters` callback, rendering a compact button only when status is not `ALL` or search is non-empty. The reset handler clears status/search, removes `status` and `query` from the URL, preserves `/tasks/{taskId}` and unrelated query/hash state, and lets the existing refresh effect reload the default task page.

**Tech Stack:** React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Do not add frontend dependencies.
- Do not reset selected task when clearing filters.
- Remove only `status` and `query`; preserve unrelated query parameters and hash fragments.
- Hide the reset action when no filters are active.
- Keep backend API behavior unchanged.

---

### Task 1: Clear Active Task Filters

Status: Complete

**Files:**

- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/TaskListPanel.tsx`
- Modify: `frontend/src/styles.css`
- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`

**Behavior:**

- `Clear filters` is hidden when status is `ALL` and search is empty.
- `Clear filters` is shown when status is not `ALL` or search is non-empty.
- Clicking it sets status to `ALL`, clears the search input, removes `status` and `query` from the URL, and preserves `/tasks/{taskId}`.
- After clearing, the dashboard calls `GET /api/tasks?limit=50`.

**Validation:**

- `cd frontend && npm test -- src/App.test.tsx -t "clear filters"`
- `cd frontend && npm test`
- `cd frontend && npm run build`
