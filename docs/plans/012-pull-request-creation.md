# Pull Request Creation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Automatically create a GitHub Pull Request after a task branch has been pushed successfully.

**Architecture:** Add a small GitHub client boundary that uses Java `HttpClient` and the existing `patchpilot.github.token` configuration to call GitHub's Pull Request API. Wrap the client in a `PullRequestTool`, then wire task execution to create a PR only after patch, diff, Maven verification, local commit, and branch push succeed. This phase does not comment on issues, persist PR URLs, or merge PRs.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, Java `HttpClient`, existing task executor and GitHub configuration modules.

## Global Constraints

- Do not call model providers in this phase.
- Do not execute model-generated shell commands.
- Do not merge Pull Requests.
- Do not create issue comments in this phase.
- PR creation must happen only after Maven verification, local commit, and remote branch push succeed.
- Missing GitHub token must fail explicitly before sending an HTTP request.
- GitHub tokens must not be logged or exposed in failure messages.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/GitHubPullRequestClient.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/CreatePullRequestCommand.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/PullRequestResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/client/domain/GitHubPullRequestException.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/PullRequestTool.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/client/GitHubPullRequestClientTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/PullRequestToolTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/012-pull-request-creation.md`

## Task 1: Add GitHub Pull Request Client

**Interfaces:**

- `CreatePullRequestCommand(String owner, String repository, String head, String base, String title, String body)`.
- `PullRequestResult(String url)`.
- `GitHubPullRequestClient#createPullRequest(CreatePullRequestCommand command)` returns `PullRequestResult`.
- The client sends `POST https://api.github.com/repos/{owner}/{repository}/pulls`.
- JSON payload includes `title`, `head`, `base`, and `body`.
- Headers include `Authorization: Bearer <token>`, `Accept: application/vnd.github+json`, and `X-GitHub-Api-Version: 2022-11-28`.
- Success is HTTP 201 and reads `html_url` from the response body.
- Missing token throws `GitHubPullRequestException("GitHub token is required to create Pull Requests")`.
- Non-201 responses throw `GitHubPullRequestException("GitHub pull request creation failed: HTTP <status>")`.

- [x] Write a failing test using a fake `HttpClient` that verifies request method, URI, headers, and JSON body.
- [x] Write a failing test that missing token fails before sending a request.
- [x] Write a failing test that non-201 responses fail with an HTTP status message.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests test` and verify it fails because client classes do not exist.
- [x] Add the domain records, exception, and `GitHubPullRequestClient`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests test` and verify it passes.

## Task 2: Add Pull Request Tool Boundary

**Interfaces:**

- `PullRequestTool#createPullRequest(FixTaskVo task, String branchName)` returns `PullRequestResult`.
- The tool creates:
  - title: `PatchPilot fix for #<issueNumber>`
  - head: `<repositoryOwner>:<branchName>`
  - base: `main`
  - body containing the issue number, trigger user, and branch name.
- It delegates to `GitHubPullRequestClient#createPullRequest(...)`.

- [x] Write a failing test that `PullRequestTool` builds the expected command from task context and branch name.
- [x] Run `mvn -pl PatchPilot -Dtest=PullRequestToolTests test` and verify it fails because `PullRequestTool` does not exist.
- [x] Add `PullRequestTool`.
- [x] Run `mvn -pl PatchPilot -Dtest=PullRequestToolTests test` and verify it passes.

## Task 3: Wire PR Creation After Push

**Interfaces:**

- `NoopFixTaskExecutor(WorkspaceService, MavenTestRunner, PatchWorkflow, DiffTool, CommitTool, PushTool, PullRequestTool)`.
- Executor order: prepare repository -> apply patch workflow -> inspect diff -> run Maven tests -> commit all changes -> push prepared branch -> create PR.
- Use `preparedWorkspace.branchName()` as the PR head branch.

- [x] Update `WorkspaceFixTaskExecutorTests` with a recording PR tool fake.
- [x] Add a failing assertion that PR creation runs after push succeeds.
- [x] Add a failing assertion that PR creation is not called when Maven tests fail.
- [x] Add a failing assertion that PR creation is not called when commit fails.
- [x] Add a failing assertion that PR creation is not called when push fails.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor is not wired.
- [x] Inject `PullRequestTool` into `NoopFixTaskExecutor`.
- [x] Call `createPullRequest(task, preparedWorkspace.branchName())` only after `PushTool#pushBranch(...)`.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it passes.

## Task 4: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests,PullRequestToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Successful tasks create a GitHub Pull Request after branch push.
- [x] PR creation happens after push, not before.
- [x] Maven test failures do not create PRs.
- [x] Commit failures do not create PRs.
- [x] Push failures do not create PRs.
- [x] Missing GitHub token fails clearly without exposing secrets.
- [x] Full Maven verification passes.
