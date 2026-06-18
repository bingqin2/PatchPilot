# GitHub Webhook MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Receive GitHub webhooks, verify signatures, detect `/agent fix` issue comments, deduplicate delivery ids, and create an in-memory fix task.

**Architecture:** Add a focused `github.webhook` module that owns HTTP receipt, signature verification, payload routing, and delivery idempotency. Add a small `task` service with an in-memory implementation because durable persistence is a later phase. The webhook path must not clone repositories, call models, or run tests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, Spring Web, Jackson, JUnit 5, Spring Boot Test, MockMvc.

## Global Constraints

- Keep the backend as one Spring Boot module under `PatchPilot/`.
- Use package-by-domain structure from `docs/product/backend-code-standard.md`.
- Use `patchpilot.github.webhook-secret` for webhook signature configuration.
- Verify `X-Hub-Signature-256` with HMAC-SHA256 before processing payloads.
- Only `issue_comment.created` with a trimmed comment body equal to `/agent fix` creates a task.
- Non-triggering events return success and do not create tasks.
- Duplicate `X-GitHub-Delivery` values return a duplicate result and do not create a second task.
- Use in-memory task storage only; do not add database migrations in this phase.
- Do not implement repository cloning, model calls, worker execution, or Pull Request creation.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookController.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookSignatureVerifier.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/WebhookHandleResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/WebhookHandleStatus.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/InvalidWebhookPayloadException.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/bo/CreateFixTaskCommand.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskStatus.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskVo.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/common/response/ApiResponse.java`
- `PatchPilot/src/main/resources/application.properties`
- `docs/progress/execution-log.md`
- `docs/plans/001-github-webhook-mvp.md`

## Implementation Tasks

### Task 1: Add Failing Webhook Controller Tests

**Files:**

- Create: `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`

**Interfaces:**

- Produces required behavior for `POST /api/github/webhook`.
- Tests compute real HMAC signatures with secret `test-secret`.

- [x] **Step 1: Write the failing tests**

Create tests that assert:

- invalid signatures return HTTP 401 with `success=false`;
- valid non-triggering comments return `IGNORED`;
- valid `/agent fix` comments return `TASK_CREATED` and a non-empty `taskId`;
- replaying the same delivery id returns `DUPLICATE_DELIVERY`.

- [x] **Step 2: Run tests to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test
```

Expected: compilation failure because the webhook controller and task service do not exist.

### Task 2: Add Task Creation Service

**Files:**

- Create: `CreateFixTaskCommand.java`
- Create: `FixTaskStatus.java`
- Create: `FixTaskVo.java`
- Create: `FixTaskService.java`
- Create: `InMemoryFixTaskService.java`

**Interfaces:**

- `FixTaskService#createFixTask(CreateFixTaskCommand command)` returns `FixTaskVo`.
- `CreateFixTaskCommand` includes repository owner/name, issue number, installation id, trigger user, trigger comment, delivery id, and comment id.
- New tasks start with `FixTaskStatus.PENDING`.

- [x] **Step 1: Implement minimal in-memory task service**

Use `UUID.randomUUID()` for task ids and `ConcurrentHashMap<String, FixTaskVo>` for storage.

- [x] **Step 2: Run webhook tests**

Expected: still fail because the webhook controller/service do not exist.

### Task 3: Add Webhook Signature Verification and Routing

**Files:**

- Create: `GitHubWebhookSignatureVerifier.java`
- Create: `GitHubWebhookService.java`
- Create: `WebhookHandleResult.java`
- Create: `WebhookHandleStatus.java`
- Create: `InvalidWebhookPayloadException.java`
- Modify: `ApiResponse.java`
- Modify: `application.properties`

**Interfaces:**

- `GitHubWebhookSignatureVerifier#isValid(String payload, String signatureHeader)` returns `boolean`.
- `GitHubWebhookService#handle(String event, String deliveryId, String payload)` returns `WebhookHandleResult`.
- `WebhookHandleStatus` values: `IGNORED`, `TASK_CREATED`, `DUPLICATE_DELIVERY`.

- [x] **Step 1: Add `ApiResponse.fail(String message)`**

Keep existing `ok` behavior unchanged.

- [x] **Step 2: Add webhook secret configuration**

Add:

```properties
patchpilot.github.webhook-secret=${PATCHPILOT_GITHUB_WEBHOOK_SECRET:}
```

- [x] **Step 3: Implement signature verifier**

Use `Mac.getInstance("HmacSHA256")`, UTF-8 bytes, lowercase hex, `sha256=` prefix, and `MessageDigest.isEqual`.

- [x] **Step 4: Implement webhook service**

Parse payload with Jackson `JsonNode`. Ignore unsupported events, non-created actions, and comments whose trimmed body is not `/agent fix`. Store delivery results in a `ConcurrentHashMap` for idempotency.

- [x] **Step 5: Run webhook tests**

Expected: still fail until the controller is added.

### Task 4: Add Webhook HTTP Endpoint

**Files:**

- Create: `GitHubWebhookController.java`

**Interfaces:**

- `POST /api/github/webhook`
- Consumes raw JSON body.
- Reads `X-GitHub-Event`, `X-GitHub-Delivery`, and `X-Hub-Signature-256`.

- [x] **Step 1: Implement controller**

Return:

- HTTP 401 for invalid signatures;
- HTTP 400 for missing delivery id or malformed required trigger payload fields;
- HTTP 200 for ignored, duplicate, and task-created results.

- [x] **Step 2: Run webhook tests**

Expected: all `GitHubWebhookControllerTests` pass.

- [x] **Step 3: Run all backend tests from root**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn test
```

Expected: `BUILD SUCCESS`.

### Task 5: Validate Packaging and Docker Build

**Files:**

- Inspect: `PatchPilot/Dockerfile`
- Inspect: `docker-compose.yml`

- [x] **Step 1: Run root package**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn clean package
```

Expected: `BUILD SUCCESS`.

- [x] **Step 2: Build backend Docker image**

Run:

```bash
docker compose build patchpilot-backend
```

Expected: `patchpilot-backend:local Built`.

### Task 6: Update Documentation

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/001-github-webhook-mvp.md`

- [x] **Step 1: Record validation evidence**

Append the commands and actual results to the execution log.

- [x] **Step 2: Mark completed checkboxes**

Only mark a checkbox after its command or implementation step has actually completed.

## Acceptance Checklist

- [x] Invalid webhook signatures return HTTP 401.
- [x] Unsupported events return HTTP 200 with `IGNORED`.
- [x] `issue_comment.created` without `/agent fix` returns `IGNORED`.
- [x] A valid `/agent fix` comment creates exactly one in-memory task.
- [x] Duplicate delivery ids return `DUPLICATE_DELIVERY`.
- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] `docker compose build patchpilot-backend` passes.
- [x] Execution log records validation evidence.

## Verification Evidence

2026-06-18:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: passed, 5 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 7 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `docker compose build patchpilot-backend`: passed and built `patchpilot-backend:local`.
