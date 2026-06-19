# Issue Comment Feedback Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Comment back on the triggering GitHub Issue when a PatchPilot task completes or fails.

**Architecture:** Add a GitHub Issue comment client boundary that uses Java `HttpClient` and the existing `patchpilot.github.token` configuration. Wrap it in an `IssueCommentTool`, update task execution to return the created PR URL, and have the async dispatcher post a completion or failure comment after status transition. This phase does not persist comment IDs, retry comment failures, or edit previous comments.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, Java `HttpClient`, existing GitHub configuration and task dispatcher modules.

## Global Constraints

- Do not call model providers in this phase.
- Do not execute model-generated shell commands.
- Do not merge Pull Requests.
- Do not persist PR URLs or issue comment IDs in storage.
- Completion comments must include the created PR URL.
- Failure comments must include the failure reason.
- Missing GitHub token must fail explicitly before sending an HTTP request.
- GitHub tokens must not be logged or exposed in failure messages.
- Issue comment failure may fail the async task flow after task status is already updated; retries are out of scope.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/GitHubIssueCommentClient.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/CreateIssueCommentCommand.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/IssueCommentResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/GitHubIssueCommentException.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/IssueCommentTool.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/domain/FixTaskExecutionResult.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/client/GitHubIssueCommentClientTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/IssueCommentToolTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/FixTaskExecutor.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/AsyncFixTaskDispatcherTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/013-issue-comment-feedback.md`

## Task 1: Add GitHub Issue Comment Client

**Interfaces:**

- `CreateIssueCommentCommand(String owner, String repository, long issueNumber, String body)`.
- `IssueCommentResult(long id, String url)`.
- `GitHubIssueCommentClient#createIssueComment(CreateIssueCommentCommand command)` returns `IssueCommentResult`.
- The client sends `POST https://api.github.com/repos/{owner}/{repository}/issues/{issueNumber}/comments`.
- JSON payload includes `body`.
- Headers include `Authorization: Bearer <token>`, `Accept: application/vnd.github+json`, `X-GitHub-Api-Version: 2022-11-28`, and `Content-Type: application/json`.
- Success is HTTP 201 and reads `id` and `html_url` from the response body.
- Missing token throws `GitHubIssueCommentException("GitHub token is required to create Issue comments")`.
- Non-201 responses throw `GitHubIssueCommentException("GitHub issue comment creation failed: HTTP <status>")`.

- [x] Write a failing test using a fake `HttpClient` that verifies request method, URI, headers, and JSON body.
- [x] Write a failing test that missing token fails before sending a request.
- [x] Write a failing test that non-201 responses fail with an HTTP status message.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test` and verify it fails because client classes do not exist.
- [x] Add the domain records, exception, and `GitHubIssueCommentClient`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test` and verify it passes.

## Task 2: Add Issue Comment Tool Boundary

**Interfaces:**

- `IssueCommentTool#commentCompleted(FixTaskVo task, String pullRequestUrl)` returns `IssueCommentResult`.
- `IssueCommentTool#commentFailed(FixTaskVo task, String failureReason)` returns `IssueCommentResult`.
- Completion body contains `PatchPilot completed the task.`, `PR: <url>`, and `Task: <taskId>`.
- Failure body contains `PatchPilot failed the task.`, `Reason: <reason>`, and `Task: <taskId>`.
- The tool delegates to `GitHubIssueCommentClient#createIssueComment(...)`.

- [x] Write a failing test that `commentCompleted` builds the expected command from task context and PR URL.
- [x] Write a failing test that `commentFailed` builds the expected command from task context and failure reason.
- [x] Run `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test` and verify it fails because `IssueCommentTool` does not exist.
- [x] Add `IssueCommentTool`.
- [x] Run `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test` and verify it passes.

## Task 3: Return PR URL From Executor

**Interfaces:**

- `FixTaskExecutionResult(String pullRequestUrl)`.
- `FixTaskExecutor#execute(FixTaskVo task)` returns `FixTaskExecutionResult`.
- `NoopFixTaskExecutor` returns the URL from `PullRequestTool#createPullRequest(...)`.

- [x] Update `WorkspaceFixTaskExecutorTests` so successful execution asserts the returned PR URL.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor currently returns `void`.
- [x] Add `FixTaskExecutionResult` and update `FixTaskExecutor`.
- [x] Update `NoopFixTaskExecutor` to return `new FixTaskExecutionResult(pullRequestResult.url())`.
- [x] Update existing test fakes that implement `FixTaskExecutor`.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it passes.

## Task 4: Post Completion And Failure Comments

**Interfaces:**

- `AsyncFixTaskDispatcher(FixTaskService, FixTaskExecutor, IssueCommentTool)`.
- On success: mark running -> mark running tests -> execute -> mark completed -> comment completed with PR URL.
- On executor failure: mark failed -> comment failed with failure reason.

- [x] Update `AsyncFixTaskDispatcherTests` with a recording issue comment tool fake.
- [x] Add a failing assertion that success posts a completion comment after task completion.
- [x] Add a failing assertion that executor failure posts a failure comment after task failure.
- [x] Run `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test` and verify it fails because the dispatcher is not wired.
- [x] Inject `IssueCommentTool` into `AsyncFixTaskDispatcher`.
- [x] Call `commentCompleted(...)` after `markCompleted(...)`.
- [x] Call `commentFailed(...)` after `markFailed(...)`.
- [x] Run `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test` and verify it passes.

## Task 5: Validate And Document

- [x] Add a primary fake `IssueCommentTool` to `GitHubWebhookControllerTests`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests,IssueCommentToolTests,WorkspaceFixTaskExecutorTests,AsyncFixTaskDispatcherTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Successful tasks comment on the original Issue with the PR URL.
- [x] Failed tasks comment on the original Issue with the failure reason.
- [x] Completion comments happen only after task status is `COMPLETED`.
- [x] Failure comments happen only after task status is `FAILED`.
- [x] Missing GitHub token fails clearly without exposing secrets.
- [x] Existing webhook tests do not call the real GitHub Issue comment API.
- [x] Full Maven verification passes.
