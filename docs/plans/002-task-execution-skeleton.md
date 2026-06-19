# Task Execution Skeleton Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a testable asynchronous task execution skeleton that moves webhook-created fix tasks from `PENDING` to terminal statuses.

**Architecture:** Keep task storage in memory for this phase. `FixTaskService` owns task creation and status transitions, `FixTaskDispatcher` schedules execution after task creation, and `FixTaskExecutor` is a narrow interface that later phases can replace with real workspace, agent, test, and PR orchestration.

**Tech Stack:** Java 17, Spring Boot 3.5.x, Spring Web, JUnit 5, Spring Boot Test, MockMvc.

## Global Constraints

- Keep the backend as one Spring Boot module under `PatchPilot/`.
- Follow package-by-domain structure from `docs/product/backend-code-standard.md`.
- Use in-memory task storage only; do not add database migrations in this phase.
- Do not clone repositories, call model providers, run Maven commands, push branches, or create Pull Requests in this phase.
- Status transitions must go through `FixTaskService`; controllers and webhook handlers must not mutate task state directly.
- The execution skeleton must be replaceable by real workspace/agent/GitHub implementation later.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/FixTaskExecutor.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskDispatcher.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/AsyncFixTaskDispatcherTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskStatus.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskVo.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/002-task-execution-skeleton.md`

## Implementation Tasks

### Task 1: Add Task Status Transition Service

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskStatus.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskVo.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`

**Interfaces:**

- `FixTaskStatus` values: `PENDING`, `RUNNING`, `COMPLETED`, `FAILED`.
- `FixTaskVo` includes `failureReason` after `status` and before `createdAt`.
- `FixTaskService#markRunning(String id)` returns `FixTaskVo`.
- `FixTaskService#markCompleted(String id)` returns `FixTaskVo`.
- `FixTaskService#markFailed(String id, String failureReason)` returns `FixTaskVo`.

- [x] **Step 1: Write failing tests**

Add tests that create a task, mark it running, mark it completed, mark another task failed, and verify missing task ids throw `IllegalArgumentException`.

- [x] **Step 2: Run tests to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test
```

Expected: compilation failure because transition methods and statuses do not exist.

- [x] **Step 3: Implement minimal transition support**

Add the new statuses, add `failureReason` to `FixTaskVo`, and update `InMemoryFixTaskService` by replacing immutable records in the map for each status transition.

- [x] **Step 4: Run tests to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 2: Add Async Dispatcher and Noop Executor

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/FixTaskExecutor.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskDispatcher.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/AsyncFixTaskDispatcherTests.java`

**Interfaces:**

- `FixTaskExecutor#execute(FixTaskVo task)` returns `void`.
- `FixTaskDispatcher#dispatch(String taskId)` returns `void`.
- `AsyncFixTaskDispatcher#dispatch` starts asynchronous work with `CompletableFuture.runAsync`.
- Dispatcher flow: find task, mark running, execute, mark completed; on exception, mark failed with the exception message.

- [x] **Step 1: Write failing dispatcher tests**

Add tests with a recording executor that verify a dispatched task becomes `COMPLETED`, the executor sees the task id, and executor failure marks the task `FAILED` with a failure reason.

- [x] **Step 2: Run tests to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test
```

Expected: compilation failure because dispatcher and executor classes do not exist.

- [x] **Step 3: Implement dispatcher and noop executor**

Create the interfaces and a `NoopFixTaskExecutor` annotated with `@Service`. Implement `AsyncFixTaskDispatcher` as a Spring service using injected `FixTaskService` and `FixTaskExecutor`.

- [x] **Step 4: Run tests to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 3: Dispatch Webhook-Created Tasks

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`

**Interfaces:**

- `GitHubWebhookService` depends on `FixTaskDispatcher`.
- After `fixTaskService.createFixTask(...)`, call `fixTaskDispatcher.dispatch(task.id())`.
- Duplicate deliveries must not dispatch a second time.

- [x] **Step 1: Add failing webhook dispatch test**

Add a test that posts a valid `/agent fix` webhook, then polls `/api/tasks` until the created task reaches `COMPLETED`.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test
```

Expected: failure because created tasks remain `PENDING`.

- [x] **Step 3: Wire dispatcher into webhook service**

Inject `FixTaskDispatcher` into `GitHubWebhookService` and dispatch only newly created trigger tasks.

- [x] **Step 4: Run tests to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 4: Validate, Package, and Document

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/002-task-execution-skeleton.md`

- [x] **Step 1: Run all tests**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn test
```

Expected: `BUILD SUCCESS`.

- [x] **Step 2: Run package**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn clean package
```

Expected: `BUILD SUCCESS`.

- [x] **Step 3: Rebuild and restart Docker backend**

Run:

```bash
PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d
```

Expected: backend container is running on port `8080`.

- [x] **Step 4: Verify runtime endpoint**

Run:

```bash
curl -sS http://127.0.0.1:8080/api/tasks
```

Expected: successful API response.

- [x] **Step 5: Update docs**

Record validation evidence in `docs/progress/execution-log.md` and mark completed checkboxes in this plan.

## Acceptance Checklist

- [x] New tasks still start as `PENDING`.
- [x] Dispatcher moves successful tasks through `RUNNING` to `COMPLETED`.
- [x] Dispatcher marks executor exceptions as `FAILED` and records `failureReason`.
- [x] Webhook-created `/agent fix` tasks are dispatched automatically.
- [x] Duplicate webhook deliveries do not dispatch duplicate task executions.
- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] Docker backend rebuilds and starts.
