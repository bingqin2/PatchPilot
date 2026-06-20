# 046 React Dashboard Scaffold

## Goal

Add the first React operations dashboard so developers can inspect PatchPilot task status, failure reasons, Pull Request links, and execution records from real backend APIs.

## Scope

- Create a React + Vite + TypeScript frontend under `frontend/`.
- Call existing backend APIs through the Vite dev proxy.
- Show task list, operational metrics, selected task summary, timeline, Maven test output, tool calls, and model calls.
- Keep the UI work-focused; do not add login, task creation, admin settings, or marketing pages.

## Tasks

1. Add frontend package, TypeScript, Vite, Vitest, and Testing Library configuration.
2. Define typed API helpers for current backend task and metrics endpoints.
3. Build a compact dashboard with task list, metric cards, and detail panels.
4. Add tests for successful backend rendering and actionable error display.
5. Document frontend setup and record validation evidence.

## Acceptance Criteria

- `npm test` passes under `frontend/`.
- `npm run build` passes under `frontend/`.
- The dashboard calls real backend routes such as `/api/tasks?limit=50`, `/api/tasks/metrics/summary`, and `/api/tasks/{id}/timeline`.
- The first screen is the usable dashboard, not a landing page.
