# 030 Model Call Audit Records

## Goal

Persist and expose model-call audit records so future real model integration can be inspected without changing task APIs later.

## Scope

- Add a `fix_task_model_call` table.
- Add model-call VO, entity, converter, mapper, and service boundary.
- Provide in-memory and MyBatis-backed service implementations.
- Expose `GET /api/tasks/{id}/model-calls`.
- Keep this phase audit-only. Do not call real model providers.

## Tasks

- [x] Add model-call conversion and migration tests.
- [x] Add in-memory and MyBatis service tests.
- [x] Add task controller API tests.
- [x] Implement model-call domain, persistence, and service classes.
- [x] Wire `TaskController` to expose model calls for existing tasks.
- [x] Run focused tests and full backend tests.

## Acceptance Criteria

- [x] Model-call records include provider, model, prompt summary, response summary, token counts, success state, timing, and optional error message.
- [x] Missing task ids return `404` from `/api/tasks/{id}/model-calls`.
- [x] Default profile starts without MySQL.
- [x] Local/docker profiles can persist records through MyBatis.
- [x] Existing task, timeline, test-run, tool-call, queue, and cancellation tests still pass.
