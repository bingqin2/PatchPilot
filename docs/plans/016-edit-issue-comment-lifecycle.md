# Edit Issue Comment Lifecycle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Use one PatchPilot-owned Issue comment as the live task status surface, editing it through task lifecycle transitions.

**Architecture:** Persist the GitHub status comment id and URL on each fix task. Extend the GitHub Issue comment client with PATCH support, add lifecycle-specific comment rendering in `IssueCommentTool`, and wire webhook task creation plus async task transitions to update the same comment. Comment API failures must not change the durable task status.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ, Java `HttpClient`.

## Global Constraints

- Do not create multiple PatchPilot status comments for one task.
- Do not reuse the trigger comment id as the PatchPilot status comment id.
- Duplicate webhook deliveries must not create or update another status comment.
- If status comment creation fails, the task still dispatches and runs.
- If status comment updates fail, task status remains authoritative and is not rolled back.
- New database schema names use snake_case.
- Existing task API fields remain compatible; new fields are additive.
- GitHub tokens must not be logged or exposed in failure messages.

---

## Target Files

Create:

- `PatchPilot/src/main/resources/db/migration/V3__add_fix_task_status_comment.sql`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/mapper/FixTaskStatusCommentMigrationTests.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/UpdateIssueCommentCommand.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskVo.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/entity/FixTaskEntity.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/convert/FixTaskConvert.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/GitHubIssueCommentClient.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/IssueCommentTool.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- Relevant tests under `PatchPilot/src/test/java`
- `docs/progress/execution-log.md`

## Task 1: Persist Status Comment Metadata

**Interfaces:**

- Migration path: `PatchPilot/src/main/resources/db/migration/V3__add_fix_task_status_comment.sql`.
- Add nullable columns to `fix_task`: `status_comment_id bigint null`, `status_comment_url varchar(2048) null`.
- `FixTaskVo` adds `Long statusCommentId`, `String statusCommentUrl`.
- `FixTaskService#attachStatusComment(String id, long commentId, String commentUrl)` stores the PatchPilot-owned status comment metadata.

- [x] Write a failing migration test for V3 status comment columns.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskStatusCommentMigrationTests test` and verify it fails because V3 does not exist.
- [x] Add V3 migration.
- [x] Extend DTO, entity, converter, and services.
- [x] Add service tests for `attachStatusComment(...)`.
- [x] Run converter and service tests.

## Task 2: Add GitHub Comment PATCH Client

**Interfaces:**

- `UpdateIssueCommentCommand(String owner, String repository, long commentId, String body)`.
- `GitHubIssueCommentClient#updateIssueComment(UpdateIssueCommentCommand command)` returns `IssueCommentResult`.
- Sends `PATCH https://api.github.com/repos/{owner}/{repository}/issues/comments/{commentId}` with JSON `body`.
- Success is HTTP 200.
- Missing token throws `GitHubIssueCommentException("GitHub token is required to update Issue comments")`.
- Non-200 responses throw `GitHubIssueCommentException("GitHub issue comment update failed: HTTP <status>")`.

- [x] Write failing client tests for PATCH request, missing token, and non-200 response.
- [x] Add command record and client method.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`.

## Task 3: Render Lifecycle Comment Bodies

**Interfaces:**

- `IssueCommentTool#commentAccepted(FixTaskVo task)` creates the status comment.
- `IssueCommentTool#updateRunning(FixTaskVo task)`, `updateRunningTests(FixTaskVo task)`, `updateCompleted(FixTaskVo task)`, and `updateFailed(FixTaskVo task)` update `task.statusCommentId()`.
- Update methods are no-ops returning `Optional.empty()` when no status comment id exists.
- Lifecycle bodies include task id, repository, issue number, trigger user, status, and PR URL or failure reason when terminal.

- [x] Write failing IssueCommentTool tests for create and update methods.
- [x] Update the tool to render lifecycle bodies and delegate to create/PATCH client methods.
- [x] Run `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`.

## Task 4: Wire Webhook And Dispatcher

**Interfaces:**

- Webhook creates and saves the initial status comment after task creation and before dispatch.
- Duplicate delivery returns duplicate without creating or updating a status comment.
- Dispatcher updates the same comment after `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, and `FAILED` transitions.
- Comment update failures do not trigger failure status rewrites.

- [x] Add webhook service tests for accepted-comment creation, save, failure tolerance, and duplicate behavior.
- [x] Add dispatcher tests for lifecycle update sequence and update failure tolerance.
- [x] Wire `IssueCommentTool` into `GitHubWebhookService`.
- [x] Update `AsyncFixTaskDispatcher`.
- [x] Run webhook and dispatcher tests.

## Task 5: Validate And Document

- [x] Run focused tests for migration, converter, services, GitHub client, tool, webhook, dispatcher, and task controller.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] One PatchPilot-owned Issue comment is created per new task.
- [x] The same comment is updated for running, testing, completed, and failed statuses.
- [x] Status comment id and URL persist in both in-memory and MySQL-backed services.
- [x] Duplicate deliveries do not create extra status comments.
- [x] Comment API failures do not corrupt task state.
- [x] Full Maven verification passes.
