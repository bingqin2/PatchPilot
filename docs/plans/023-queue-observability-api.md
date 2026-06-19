# Queue Observability API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose read-only HTTP endpoints for inspecting MySQL-backed task queue state during local and docker deployments.

**Architecture:** Add a queue query service boundary separate from the execution queue. The default profile returns an empty snapshot so local no-database runs still start cleanly. The `local` and `docker` profiles read `fix_task_queue_item` through MyBatis and expose item details plus aggregate queue counts.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, JUnit 5, Spring MockMvc, AssertJ, Mockito.

## Global Constraints

- Keep all new queue endpoints read-only.
- Do not change queue execution, retry, or recovery behavior.
- Keep `default` profile database-free.
- Do not add new infrastructure services.
- Return queue responses through the existing `ApiResponse` envelope.

---

## Task 1: Add Queue Query Contract

- [x] Add `FixTaskQueueQueryService` with `listItems(status)` and `summary()`.
- [x] Add `FixTaskQueueSummaryVo` for aggregate counts.
- [x] Add default-profile empty query implementation.

## Task 2: Add MyBatis Queue Query Implementation

- [x] Add `MyBatisFixTaskQueueQueryService` for `local` and `docker` profiles.
- [x] Support optional status filtering for queue item listing.
- [x] Return item details using existing `FixTaskQueueItemVo`.
- [x] Summarize total, pending, available pending, delayed pending, running, completed, and failed counts.

## Task 3: Add Read-Only Queue API

- [x] Add `GET /api/task-queue/items`.
- [x] Add optional `status` query parameter.
- [x] Add `GET /api/task-queue/summary`.
- [x] Keep default profile endpoints available with empty data.

## Task 4: Validate And Document

- [x] Add service and controller tests.
- [x] Run focused queue observability tests.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Operators can inspect queue item records through HTTP.
- [x] Operators can see queue backlog and failure counts through HTTP.
- [x] Default profile remains database-free.
- [x] No queue mutation endpoint is introduced.
- [x] Full backend tests pass.
