# 049 Dashboard Queue Observability

## Goal

Add a read-only queue panel to the React dashboard so operators can inspect backlog, retry delay, running queue items, and queue-level failures without using curl.

## Scope

- Reuse existing backend endpoints:
  - `GET /api/task-queue/summary`
  - `GET /api/task-queue/items`
- Show aggregate queue counts for total, pending, available pending, delayed pending, running, failed, and cancelled work.
- Show recent queue items with id, task id, status, attempt count, available time, and last error.
- Keep the dashboard queue view read-only.
- Do not add queue mutation actions or backend API changes.

## Tasks

1. Add frontend tests for queue summary and queue item rendering.
2. Add typed queue response interfaces.
3. Add frontend API helpers for queue summary and item list endpoints.
4. Load queue data during dashboard refresh.
5. Render a compact queue panel below task list and task detail.
6. Document queue visibility and record validation evidence.

## Acceptance Criteria

- Dashboard requests `/api/task-queue/summary`.
- Dashboard requests `/api/task-queue/items`.
- Queue summary shows pending, available, delayed, running, failed, and cancelled counts.
- Queue items show status, task id, attempts, available time, and last error.
- `npm test` and `npm run build` pass under `frontend/`.
