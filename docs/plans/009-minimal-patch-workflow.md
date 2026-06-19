# Minimal Patch Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let a fix task produce one safe, deterministic local patch before Maven verification.

**Architecture:** Add a small `agent.workflow` boundary that receives a prepared repository and task context, applies only an allowlisted deterministic instruction, and returns whether a patch was applied. Wire the workflow into the existing task executor before diff and Maven tests. This phase does not call model providers, push branches, create commits, or open Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, existing file and diff tools.

## Global Constraints

- Do not call model providers in this phase.
- Do not execute model-generated shell commands.
- Do not push branches, commit changes, or create Pull Requests.
- File writes must go through `FileWriteTool` and repository-relative path validation.
- Only apply patches for an explicit `touch <relative-path>` instruction embedded in the trigger comment.
- Continue Maven verification after patch workflow execution.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/agent/workflow/PatchWorkflow.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/workflow/SimplePatchWorkflow.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/workflow/domain/PatchWorkflowResult.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/workflow/SimplePatchWorkflowTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/009-minimal-patch-workflow.md`

## Task 1: Add Deterministic Patch Workflow

**Interfaces:**

- `PatchWorkflow#apply(FixTaskVo task, Path repositoryDir)` returns `PatchWorkflowResult`.
- `PatchWorkflowResult(boolean patchApplied, String summary)`.
- `SimplePatchWorkflow` supports trigger comments containing `touch <relative-path>`.

- [x] Write failing tests for `SimplePatchWorkflow` that verify `touch docs/demo.md` writes a deterministic file through the repository path guard.
- [x] Add a failing test that blank or missing `touch` instructions return `patchApplied=false`.
- [x] Add a failing test that unsafe paths such as `touch ../outside.md` are rejected by the existing path guard.
- [x] Run `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests test` and verify it fails because workflow classes do not exist.
- [x] Add `PatchWorkflow`, `PatchWorkflowResult`, and `SimplePatchWorkflow`.
- [x] Run the targeted workflow tests and verify they pass.

## Task 2: Accept Patch Instructions From Webhook Comments

**Behavior:**

- `/agent fix` remains valid.
- `/agent fix <instruction>` is valid when the comment starts with the exact command followed by whitespace.
- Non-triggering comments remain ignored.

- [x] Add a failing webhook test for `/agent fix touch docs/demo.md`.
- [x] Verify the test fails with `IGNORED` before the service change.
- [x] Update `GitHubWebhookService` trigger matching to accept command-prefixed instructions.
- [x] Run the targeted webhook test and verify it passes.

## Task 3: Wire Workflow Into Executor

**Interfaces:**

- `NoopFixTaskExecutor(WorkspaceService, MavenTestRunner, PatchWorkflow, DiffTool)`.
- Executor order: prepare repository -> apply patch workflow -> inspect diff -> run Maven tests.

- [x] Update `WorkspaceFixTaskExecutorTests` with recording workflow and diff tool fakes.
- [x] Add a failing assertion that workflow receives the prepared repository path before Maven tests run.
- [x] Add a failing assertion that diff is inspected before Maven tests run.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor is not wired.
- [x] Inject `PatchWorkflow` and `DiffTool` into `NoopFixTaskExecutor`.
- [x] Call workflow and diff before `MavenTestRunner#runTests(...)`.
- [x] Run the targeted executor tests and verify they pass.

## Task 4: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests test`.
- [x] Run `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] A task can apply a deterministic local patch from `touch <relative-path>`.
- [x] Unsafe patch paths are rejected.
- [x] Webhook comments can trigger patch instructions with `/agent fix touch <relative-path>`.
- [x] Executor inspects diff after patch workflow execution.
- [x] Maven verification still runs after patch workflow execution.
- [x] Full Maven verification passes.
