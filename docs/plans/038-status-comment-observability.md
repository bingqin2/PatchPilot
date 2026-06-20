# Status Comment Observability Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make GitHub issue status-comment failures visible in task timeline without blocking task execution.

**Architecture:** Keep GitHub comment operations best-effort, but record a timeline event whenever status comment creation or update fails. Reuse the existing timeline API and failure truncation behavior so operators can diagnose missing issue comments from the task detail endpoints.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, MyBatis-Plus, MySQL 8.4.

## Global Constraints

- Do not fail or roll back tasks when GitHub comment feedback fails.
- Do not expose GitHub tokens or secrets in timeline messages.
- Keep new timeline events additive and API-compatible.
- Keep documentation aligned with the permissions needed for issue comments.

---

## Tasks

- [x] Add a `STATUS_COMMENT_FAILED` timeline event and record it when accepted-comment creation fails.
- [x] Record `STATUS_COMMENT_FAILED` when lifecycle comment updates fail in the worker.
- [x] Update setup and smoke-test docs to require `Issues: Read and write`.
- [x] Run focused tests and the full backend test suite.
- [x] Record validation evidence in `docs/progress/execution-log.md`.

## Acceptance Criteria

- A task still dispatches when status comment creation fails.
- The task timeline records a clear status-comment failure message.
- A task still reaches its durable terminal state when status comment update fails.
- The task timeline records update failure details.
- README and smoke checklist list GitHub issue comment permissions.
