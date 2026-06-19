# Active Task Deduplication and Queue Abstraction Plan

> **For agentic workers:** Use test-driven development for each task. Do not add an external queue service in this phase.

**Goal:** Prevent multiple active PatchPilot tasks from running for the same repository issue, and introduce a queue boundary that can later be backed by Redis, RabbitMQ, or another durable queue.

**Architecture:** Webhooks check for an existing active task before creating a new one. The dispatcher only enqueues task ids. A worker owns task state transitions, verification, Pull Request creation, comments, and timeline events. The default queue remains in-process and asynchronous.

## Constraints

- Keep the existing webhook response wrapper and task APIs.
- Do not add Redis, RabbitMQ, Kafka, or new compose services in this phase.
- Treat `PENDING`, `RUNNING`, and `RUNNING_TESTS` as active.
- Do not create a second task for the same `owner/repo#issue` while an active task exists.
- Duplicate delivery idempotency remains separate from active-task deduplication.
- Timeline and GitHub comment updates must not block durable task state changes.

## Task 1: Active Task Lookup

- [x] Add a `FixTaskService#findActiveTaskForIssue(owner, repo, issueNumber)` contract.
- [x] Add a `FixTaskService#findTaskByDeliveryId(deliveryId)` contract so duplicate delivery idempotency remains first-class.
- [x] Implement it for in-memory storage.
- [x] Implement it for MyBatis storage.
- [x] Add a V5 migration with an issue/status lookup index.
- [x] Cover active statuses and terminal statuses in service tests.

## Task 2: Webhook Active Deduplication

- [x] Add `WebhookHandleStatus.ACTIVE_TASK_EXISTS`.
- [x] Return the existing active task id when a new `/agent fix` arrives for the same issue.
- [x] Do not create, dispatch, or create a new accepted status comment for duplicate active requests.
- [x] Record an `ACTIVE_TASK_EXISTS` timeline event on the existing task.
- [x] Edit the existing task status comment when possible to tell the user a task is already active.
- [x] Preserve duplicate delivery priority over active-task deduplication.

## Task 3: Queue Boundary

- [x] Add `FixTaskQueue#enqueue(String taskId)`.
- [x] Add an in-memory queue implementation that asynchronously invokes the worker.
- [x] Move task execution from `AsyncFixTaskDispatcher` into `FixTaskWorker`.
- [x] Make `AsyncFixTaskDispatcher` depend only on `FixTaskQueue`.
- [x] Preserve existing lifecycle events and status comment behavior.

## Task 4: Validation

- [x] Run focused tests for migrations, services, webhook handling, comments, dispatcher, queue, and worker.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] A second `/agent fix` for an active issue returns `ACTIVE_TASK_EXISTS`.
- [x] The existing active task id is returned and no new task is inserted.
- [x] The active task receives a timeline event for the ignored duplicate trigger.
- [x] Dispatcher behavior is queue-based.
- [x] Worker behavior still marks running, running tests, completed, and failed correctly.
- [x] No external message queue dependency is introduced.
