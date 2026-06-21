# Dashboard Repository Filters Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add repository owner/name filters to the dashboard task list so operators can narrow task history to one GitHub repository and share that filtered view through the URL.

**Architecture:** Reuse the existing backend `GET /api/tasks` `repositoryOwner` and `repositoryName` query parameters. The React dashboard owns repository filter state alongside status, search, and sort, writes repository filters to the URL only when non-empty, passes them through `listTasks`, and keeps pagination and reset behavior consistent.

**Tech Stack:** React, TypeScript, Vite, Vitest, Testing Library.

## Global Constraints

- Implement one complete dashboard feature slice across API helper, UI, tests, and docs.
- Do not add frontend or backend dependencies.
- Do not change backend task-list response shape or repository filtering semantics.
- Preserve selected task routes, unrelated query parameters, hash fragments, sort state, and pagination behavior.

---

### Task 1: Dashboard API and URL State

Status: Complete

**Files:**

- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`

**Behavior:**

- `listTasks()` sends trimmed `repositoryOwner` and `repositoryName` values when provided.
- Initial dashboard load restores `repositoryOwner` and `repositoryName` from the URL.
- Updating either repository filter writes the trimmed value to the URL without dropping status, query, sort, route, or hash state.
- Clearing a repository filter removes only that empty repository query parameter.

**Validation:**

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "repository filter|backend task search sort|offset pagination"`

### Task 2: Task List Repository Filter Controls

Status: Complete

**Files:**

- Modify: `frontend/src/dashboard/components/TaskListPanel.tsx`
- Modify: `frontend/src/styles.css`
- Modify: `frontend/src/App.test.tsx`

**Behavior:**

- The task list renders `Filter repository owner` and `Filter repository name` inputs.
- Active repository filters make `Clear filters` visible.
- `Clear filters` resets status, search, repository owner, and repository name while preserving active sort state.
- `Load more` includes the active repository filters.
- Empty repository-filter results show a repository-specific empty state.

**Validation:**

- `cd frontend && npm test -- src/App.test.tsx -t "repository filter|offset pagination|clear filters"`

### Task 3: Documentation and Final Verification

Status: Complete

**Files:**

- Modify: `README.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/085-dashboard-repository-filters.md`

**Behavior:**

- README documents repository filters as part of dashboard task-list behavior.
- Frontend design notes explain URL-backed repository filter state and reset behavior.
- Execution log records red-green evidence and final verification.

**Validation:**

- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
