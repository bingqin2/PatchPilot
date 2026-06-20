# Execution Log

This file records dated implementation progress, validation commands, and important outcomes.

## 2026-06-18

Initialized the PatchPilot documentation baseline.

Created documentation categories following the reference layout:

- `docs/product`
- `docs/plans`
- `docs/progress`
- `docs/agent`
- `docs/superpowers`

Initial product documents:

- `docs/product/spec.md`
- `docs/product/architecture.md`
- `docs/product/backend-code-standard.md`
- `docs/product/target-state.md`
- `docs/product/roadmap.md`
- `docs/product/milestones.md`

Initial progress documents:

- `docs/progress/decisions.md`
- `docs/progress/execution-log.md`

Initial plan:

- `docs/plans/000-project-foundation.md`

Validation is pending until documents are accepted and copied into the project docs directory.

## 2026-06-18

Implemented the basic backend foundation from `docs/plans/001-basic-version-implementation.md`.

Changes:

- Added Spring Web, Validation, Actuator, MySQL Connector/J, Flyway, and MyBatis-Plus backend dependencies.
- Added base, local, and docker Spring profile configuration.
- Added structured `ApiResponse` and custom `/health` endpoint.
- Added MockMvc coverage for `/health`.
- Added test-only Mockito subclass mock maker configuration because the local macOS/JDK environment cannot self-attach Mockito's inline mock maker.
- Added test-only default datasource auto-configuration exclusion so root tests do not require a MySQL service before persistence migrations exist.

Validation:

- `mvn test` from repository root with Java 17: passed, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=HealthControllerTests test` with Java 17: first failed because `HealthController` did not exist, then passed after implementation.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `docker compose config`: passed.
- `docker compose build patchpilot-backend`: initially failed while resolving `maven:3.9-eclipse-temurin-17` and `eclipse-temurin:17-jre` through `https://docker.mirrors.ustc.edu.cn` with EOF. After the base images were pulled successfully, rerunning the command passed and built `patchpilot-backend:local`.

## 2026-06-18

Implemented the GitHub webhook MVP from `docs/plans/001-github-webhook-mvp.md`.

Changes:

- Added `POST /api/github/webhook` with HMAC-SHA256 validation for `X-Hub-Signature-256`.
- Added webhook routing for `issue_comment.created` comments whose trimmed body is exactly `/agent fix`.
- Added in-memory delivery idempotency using `X-GitHub-Delivery`.
- Added in-memory fix task creation with `PENDING` task status.
- Added `patchpilot.github.webhook-secret=${PATCHPILOT_GITHUB_WEBHOOK_SECRET:}` configuration.
- Added MockMvc coverage for invalid signatures, unsupported events, non-triggering comments, task creation, and duplicate deliveries.
- Updated `ApiResponse` with `fail(String message)` for error responses.

Notes:

- The initial webhook test was intentionally failing before implementation because the controller and task service did not exist.
- After adding the controller, `@WebMvcTest` failed to load service dependencies; the test was changed to `@SpringBootTest` with `@AutoConfigureMockMvc` so the real verifier, router, and in-memory service are exercised.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: passed, 5 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 7 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `docker compose build patchpilot-backend`: passed, built `patchpilot-backend:local`.

## 2026-06-18

Fixed local GitHub repository webhook delivery returning HTTP 400.

Changes:

- Made `installation.id` optional for repository-level GitHub webhooks. GitHub App deliveries include `installation`, but repository webhooks can omit it.
- Added regression coverage for `/agent fix` issue comments without an `installation` object.
- Removed the host `3306:3306` MySQL port mapping from `docker-compose.yml` so PatchPilot does not conflict with other local MySQL containers. The backend still reaches MySQL through the compose network at `mysql:3306`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: first failed with `Missing field: installation.id`, then passed after the fix, 6 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 8 tests run, 0 failures, 0 errors.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up -d`: passed after removing the host MySQL port mapping.
- `curl http://127.0.0.1:8080/health`: returned `UP`.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/health`: returned `UP`.

## 2026-06-18

Added task query endpoints for webhook observability.

Changes:

- Added `GET /api/tasks` to list in-memory fix tasks, newest first.
- Added `GET /api/tasks/{id}` to return one in-memory fix task or a 404 API response.
- Extended `FixTaskService` with `listTasks()` and `findTask(String id)`.
- Added MockMvc coverage for listing tasks, fetching a task by id, and missing task responses.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test` with Java 17: first failed because `/api/tasks` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 11 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `curl http://127.0.0.1:8080/api/tasks`: returned an empty task list API response.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/api/tasks`: returned an empty task list API response through the Cloudflare temporary URL.

## 2026-06-18

Implemented the task execution skeleton from `docs/plans/002-task-execution-skeleton.md`.

Changes:

- Added task status transitions for `PENDING`, `RUNNING`, `COMPLETED`, and `FAILED`.
- Added `failureReason` to task API responses for failed executions.
- Added `FixTaskExecutor` and a no-op executor implementation as the replaceable execution boundary.
- Added `FixTaskDispatcher` and an asynchronous dispatcher that marks tasks running, completed, or failed.
- Wired `/agent fix` webhook-created tasks into the dispatcher so new tasks no longer remain permanently pending.
- Added unit and MockMvc coverage for status transitions, dispatcher success/failure behavior, and webhook dispatch completion.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test` with Java 17: first failed because status transition methods, new statuses, and `failureReason` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test` with Java 17: first failed because dispatcher and executor classes did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: first failed because created tasks stayed `PENDING`, then passed after wiring the dispatcher, 7 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 17 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned a successful API response.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/api/tasks`: returned a successful API response through the Cloudflare temporary URL.
- A signed local `POST /api/github/webhook` with `/agent fix` returned `TASK_CREATED`, and polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.

## 2026-06-18

Implemented the workspace clone MVP from `docs/plans/003-workspace-clone-mvp.md`.

Changes:

- Added a `workspace` module with `WorkspaceService`, task-scoped clone commands/results, and `GitWorkspaceService`.
- Added `GitCommandRunner` using controlled `git clone --depth 1 <url> <target>` arguments via `ProcessBuilder`.
- Added optional `PATCHPILOT_GITHUB_TOKEN` configuration through `patchpilot.github.token` for private repository clone URLs, with token redaction in command output.
- Replaced the no-op task executor behavior so webhook-created tasks clone the target repository before completing.
- Added `PATCHPILOT_WORKSPACE_ROOT_DIR` and `PATCHPILOT_GITHUB_TOKEN` compose environment entries.
- Updated the runtime Docker image to install `git` and `ca-certificates`.
- Isolated webhook controller tests from real network clone by providing a test `WorkspaceService`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test` with Java 17: first failed because the workspace classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` with Java 17: first failed because the executor constructor and clone call were missing, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first exposed the blank-token output sanitization bug, then passed after fixing it, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: passed, 7 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 23 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `docker exec patchpilot-backend git --version`: returned `git version 2.53.0`.
- A signed local `POST /api/github/webhook` with `/agent fix` against public repository `octocat/Hello-World` returned `TASK_CREATED`; polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.

## 2026-06-18

Implemented the workspace branch MVP from `docs/plans/004-workspace-branch-mvp.md`.

Changes:

- Added `PreparedWorkspaceResult` to return task id, workspace path, repository path, and branch name.
- Added `WorkspaceService#prepareRepository(...)` to clone the repository and create a task branch.
- Added `GitCommandRunner#createBranch(...)` using controlled `git -C <repo> checkout -b <branch>` arguments.
- Changed the task executor to call workspace preparation instead of clone-only execution.
- Updated webhook controller tests with a concrete `WorkspaceService` fake because the service interface now has multiple methods.
- Kept this phase local-only: no file edits, Maven test execution in cloned repositories, pushes, or Pull Requests.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test` with Java 17: first failed because `PreparedWorkspaceResult`, `prepareRepository`, and `createBranch` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` with Java 17: first failed because the executor still called `cloneRepository`, then passed after switching it to `prepareRepository`, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first failed because `createBranch` did not switch the branch and did not reject blank names, then passed after implementing the fixed git command, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test` with Java 17: passed, 13 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 27 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- A signed local `POST /api/github/webhook` with `/agent fix` against public repository `octocat/Hello-World` returned `TASK_CREATED`; polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.
- `docker exec patchpilot-backend git -C /tmp/patchpilot/workspaces/<taskId>/repo branch --show-current`: returned `patchpilot/<taskId>` for task `0aa0e1e7-888b-43fa-848b-311c717d8418`.

## 2026-06-18

Implemented the controlled file tools MVP from `docs/plans/005-file-tools-mvp.md`.

Changes:

- Added `WorkspacePathResolver` to resolve repository-relative paths and reject blank, absolute, and traversal inputs.
- Added `FileReadTool` for UTF-8 file reads inside a repository workspace.
- Added `FileWriteTool` for UTF-8 file writes inside a repository workspace, including parent directory creation.
- Added `GitCommandRunner#diff(...)` using controlled `git -C <repo> diff --` arguments.
- Added `DiffTool` to expose git diff output and fail explicitly on non-zero git exit.
- Kept these tools internal only; no model integration, HTTP API, Maven execution, push, or Pull Request creation was added in this phase.

Validation:

- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests test` with Java 17: first failed because `WorkspacePathResolver` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FileToolsTests test` with Java 17: first failed because `FileReadTool` and `FileWriteTool` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first failed because `GitCommandRunner#diff(...)` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=DiffToolTests test` with Java 17: first failed because `DiffTool` did not exist, then passed after implementation.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests,FileToolsTests,GitCommandRunnerTests,DiffToolTests test` with Java 17: passed, 15 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 38 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned a successful API response with an empty in-memory task list after restart.

## 2026-06-18

Implemented the Maven test runner MVP from `docs/plans/006-maven-test-runner-mvp.md`.

Changes:

- Added `TestRunResult` to return the executed command, exit code, and combined command output.
- Added `MavenTestRunner` under the `runner` module.
- Detects Maven wrapper repositories and runs only `./mvnw test`.
- Falls back to `mvn test` when only `pom.xml` exists.
- Fails explicitly for unsupported repositories without `mvnw` or `pom.xml`.
- Captures non-zero test exits without throwing away output.
- Enforces a command timeout and returns exit code `124` for timed out runs.
- Kept this phase internal only: no task executor wiring, model integration, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: first failed because `MavenTestRunner` and `TestRunResult` did not exist, then passed after implementation, 6 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 44 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned `{"success":true,"data":[],"message":null}` after restart.

## 2026-06-18

Integrated Maven test execution into task execution from `docs/plans/007-task-test-runner-integration.md`.

Changes:

- Added `RUNNING_TESTS` task status.
- Added `FixTaskService#markRunningTests(...)` and the in-memory implementation.
- Updated the async dispatcher to transition tasks from `RUNNING` to `RUNNING_TESTS` before calling the executor.
- Updated `NoopFixTaskExecutor` to prepare the repository and then call `MavenTestRunner` on the prepared repository directory.
- Made non-zero Maven test exits fail task execution with `maven tests failed: <output>`.
- Isolated webhook controller tests from real Maven execution by providing a primary test `MavenTestRunner`.
- Added a Dockerfile packaging test to ensure the backend runtime image includes Maven on the Java 17 Maven base image.
- Updated the backend runtime Docker stage to use `maven:3.9-eclipse-temurin-17` and avoid installing a second JDK through apt.
- Kept this phase local-only: no model integration, file edits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test`: first failed because `markRunningTests` and `RUNNING_TESTS` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because `NoopFixTaskExecutor` did not accept or call `MavenTestRunner`, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because the dispatcher transitioned directly from `RUNNING` to `COMPLETED`, then passed after adding `RUNNING_TESTS`, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real Maven runner against a fake non-Maven workspace, then passed after adding a test runner bean, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests test`: first failed because the runtime image did not guarantee Maven availability, then passed after switching the runtime stage to the Java 17 Maven image.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,AsyncFixTaskDispatcherTests,WorkspaceFixTaskExecutorTests test`: passed, 9 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests,MavenTestRunnerTests,InMemoryFixTaskServiceTests,AsyncFixTaskDispatcherTests,WorkspaceFixTaskExecutorTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 48 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `docker exec patchpilot-backend mvn -version`: returned Apache Maven 3.9.16 running on Java 17.0.19.
- `docker exec patchpilot-backend java -version`: returned Temurin 17.0.19.
- `curl http://127.0.0.1:8080/health`: returned `UP` from the backend after container startup.
- `curl http://127.0.0.1:8080/api/tasks`: returned `{"success":true,"data":[],"message":null}` after restart.

## 2026-06-18

Implemented repository inspection tools from `docs/plans/008-repository-inspection-tools.md`.

Changes:

- Added `RepositoryFileScanner` to produce sorted repository-relative file lists while skipping noisy/generated directories.
- Added `RepoTreeTool` to return a bounded newline-separated repository file tree.
- Added `CodeSearchTool` to return bounded `path:line: text` matches using Java filesystem APIs and UTF-8 reads.
- Added coverage for sorted tree output, skipped directories, search match formatting, blank query rejection, and search result limiting.
- Kept this phase internal and read-only: no model integration, file edits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test`: first failed because `RepoTreeTool`, `RepositoryFileScanner`, and `CodeSearchTool` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 53 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented the minimal deterministic patch workflow from `docs/plans/009-minimal-patch-workflow.md`.

Changes:

- Added `PatchWorkflow`, `PatchWorkflowResult`, and `SimplePatchWorkflow`.
- Added deterministic support for trigger comments containing `touch <relative-path>`.
- Wrote generated files through `FileWriteTool`, preserving repository-relative path validation.
- Updated webhook trigger matching so `/agent fix` and `/agent fix <instruction>` both create tasks.
- Wired task execution order as repository preparation -> patch workflow -> diff inspection -> Maven tests.
- Isolated webhook controller tests from real patch/diff side effects with primary test beans.
- Kept this phase local-only: no model provider calls, commits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests test`: first failed because workflow classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor was not wired for workflow and diff, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests#should_create_task_for_agent_fix_issue_comment_with_patch_instruction test`: first failed with `IGNORED`, then passed after accepting command-prefixed instructions.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: passed, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 57 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 57 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented the local commit workflow from `docs/plans/010-local-commit-workflow.md`.

Changes:

- Added controlled `GitCommandRunner#stageAll(...)` using `git -C <repo> add --all`.
- Added controlled `GitCommandRunner#commit(...)` using `git -C <repo> commit -m <message>`.
- Added `CommitTool` to stage and commit all repository changes with clear failure messages.
- Updated task execution order to commit only after patch workflow, diff inspection, and successful Maven verification.
- Ensured Maven test failures do not create commits.
- Isolated webhook controller tests from real commit side effects with a primary test `CommitTool`.
- Kept this phase local-only: no branch push, GitHub API calls, issue comments, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because `stageAll` and `commit` did not exist, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommitToolTests test`: first failed because `CommitTool` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `CommitTool`, then passed after wiring commit after Maven verification, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real commit tool against a fake workspace, then passed after adding a test commit tool bean, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 21 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 63 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 63 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented remote task branch push from `docs/plans/011-remote-branch-push.md`.

Changes:

- Added controlled `GitCommandRunner#pushBranch(...)` using `git -C <repo> push origin HEAD:<branch>`.
- Added `PushTool` to wrap push execution and surface clear failure messages.
- Updated task execution order to push the prepared task branch only after patch workflow, diff inspection, Maven verification, and local commit succeed.
- Ensured Maven test failures and commit failures do not push.
- Isolated webhook controller tests from real push side effects with a primary test `PushTool`.
- Kept this phase limited to branch push: no GitHub REST API calls, Pull Requests, or issue comments were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because `pushBranch` did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PushToolTests test`: first failed because `PushTool` did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `PushTool`, then passed after wiring push after commit, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real push tool against a fake workspace, then passed after adding a test push tool bean, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,PushToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 23 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 68 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 68 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented Pull Request creation from `docs/plans/012-pull-request-creation.md`.

Changes:

- Added `GitHubPullRequestClient` for GitHub Pull Request API calls using Java `HttpClient`.
- Added `CreatePullRequestCommand`, `PullRequestResult`, and `GitHubPullRequestException`.
- Added `PullRequestTool` to build PR title, head branch, base branch, and body from task context.
- Updated task execution order to create a PR only after patch workflow, diff inspection, Maven verification, local commit, and branch push succeed.
- Ensured Maven test failures, commit failures, and push failures do not create PRs.
- Ensured missing GitHub tokens fail clearly before any HTTP request and without exposing secrets.
- Isolated webhook controller tests from real GitHub API calls with a primary test `PullRequestTool`.
- Kept this phase limited to PR creation: no issue comments, PR URL persistence, merge behavior, or model provider calls were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests test`: first failed because PR client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PullRequestToolTests test`: first failed because `PullRequestTool` did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `PullRequestTool`, then passed after wiring PR creation after push, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests,PullRequestToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: first failed because Spring could not choose a `GitHubPullRequestClient` constructor, then passed after marking the production constructor for injection, 16 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 73 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 73 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented Issue comment feedback from `docs/plans/013-issue-comment-feedback.md`.

Changes:

- Added `GitHubIssueCommentClient` for GitHub Issue comment API calls using Java `HttpClient`.
- Added `CreateIssueCommentCommand`, `IssueCommentResult`, and `GitHubIssueCommentException`.
- Added `IssueCommentTool` to build completion and failure comments from task context.
- Added `FixTaskExecutionResult` so task execution can return the created PR URL.
- Updated `FixTaskExecutor` and `NoopFixTaskExecutor` to return the PR URL after Pull Request creation.
- Updated `AsyncFixTaskDispatcher` to comment on the original Issue after `COMPLETED` or `FAILED` status updates.
- Kept task status stable when a post-completion Issue comment fails.
- Isolated webhook controller tests from real GitHub Issue comment API calls with a primary test `IssueCommentTool`.
- Kept this phase limited to status feedback: no retries, persisted PR URLs, persisted comment IDs, merges, or model provider calls were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because Issue comment client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because `IssueCommentTool` did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because `FixTaskExecutionResult` did not exist and the executor returned `void`, then passed after returning the PR URL, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because the dispatcher did not accept `IssueCommentTool`, then passed after posting completion and failure comments, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests#should_keep_completed_status_when_completion_comment_fails test`: first failed because completion comment failure triggered a failure comment and status rewrite, then passed after narrowing the executor failure boundary, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: passed after the boundary fix, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests,IssueCommentToolTests,WorkspaceFixTaskExecutorTests,AsyncFixTaskDispatcherTests,GitHubWebhookControllerTests test`: passed, 21 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 79 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 79 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented MySQL task persistence from `docs/plans/014-mysql-task-persistence.md`.

Changes:

- Added Flyway migration `V1__create_fix_task.sql` for the `fix_task` table and unique `delivery_id`.
- Added `FixTaskEntity`, `FixTaskMapper`, and `FixTaskConvert` for MyBatis-Plus persistence mapping.
- Added `MyBatisFixTaskService` for `local` and `docker` profiles, preserving duplicate delivery handling and task status transitions.
- Added a task creation result contract so webhook handling can return `DUPLICATE_DELIVERY` without re-dispatching when MySQL already has the delivery id.
- Kept `InMemoryFixTaskService` as the default no-database service.
- Enabled Flyway migrations for `application-local.properties` and `application-docker.properties`.
- Added migration, converter, and MyBatis service tests.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests test`: first failed because the migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test`: first failed because entity/converter classes did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskServiceTests test`: first failed because `MyBatisFixTaskService` did not exist, then passed after implementation, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,InMemoryFixTaskServiceTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,MyBatisFixTaskServiceTests test`: first failed because `FixTaskCreationResult` did not exist, then passed after adding the creation-result contract, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,InMemoryFixTaskServiceTests,PatchPilotApplicationTests,GitHubWebhookControllerTests,GitHubWebhookServiceTests test`: passed, 23 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 89 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 89 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented task result persistence from `docs/plans/015-task-result-persistence.md`.

Changes:

- Added Flyway migration `V2__add_fix_task_result_fields.sql` for `pull_request_url`, `completed_at`, and `updated_at`.
- Extended `FixTaskVo`, `FixTaskEntity`, and `FixTaskConvert` with result metadata fields.
- Added `FixTaskService#markCompleted(String id, String pullRequestUrl)` while keeping the old single-argument method as a compatibility default.
- Updated in-memory and MyBatis task services to persist PR URL, completion time, and update time.
- Updated the async dispatcher to persist the Pull Request URL returned by task execution.
- Added task API assertions for additive JSON fields `pullRequestUrl`, `completedAt`, and `updatedAt`.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests test`: first failed because the V2 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test`: first failed because result fields and `replaceCompleted(...)` did not exist, then passed after updating DTO/entity/converter, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because `markCompleted(id, pullRequestUrl)` did not exist, then passed after service updates, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests,TaskControllerTests test`: first failed because completed tasks did not retain `pullRequestUrl`, then passed after dispatcher wiring, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests,FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,AsyncFixTaskDispatcherTests,TaskControllerTests,GitHubWebhookControllerTests test`: passed, 30 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 92 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 92 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented editable Issue comment lifecycle feedback from `docs/plans/016-edit-issue-comment-lifecycle.md`.

Changes:

- Added Flyway migration `V3__add_fix_task_status_comment.sql` for `status_comment_id` and `status_comment_url`.
- Extended `FixTaskVo`, `FixTaskEntity`, and `FixTaskConvert` with PatchPilot-owned status comment metadata.
- Added `FixTaskService#attachStatusComment(...)` for both in-memory and MyBatis-backed task services.
- Added `UpdateIssueCommentCommand` and `GitHubIssueCommentClient#updateIssueComment(...)` using GitHub's Issue comment PATCH endpoint.
- Updated `IssueCommentTool` with lifecycle methods for accepted, running, running tests, completed, and failed task states.
- Updated webhook handling to create one initial PatchPilot status comment, persist its id and URL, and avoid duplicate status comments for duplicate deliveries.
- Updated async dispatch to edit the same status comment after `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, and `FAILED` transitions.
- Kept GitHub comment creation/update failures non-blocking so durable task state remains authoritative.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskStatusCommentMigrationTests test`: first failed because the V3 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because status comment fields and `attachStatusComment(...)` did not exist, then passed after DTO/entity/converter/service updates, 17 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because `UpdateIssueCommentCommand` and `updateIssueComment(...)` did not exist, then passed after PATCH client implementation, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because lifecycle comment methods did not exist, then passed after tool updates, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests test`: first failed because webhook handling did not create/save the accepted status comment, then passed after wiring `IssueCommentTool`, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because only terminal comments were updated, then passed after adding lifecycle updates, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests,TaskControllerTests,AsyncFixTaskDispatcherTests test`: passed, 15 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskStatusCommentMigrationTests,FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,GitHubIssueCommentClientTests,IssueCommentToolTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,TaskControllerTests,GitHubWebhookControllerTests test`: passed, 48 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 105 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 105 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented task execution timeline from `docs/plans/017-task-execution-timeline.md`.

Changes:

- Added Flyway migration `V4__create_fix_task_timeline_event.sql` for timeline events.
- Added timeline event enum, VO, entity, converter, and MyBatis mapper.
- Added `FixTaskTimelineService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/timeline` to expose ordered task timeline events.
- Updated webhook handling to record `TASK_CREATED` and `STATUS_COMMENT_CREATED`.
- Updated async dispatch to record `RUNNING`, `RUNNING_TESTS`, `PR_CREATED`, `COMPLETED`, and `FAILED`.
- Kept timeline write failures non-blocking so durable task status transitions remain authoritative.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTimelineMigrationTests test`: first failed because the V4 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskTimelineEventConvertTests test`: first failed because timeline domain/converter classes did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskTimelineServiceTests,MyBatisFixTaskTimelineServiceTests test`: first failed because `FixTaskTimelineService` implementations did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `GET /api/tasks/{id}/timeline` did not exist, then passed after adding the endpoint, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests test`: first failed because webhook and dispatcher were not wired to `FixTaskTimelineService`, then passed after lifecycle event recording, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests,TaskControllerTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskTimelineMigrationTests,FixTaskTimelineEventConvertTests,InMemoryFixTaskTimelineServiceTests,MyBatisFixTaskTimelineServiceTests,TaskControllerTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,GitHubWebhookControllerTests test`: passed, 25 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 112 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 112 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented active task deduplication and the in-process queue boundary from `docs/plans/018-active-task-deduplication-queue.md`.

Changes:

- Added `FixTaskService#findTaskByDeliveryId(...)` so duplicate delivery idempotency can be checked before issue-level active task deduplication.
- Added `FixTaskService#findActiveTaskForIssue(...)` for both in-memory and MyBatis-backed task services.
- Added Flyway migration `V5__add_fix_task_active_lookup_index.sql` for `repository_owner`, `repository_name`, `issue_number`, and `status` lookup.
- Added `WebhookHandleStatus.ACTIVE_TASK_EXISTS` and `FixTaskTimelineEventType.ACTIVE_TASK_EXISTS`.
- Updated webhook handling so a second `/agent fix` for the same active issue returns the existing task id, records a timeline event, edits the existing status comment when possible, and does not create or dispatch a second task.
- Preserved duplicate delivery priority over active task deduplication, including active tasks found by delivery id after process restart.
- Added `FixTaskQueue` and `InMemoryFixTaskQueue` as the queue abstraction.
- Moved task execution lifecycle transitions from `AsyncFixTaskDispatcher` into `FixTaskWorker`.
- Reduced `AsyncFixTaskDispatcher` to enqueue task ids only.
- Kept this phase in-process only: no Redis, RabbitMQ, Kafka, or docker-compose service was added.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskMigrationTests,IssueCommentToolTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,FixTaskWorkerTests,InMemoryFixTaskQueueTests test`: first failed because the new queue, worker, active-task lookup, webhook status, timeline event, comment update, and migration did not exist, then passed after implementation, 36 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,GitHubWebhookServiceTests test`: first failed because `findTaskByDeliveryId(...)` was not part of the service contract, then passed after adding the contract and duplicate-before-active webhook check, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 124 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented Maven test-run records from `docs/plans/019-test-run-records.md`.

Changes:

- Added Flyway migration `V6__create_fix_task_test_run.sql` for the `fix_task_test_run` table.
- Added `FixTaskTestRunVo`, `FixTaskTestRunEntity`, `FixTaskTestRunConvert`, and `FixTaskTestRunMapper`.
- Added `FixTaskTestRunService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/test-runs` to expose ordered Maven verification records for a task.
- Updated `NoopFixTaskExecutor` to record Maven command, exit code, output, start time, end time, and duration immediately after `MavenTestRunner` returns.
- Preserved existing Maven failure behavior: non-zero test exits still fail the task before commit, push, or Pull Request creation.
- Kept this phase limited to Maven test-run observability; no external services or frontend changes were added.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTestRunMigrationTests,FixTaskTestRunConvertTests,InMemoryFixTaskTestRunServiceTests,MyBatisFixTaskTestRunServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests test`: first failed because the test-run VO, entity, mapper, service, controller endpoint, and executor dependency did not exist, then passed after implementation, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 131 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented tool-call audit records from `docs/plans/020-tool-call-audit-records.md`.

Changes:

- Added Flyway migration `V7__create_fix_task_tool_call.sql` for the `fix_task_tool_call` table.
- Added `FixTaskToolCallVo`, `FixTaskToolCallEntity`, `FixTaskToolCallConvert`, and `FixTaskToolCallMapper`.
- Added `FixTaskToolCallService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/tool-calls` to expose ordered tool-call audit records for a task.
- Updated `NoopFixTaskExecutor` to record `PatchWorkflow`, `DiffTool`, `CommitTool`, `PushTool`, and `PullRequestTool` success or failure with input/output summaries and timing.
- Kept Maven verification records in `fix_task_test_run`; tool-call audit does not duplicate Maven test-run details.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskToolCallMigrationTests,FixTaskToolCallConvertTests,InMemoryFixTaskToolCallServiceTests,MyBatisFixTaskToolCallServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests test`: first failed because the tool-call VO, entity, mapper, service, controller endpoint, and executor dependency did not exist, then passed after implementation, 18 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 138 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented MySQL-backed task queue from `docs/plans/021-mysql-backed-task-queue.md`.

Changes:

- Added Flyway migration `V8__create_fix_task_queue_item.sql` for durable queue records.
- Added `FixTaskQueueItemStatus`, `FixTaskQueueItemEntity`, `FixTaskQueueItemVo`, `FixTaskQueueItemConvert`, and `FixTaskQueueItemMapper`.
- Added `MyBatisFixTaskQueue` for `local` and `docker` profiles.
- Added `FixTaskQueuePoller` to claim queued items, execute `FixTaskWorker`, and persist `COMPLETED` or `FAILED` queue item status.
- Enabled Spring scheduling in `PatchPilotApplication`.
- Scoped `InMemoryFixTaskQueue` to the default profile so default tests remain database-free.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskQueueItemMigrationTests,FixTaskQueueItemConvertTests,MyBatisFixTaskQueueTests,FixTaskQueuePollerTests test`: first failed because the queue item VO, entity, enum, mapper, MyBatis queue, poller, and migration did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 148 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented queue recovery and retry behavior from `docs/plans/022-queue-recovery-and-retry.md`.

Changes:

- Added `TaskQueueProperties` with `patchpilot.task.queue` settings for max attempts, retry delay, and visibility timeout.
- Registered queue properties in `PatchPilotApplication`.
- Updated `MyBatisFixTaskQueue#markFailed(...)` so transient worker failures return the item to `PENDING` until max attempts are reached.
- Added stale `RUNNING` item recovery through `MyBatisFixTaskQueue#recoverTimedOutRunningItems()`.
- Updated `FixTaskQueuePoller` to recover timed-out running items before claiming new work.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests,FixTaskQueuePollerTests test`: first failed because `TaskQueueProperties`, retry-aware queue constructor, and `recoverTimedOutRunningItems()` did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 150 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented queue observability API from `docs/plans/023-queue-observability-api.md`.

Changes:

- Added `FixTaskQueueQueryService` to separate read-only queue inspection from queue execution.
- Added `FixTaskQueueSummaryVo` for aggregate queue state.
- Added default-profile empty queue query implementation so no-database local runs still expose queue endpoints safely.
- Added `MyBatisFixTaskQueueQueryService` for `local` and `docker` profiles to list queue items and summarize status counts from `fix_task_queue_item`.
- Added `GET /api/task-queue/items` with optional `status` filtering.
- Added `GET /api/task-queue/summary` for total, pending, available pending, delayed pending, running, completed, and failed counts.
- Kept this phase read-only: no queue mutation or admin retry endpoint was added.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueQueryServiceTests,TaskControllerTests test`: first failed because the queue query service and summary VO did not exist, then passed after implementation, 14 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskQueueControllerTests,MyBatisFixTaskQueueQueryServiceTests,TaskControllerTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 157 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented concurrent queue claim safety from `docs/plans/024-concurrent-queue-claim-safety.md`.

Changes:

- Updated `MyBatisFixTaskQueue#claimNext()` to use a conditional update for claiming selected pending queue items.
- The conditional update now requires the item id, `PENDING` status, and `available_at <= now`.
- `claimNext()` returns a running queue item only when the update affects one row.
- If another worker claims the selected item first and the update affects zero rows, `claimNext()` now returns `Optional.empty()` so the losing worker does not execute the task.
- Kept this phase scoped to claim safety: no distributed lock service, queue mutation API, or `FOR UPDATE SKIP LOCKED` path was added.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests test`: first failed because the old implementation still used `updateById` and returned a claimed item even when a conditional update would affect zero rows, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests,FixTaskQueuePollerTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 158 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented command allowlist and task sandbox guards from `docs/plans/025-command-allowlist-and-task-sandbox.md`.

Changes:

- Added `CommandExecutionGuard` to validate command shapes and command working directories before process execution.
- Guarded Git commands in `GitCommandRunner`, including clone, branch creation, diff, add, commit, and push.
- Guarded Maven test execution in `MavenTestRunner` for `./mvnw test` and `mvn test`.
- Updated `WorkspacePathResolver` to reject repository roots outside `patchpilot.workspace.root-dir` before resolving file read/write paths.
- Updated `RepositoryFileScanner` to reject repository roots outside `patchpilot.workspace.root-dir` before file tree and code search scans.
- Preserved existing path traversal rejection for relative file inputs.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests test`: first failed because `CommandExecutionGuard` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,MavenTestRunnerTests test`: first failed because existing runner tests used temp directories outside the configured workspace root, then passed after injecting test workspace roots, 22 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test`: first failed because `RepositoryFileScanner` did not accept `WorkspaceProperties`, then passed after scanner root validation, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests test`: first failed because `WorkspacePathResolver` did not accept `WorkspaceProperties`, then passed after resolver root validation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests,FileToolsTests,SimplePatchWorkflowTests,RepositoryInspectionToolsTests test`: passed, 19 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,MavenTestRunnerTests,WorkspacePathResolverTests,FileToolsTests,RepositoryInspectionToolsTests,SimplePatchWorkflowTests,GitWorkspaceServiceTests,PatchPilotApplicationTests test`: passed, 47 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 167 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented task control API from `docs/plans/026-task-control-api.md`.

Changes:

- Added `CANCELLED` task status and `CANCELLED` / `REQUEUED` timeline event types.
- Added `FixTaskControlService` to own user-driven task lifecycle actions.
- Added `POST /api/tasks/{id}/cancel` for pending tasks only.
- Added `POST /api/tasks/{id}/retry` for failed or cancelled tasks only.
- Added pending queue-item cancellation support and `CANCELLED` queue item status.
- Extended queue summary responses with `cancelledCount`.
- Kept cancellation scoped to pending tasks because running worker interruption is not implemented yet.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,TaskControllerTests test`: first failed because the new service methods and status enums did not exist, confirming the red test path.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests,TaskControllerTests test`: passed, 54 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests,TaskControllerTests test`: passed, 58 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented running task cancellation safety from `docs/plans/027-running-task-cancellation-safety.md`.

Changes:

- Extended task cancellation to active `RUNNING` and `RUNNING_TESTS` tasks while keeping terminal tasks non-cancellable.
- Added `TaskCancellationChecker` and `TaskCancellationException` for durable task-state cancellation checks.
- Added `DefaultTaskCancellationChecker` backed by `FixTaskService`.
- Updated `NoopFixTaskExecutor` to check cancellation at execution stage boundaries before later side effects.
- Updated `FixTaskWorker` so cancellation exceptions stop execution cleanly without overwriting `CANCELLED` tasks as `FAILED`.
- Kept this phase scoped to stage-boundary safety; Maven/Git process interruption is not implemented yet.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskWorkerTests,WorkspaceFixTaskExecutorTests test`: first failed because `TaskCancellationChecker`, `TaskCancellationException`, and the extended executor constructor did not exist; then failed once because the cancellation test fired before test-run recording; then passed after implementation and test correction, 34 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 187 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented cancellable Maven process runner from `docs/plans/028-cancellable-process-runner.md`.

Changes:

- Added `TaskProcessRegistry` to register the currently running external process for each task.
- Added task-scoped Maven execution through `MavenTestRunner#runTests(String taskId, Path repositoryDir)`.
- Updated Maven process execution to register and unregister task processes around `ProcessBuilder` execution.
- Updated task cancellation control to interrupt a registered process when cancelling `RUNNING` or `RUNNING_TESTS` tasks.
- Updated `NoopFixTaskExecutor` to pass the task id into Maven test execution.
- Rechecked cancellation after recording Maven test results so a cancelled Maven process is not reported as a failed task.
- Deferred Git process cancellation and repository recovery to plan 029.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskProcessRegistryTests,MavenTestRunnerTests,DefaultFixTaskControlServiceTests,WorkspaceFixTaskExecutorTests test`: first failed because `TaskProcessRegistry`, task-aware Maven execution, and the extended control-service constructor did not exist; then failed once because a recording test runner still overrode the old `runCommand` signature; then passed after implementation and test correction, 22 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because a cancelled Maven process result was still raised as `IllegalStateException`; then passed after checking cancellation before Maven exit-code failure handling, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskProcessRegistryTests,MavenTestRunnerTests,DefaultFixTaskControlServiceTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 31 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 192 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented Git process cancellation and recovery guidance from `docs/plans/029-git-process-cancellation-recovery.md`.

Changes:

- Added `GitWorkspaceRecoveryInspector` to read known Git half-finished states without deleting files or running recovery commands.
- Detects `.git/index.lock`, `.git/HEAD.lock`, `.git/MERGE_HEAD`, `.git/rebase-merge`, and `.git/rebase-apply`.
- Extended `GitCommandRunner` with task-aware clone, branch, diff, stage, commit, and push overloads.
- Reused `TaskProcessRegistry` so task cancellation can interrupt Git processes as well as Maven processes.
- Passed task ids through workspace clone/branch creation, commit, and push.
- Added recovery guidance to clone, branch creation, commit, and push failure messages when known Git lock or in-progress states are present.
- Added `WorkspaceService` tool-call audit records for repository preparation.
- Converted commit and push failures into cancellation when the durable task state has become `CANCELLED` during the Git operation.
- Kept recovery read-only; manual cleanup remains explicit.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceRecoveryInspectorTests test`: first failed because `GitWorkspaceRecoveryInspector` did not exist, then passed, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because task-aware overloads, process registry injection, and `startProcess` override point did not exist, then passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests,CommitToolTests,PushToolTests,WorkspaceFixTaskExecutorTests test`: first failed because task-aware tool APIs and recovery inspector injection did not exist, then passed, 21 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because commit and push cancellation still surfaced as Git failures, then passed after checking cancellation after failed audited tool calls, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceRecoveryInspectorTests,GitCommandRunnerTests,GitWorkspaceServiceTests,CommitToolTests,PushToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: first failed because new constructors needed explicit Spring injection and webhook test doubles still overrode old commit/push signatures, then passed, 52 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 209 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented model-call audit records from `docs/plans/030-model-call-audit-records.md`.

Changes:

- Added Flyway migration `V9__create_fix_task_model_call.sql` for model-call audit rows.
- Added `FixTaskModelCallVo`, `FixTaskModelCallEntity`, converter, mapper, and service boundary.
- Added default-profile in-memory and local/docker MyBatis-backed model-call services.
- Added `GET /api/tasks/{id}/model-calls` to expose ordered model-call records for existing tasks.
- Kept this phase audit-only: no real model provider calls, prompt generation, or workflow changes were added.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskModelCallConvertTests,FixTaskModelCallMigrationTests,InMemoryFixTaskModelCallServiceTests,MyBatisFixTaskModelCallServiceTests,TaskControllerTests test`: first failed because model-call VO/entity/converter/mapper/service and controller wiring did not exist, then passed after implementation, 25 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 216 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented model provider client boundary from `docs/plans/031-model-provider-client-boundary.md`.

Changes:

- Added `AgentProperties` under `patchpilot.agent` for provider, model, base URL, and API key.
- Added `ModelProviderClient`, request/response records, and `ModelProviderException`.
- Added `OpenAiCompatibleModelClient` using Java `HttpClient` against `/chat/completions`.
- Recorded successful and failed model client calls through `FixTaskModelCallService`.
- Added environment-backed defaults for `PATCHPILOT_AGENT_PROVIDER`, `PATCHPILOT_AGENT_MODEL`, `PATCHPILOT_AGENT_BASE_URL`, and `PATCHPILOT_AGENT_API_KEY`.
- Kept this phase boundary-only: no workflow path invokes the model client yet.

Validation:

- `mvn -pl PatchPilot -Dtest=OpenAiCompatibleModelClientTests test`: first failed because agent config, provider domain, and client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,OpenAiCompatibleModelClientTests test`: passed, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 219 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented structured fix-plan generation from `docs/plans/032-structured-fix-plan-generation.md`.

Changes:

- Added `FixPlanGenerator` as a model-backed planning boundary.
- Added typed `FixPlan` output with summary, target files, steps, and risk fields.
- Added `FixPlanGenerationException` for invalid or incomplete model output.
- Built deterministic system and user prompts from `FixTaskVo` metadata.
- Kept execution unchanged: `NoopFixTaskExecutor` and `SimplePatchWorkflow` do not call the fix-plan generator yet.

Validation:

- `mvn -pl PatchPilot -Dtest=FixPlanGeneratorTests test`: first failed because `FixPlanGenerator`, `FixPlan`, and `FixPlanGenerationException` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,FixPlanGeneratorTests,WorkspaceFixTaskExecutorTests test`: first failed because Spring could not choose the production `FixPlanGenerator` constructor, then passed after marking it with `@Autowired`, 12 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 222 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented planned file edit workflow from `docs/plans/033-planned-file-edit-workflow.md`.

Changes:

- Added `PlannedPatchWorkflow` as an internal, non-Spring workflow class.
- Supported only `/agent fix replace <path> <text>` planned replacement instructions.
- Required replacement targets to appear in `FixPlan.targetFiles()`.
- Routed writes through `FileWriteTool` so existing workspace path guards still apply.
- Kept production execution unchanged: `SimplePatchWorkflow` remains the active `PatchWorkflow` bean.

Validation:

- `mvn -pl PatchPilot -Dtest=PlannedPatchWorkflowTests test`: first failed because `PlannedPatchWorkflow` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,PlannedPatchWorkflowTests,SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 226 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented plan-driven executor integration from `docs/plans/034-plan-driven-executor-integration.md`.

Changes:

- Added `PlanDrivenPatchWorkflow` as the production `PatchWorkflow`.
- Wired production patching as `FixPlanGenerator` followed by `PlannedPatchWorkflow`.
- Added `PatchWorkflowConfiguration` to provide the planned patch workflow bean.
- Kept `SimplePatchWorkflow` available as a deterministic test helper but no longer registered it as a Spring component.
- Added an application-context assertion that production has one `PatchWorkflow` bean and it is plan-driven.
- Preserved the existing executor sequence after patching: diff, Maven tests, commit, push, and Pull Request creation.
- Added explicit Maven compiler annotation processor configuration for Lombok because the current branch contains Lombok-based entity and constructor changes.

Validation:

- `mvn -pl PatchPilot -Dtest=PlanDrivenPatchWorkflowTests test`: first failed because `PlanDrivenPatchWorkflow` did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests test`: first failed because Lombok-generated accessors were unavailable during compilation, then passed after adding the compiler annotation processor configuration, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PlanDrivenPatchWorkflowTests,PlannedPatchWorkflowTests,PatchPilotApplicationTests test`: passed, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests,PlanDrivenPatchWorkflowTests,PlannedPatchWorkflowTests,PatchPilotApplicationTests test`: passed, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Completed low-risk Lombok cleanup from `docs/plans/035-code-simplification-lombok-cleanup.md`.

Changes:

- Removed duplicated getter/setter methods from task persistence entities that are now covered by Lombok `@Data`.
- Replaced pure dependency-injection constructors with `@RequiredArgsConstructor` in task controllers, task services, queue components, workflow classes, repository tools, file tools, issue/PR tools, and command guard code.
- Replaced repeated blank-string checks with Spring `StringUtils` instead of adding Hutool for a small helper-only cleanup.
- Kept explicit constructors where they still document overloads, default helper creation, package-private test seams, or custom initialization.
- Verified `.idea/` and `.DS_Store` are ignored and not tracked.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskQueueItemConvertTests,FixTaskTestRunConvertTests,FixTaskTimelineEventConvertTests,FixTaskToolCallConvertTests,MyBatisFixTaskQueueTests,MyBatisFixTaskTestRunServiceTests,MyBatisFixTaskTimelineServiceTests,MyBatisFixTaskToolCallServiceTests test`: passed, 19 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,TaskControllerTests,TaskQueueControllerTests,FixTaskWorkerTests,WorkspaceFixTaskExecutorTests,CommandExecutionGuardTests,RepositoryInspectionToolsTests,FileToolsTests,DiffToolTests,PullRequestToolTests,IssueCommentToolTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,MyBatisFixTaskModelCallServiceTests,MyBatisFixTaskTestRunServiceTests,MyBatisFixTaskTimelineServiceTests,MyBatisFixTaskToolCallServiceTests test`: passed, 95 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,OpenAiCompatibleModelClientTests,GitHubIssueCommentClientTests,GitHubPullRequestClientTests,GitHubWebhookServiceTests,GitHubWebhookSignatureVerifierTests,FixPlanGeneratorTests,SimplePatchWorkflowTests,PlannedPatchWorkflowTests,RepositoryInspectionToolsTests,MavenTestRunnerTests,CommandExecutionGuardTests,TaskProcessRegistryTests,FixTaskWorkerTests,FixTaskQueuePollerTests test`: passed, 70 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Started self-hosted README and environment documentation from `docs/plans/036-self-hosted-readme-and-env-docs.md`.

Changes:

- Rewrote `README.md` around the current self-hosted GitHub issue-to-PR workflow.
- Added `.env.example` for Docker Compose, GitHub webhook/token, workspace, and model provider configuration.
- Updated `.gitignore` so local `.env` files stay untracked while `.env.example` remains commit-ready.
- Passed model provider environment variables through `docker-compose.yml`.
- Added `docs/agent/smoke-test-checklist.md` for repeatable local demo validation.
- Updated temporary URL and IDEA local run docs to match the MySQL-backed Docker profile and default no-database IDEA profile.

Validation:

- `docker compose --env-file .env.example config`: passed, Compose resolves the backend, MySQL, GitHub, workspace, and model provider environment variables.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Started end-to-end smoke test hardening from `docs/plans/037-end-to-end-smoke-test-hardening.md`.

Initial state:

- Branch `037-end-to-end-smoke-test-hardening` is active.
- Local `.env` is not present, so Docker runtime and GitHub webhook smoke tests are blocked until local secrets are configured.

Validation:

- `docker compose --env-file .env.example config`: passed, Compose structure resolves with placeholder values.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.
- `.env` validation: passed, required keys are present and non-placeholder without exposing values.
- `docker compose --env-file .env config --quiet`: passed.
- `docker compose --env-file .env ps -a`: passed, `patchpilot-backend` is up and `patchpilot-mysql` is healthy.
- Backend logs show Spring Boot active with the `docker` profile, Flyway migrations up to date, and Tomcat started on port 8080.
- Local API checks passed outside the restricted command sandbox:
  - `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
  - `curl http://127.0.0.1:8080/api/tasks`: returned `success=true` with an empty task list.
  - `curl http://127.0.0.1:8080/api/task-queue/summary`: returned `success=true` with zero queue counts.
- Note: sandboxed local port checks returned connection errors even while Docker and the backend were healthy; unrestricted local checks confirmed the backend is reachable.
- Cloudflare Tunnel health check reached the backend; `GET /api/github/webhook` returned `405`, confirming the route is reachable and POST-only.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `d87a1b3d-87e3-4435-9ad1-fda9d1f528e5`.
- Task `d87a1b3d-87e3-4435-9ad1-fda9d1f528e5` reached the worker:
  - Workspace clone and branch creation succeeded.
  - Model call succeeded with model `gpt-5.5`.
  - Planned patch replaced `docs/demo.md`.
  - Diff tool ran successfully.
- Task failed during verification with `maven tests failed: maven test command timed out`.
- Test run record captured `mvn test`, exit code `124`, duration `300001` ms, and output `maven test command timed out`.
- Root cause investigation:
  - The backend container runs with `SPRING_PROFILES_ACTIVE=docker`.
  - `MavenTestRunner` inherited the backend container environment when launching target repository tests.
  - The target repository Spring tests then loaded the Docker profile and failed to create MyBatis mapper-backed services in the test context.
  - Re-running the same container workspace with `env -u SPRING_PROFILES_ACTIVE mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test` passed, 30 tests run, 0 failures, 0 errors.
- Fixed `MavenTestRunner` so child Maven processes remove `PATCHPILOT_*` runtime variables and `SPRING_PROFILES_ACTIVE`.
- Added a regression test that verifies Maven child process environment sanitization preserves normal variables such as `PATH` and `JAVA_HOME`.
- Found a second Maven runner issue: output was read only after process exit, so large Maven/Spring output could fill the process pipe and make a failing command look like a timeout.
- Fixed `MavenTestRunner` to read merged output asynchronously while the process is running and keep partial output on timeout.
- Added a regression test that emits 20,000 output lines and exits with code 7; it failed with exit code 124 before the fix and passed after output was read asynchronously.
- Updated smoke test docs from the old unsupported `/agent fix touch ...` command to `/agent fix replace docs/demo.md PatchPilot smoke test`.

Validation after fix:

- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: first failed because environment sanitization did not exist, then passed after implementation, 9 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test`: passed, 30 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests#should_capture_large_command_output_without_blocking_until_timeout test`: first failed with exit code 124 instead of the expected exit code 7, then passed after asynchronous output capture.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: passed after both fixes, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 231 tests run, 0 failures, 0 errors.

Follow-up:

- Rebuild the Docker backend image and rerun the GitHub issue smoke test so the running container uses the fixed `MavenTestRunner`.

Second smoke-test rerun:

- Rebuilt and restarted Docker Compose backend with the fixed `MavenTestRunner`.
- `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `4645c8e7-058b-4ea6-a51e-e00d1d3878be`.
- The task reached workspace clone, model call, planned patch, diff, and Maven verification.
- The task then failed while persisting the test run record because full Maven output exceeded MySQL `text` capacity:
  - `Data too long for column 'output' at row 1`.
- Fixed test-run persistence by truncating captured Maven output before storing it.
- Fixed worker failure handling by truncating failure reasons before saving task state, timeline messages, and GitHub status-comment content.

Validation after output truncation fix:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskTestRunServiceTests,InMemoryFixTaskTestRunServiceTests,FixTaskWorkerTests test`: first failed because output and failure reasons were not truncated, then passed after implementation, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 234 tests run, 0 failures, 0 errors.

Follow-up:

- Rebuild the Docker backend image again and rerun the GitHub issue smoke test so the running container uses the output truncation fix.

Third smoke-test rerun:

- Confirmed the running backend image contains `LogSummary.class`, so the output truncation fix is present in the container.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `c550c1ed-bc4e-42b8-aa02-f3de027b0b9e`.
- The task reached workspace clone, model call, planned patch, diff, and Maven verification.
- Maven verification succeeded:
  - `mvn test` exit code `0`.
  - Duration `43292` ms.
  - Captured output was truncated before persistence, avoiding the previous MySQL `output` overflow.
- The task then failed at `CommitTool` because the container workspace had no Git author identity:
  - `git commit failed: Author identity unknown`.
  - Git attempted to auto-detect `root@...` inside the container and failed.
- Fixed `GitCommandRunner#commit(...)` to run commits with a command-scoped PatchPilot author identity:
  - `git -C <repo> -c user.name=PatchPilot -c user.email=patchpilot@example.com commit -m <message>`.
- Updated `CommandExecutionGuard` to allow only that exact command-scoped identity form for commits.

Validation after Git author identity fix:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests#should_commit_with_patchpilot_author_identity,CommandExecutionGuardTests#should_allow_mvp_git_and_maven_commands_inside_workspace_root test`: first failed because the commit command lacked author identity and the guard rejected the command-scoped identity form, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests test`: passed, 32 tests run, 0 failures, 0 errors.

Follow-up:

- Run the full backend test suite, rebuild the Docker backend image, and rerun the GitHub issue smoke test so the running container uses the Git author identity fix.

Fourth smoke-test rerun:

- Rebuilt and restarted Docker Compose backend with the Git author identity fix.
- `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `7cb4f2ab-189e-4df6-85b0-519f3fabe46c`.
- The task completed end to end:
  - Workspace clone and branch creation succeeded.
  - Planned patch replaced `docs/demo.md`.
  - Diff tool succeeded.
  - `mvn test` succeeded with exit code `0` and duration `45088` ms.
  - Commit succeeded on branch `patchpilot/7cb4f2ab-189e-4df6-85b0-519f3fabe46c`.
  - Push succeeded to GitHub.
  - Pull Request creation succeeded: `https://github.com/bingqin2/PatchPilot/pull/7`.
- Task detail returned `status=COMPLETED`, `failureReason=null`, and `pullRequestUrl=https://github.com/bingqin2/PatchPilot/pull/7`.
- Timeline ended with `PR_CREATED` followed by `COMPLETED`.

## 2026-06-20

Implemented status comment failure observability from `docs/plans/038-status-comment-observability.md`.

Changes:

- Added `STATUS_COMMENT_FAILED` as an additive timeline event.
- Recorded `STATUS_COMMENT_FAILED` when accepted issue status-comment creation fails, while still dispatching the task.
- Recorded `STATUS_COMMENT_FAILED` when lifecycle status-comment updates fail in the worker, while preserving the durable task status.
- Reused failure-reason truncation for status-comment failure messages.
- Updated setup and smoke-test docs to require fine-grained GitHub token permissions for `Contents`, `Issues`, and `Pull requests`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_dispatch_created_task_when_status_comment_creation_fails,FixTaskWorkerTests#should_keep_completed_status_when_status_comment_update_fails test`: passed, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,FixTaskWorkerTests test`: passed, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 235 tests run, 0 failures, 0 errors.

Implemented test-run output storage expansion from `docs/plans/039-test-run-output-storage.md`.

Changes:

- Added Flyway migration `V10__expand_fix_task_test_run_output.sql` to change `fix_task_test_run.output` to `mediumtext`.
- Added a dedicated `LogSummary.truncateTestRunOutput(...)` helper with a higher bounded output limit for Maven test logs.
- Updated in-memory and MyBatis test-run services to use the test-run-specific output limit.
- Added regression coverage that preserves 120k-character test output and still truncates very large output.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTestRunOutputStorageMigrationTests,MyBatisFixTaskTestRunServiceTests,InMemoryFixTaskTestRunServiceTests test`: first failed because migration `V10__expand_fix_task_test_run_output.sql` did not exist and 120k-character output was still truncated to the old `TEXT` limit, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 238 tests run, 0 failures, 0 errors.

Ran GitHub smoke test after the test-run output storage fix from `docs/plans/040-github-smoke-run-after-storage-fix.md`.

Smoke setup:

- Local IDEA backend was running on `http://127.0.0.1:18080` with the `idea` profile.
- Docker MySQL was reachable through the IDEA compose profile on `127.0.0.1:3307`.
- Cloudflare Tunnel was restarted against `http://127.0.0.1:18080`.
- GitHub webhook payload URL was updated to the new temporary tunnel URL.

Smoke result:

- GitHub webhook delivery for `/agent fix replace docs/demo.md PatchPilot storage smoke test` created task `73c92ed1-d2d5-4be3-a5ad-c1ff7047f12c`.
- Task detail returned `status=COMPLETED`, `failureReason=null`, and `pullRequestUrl=https://github.com/bingqin2/PatchPilot/pull/8`.
- Queue summary returned `pendingCount=0`, `runningCount=0`, `completedCount=5`, `failedCount=0`, and `cancelledCount=0`.
- Test-run API returned a persisted `mvn test` record with exit code `0`, duration `12769` ms, and full Maven test output including `Tests run: 238, Failures: 0, Errors: 0, Skipped: 0`.
- Timeline ended with `PR_CREATED` followed by `COMPLETED`.

Follow-up:

- The timeline recorded `STATUS_COMMENT_FAILED` with `GitHub issue comment creation failed: HTTP 403`; task execution still completed because issue comments are best-effort. Recheck the fine-grained GitHub token's `Issues: Read and write` permission or regenerate/reload the token before relying on issue status comments.

Implemented issue comment permission diagnostics from `docs/plans/041-issue-comment-permission-diagnostics.md`.

Changes:

- Added a shared HTTP failure message helper for GitHub Issue comment create/update calls.
- Expanded HTTP `403` failures with an actionable `PATCHPILOT_GITHUB_TOKEN` permission hint for fine-grained tokens.
- Preserved concise existing messages for non-`403` GitHub Issue comment failures.
- Updated setup and smoke-test docs to call out `Issues: Read and write` as the required permission for PatchPilot status comments.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because HTTP `403` messages only included the status code, then passed after adding the permission hint, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests,GitHubWebhookServiceTests,FixTaskWorkerTests test`: passed, 18 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 239 tests run, 0 failures, 0 errors.

Implemented task metrics summary API from `docs/plans/042-task-metrics-summary-api.md`.

Changes:

- Added `FixTaskMetricsSummaryVo` for task-level operational metrics.
- Added `FixTaskMetricsService` and `DefaultFixTaskMetricsService` to aggregate status counts, completion/failure rates, completion duration, and model token usage from existing task and model-call records.
- Exposed `GET /api/tasks/metrics/summary`.
- Documented the metrics endpoint in README.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because metrics service classes did not exist, then failed on a non-deterministic same-millisecond duration assertion, then passed after using fixed test data, 23 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 242 tests run, 0 failures, 0 errors.

Implemented task list filtering API from `docs/plans/043-task-list-filter-api.md`.

Changes:

- Extended `GET /api/tasks` with optional `status`, `repositoryOwner`, `repositoryName`, and `limit` query parameters.
- Added HTTP `400` responses for invalid task status and out-of-range limits.
- Kept the default `GET /api/tasks` behavior backward compatible.
- Documented filtered task-list examples in README.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because the task list endpoint ignored query parameters and returned HTTP `200` for invalid filters, then passed after controller filtering and validation, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 245 tests run, 0 failures, 0 errors.

Implemented task detail audit summary API from `docs/plans/044-task-detail-audit-summary-api.md`.

Changes:

- Added `FixTaskAuditSummaryVo` for single-task audit summaries.
- Added `FixTaskAuditSummaryService` and `DefaultFixTaskAuditSummaryService` to aggregate existing task, timeline, test-run, tool-call, and model-call records.
- Exposed `GET /api/tasks/{taskId}/summary`.
- Documented the summary endpoint in README.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `/api/tasks/{taskId}/summary` did not exist, then passed after adding the summary service and controller route, 26 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 247 tests run, 0 failures, 0 errors.

Implemented task metrics test pass rate from `docs/plans/045-task-metrics-test-pass-rate.md`.

Changes:

- Extended task metrics summary with test-run count, passed/failed test-run counts, and test pass rate.
- Aggregated test-run metrics through the existing `FixTaskTestRunService`.
- Preserved zero values when no tasks or test runs exist.
- Covered the new metrics fields in service and controller tests.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because metrics summary did not expose test-run fields and metrics service did not depend on test-run service, then passed after implementation, 28 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 247 tests run, 0 failures, 0 errors.

Implemented the React dashboard scaffold from `docs/plans/046-react-dashboard-scaffold.md`.

Changes:

- Added a React + Vite + TypeScript frontend under `frontend/`.
- Added typed API helpers for task list, metrics summary, task summary, timeline, test-run, tool-call, and model-call endpoints.
- Built a compact operations dashboard with metric cards, task list, Pull Request links, selected task summary, timeline, Maven test output, tool calls, and model calls.
- Added Vitest and Testing Library coverage for successful backend rendering and backend error display.
- Documented frontend setup and validation commands in README.

Validation:

- `npm test` in `frontend/`: first failed because `src/App.tsx` did not exist, then failed because the task detail omitted the latest summary event, then passed after implementation, 2 tests run, 0 failures.
- `npm run build` in `frontend/`: first failed because TypeScript config did not match Vite/Vitest module resolution and test globals, then passed after separating Vite and Vitest config, production build generated `dist/`.

Implemented dashboard task status filters from `docs/plans/047-dashboard-task-filters.md`.

Changes:

- Added `ALL`, `PENDING`, `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, `FAILED`, and `CANCELLED` filters to the React task list.
- Updated the frontend task API helper to call `/api/tasks?limit=50` for all tasks and `/api/tasks?limit=50&status={STATUS}` for filtered lists.
- Reset selected task details when the current selection is not present in the filtered result.
- Added empty-state copy for filtered task lists.
- Documented the dashboard status filters in README.

Validation:

- `npm test` in `frontend/`: first failed because the filter controls were not implemented, then failed because the test used ambiguous status text and missing task-detail mocks, then passed after implementation, 3 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard task control actions from `docs/plans/048-dashboard-task-control-actions.md`.

Changes:

- Added frontend POST helpers for `/api/tasks/{taskId}/cancel` and `/api/tasks/{taskId}/retry`.
- Added `Cancel task` in task detail for `PENDING`, `RUNNING`, and `RUNNING_TESTS` tasks.
- Added `Retry task` in task detail for `FAILED` and `CANCELLED` tasks.
- Disabled the active action while a control request is in flight.
- Refreshed dashboard task list, metrics, and selected task detail after successful control actions.
- Documented dashboard cancel/retry support in README.

Validation:

- `npm test` in `frontend/`: first failed because `Cancel task` and `Retry task` controls did not exist, then passed after implementation, 5 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard queue observability from `docs/plans/049-dashboard-queue-observability.md`.

Changes:

- Added frontend queue types for queue summaries and queue items.
- Added API helpers for `/api/task-queue/summary` and `/api/task-queue/items`.
- Loaded queue data during dashboard refresh.
- Added a read-only Queue panel showing pending, available, delayed, running, failed, and cancelled counts.
- Rendered queue item id, task id, status, attempt count, available time, and last error.
- Documented dashboard queue visibility in README.

Validation:

- `npm test` in `frontend/`: first failed because the Queue panel did not exist, then passed after implementation, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard issue links from `docs/plans/050-dashboard-issue-links.md`.

Changes:

- Added generated GitHub Issue links from task repository owner, repository name, and issue number.
- Rendered `Open Issue` links in visible task rows.
- Rendered `Open Issue` in the selected task detail header alongside existing Pull Request links.
- Documented dashboard issue links in README.

Validation:

- `npm test` in `frontend/`: first failed because no `Open Issue` links existed, then passed after implementation, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard component extraction from `docs/plans/051-dashboard-component-extraction.md`.

Changes:

- Kept `frontend/src/App.tsx` as the dashboard data-loading, selection, and task-action coordinator.
- Extracted task list, task detail, queue panel, metric card, summary item, and record-line rendering into `frontend/src/dashboard/components/`.
- Moved dashboard formatting helpers into `frontend/src/dashboard/format.ts`.
- Moved selected-task detail state shape and empty state into `frontend/src/dashboard/types.ts`.
- Documented the dashboard component boundary in README.

Validation:

- `npm test` in `frontend/`: passed, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard status comment links from `docs/plans/052-dashboard-status-comment-links.md`.

Changes:

- Added frontend coverage for optional task `statusCommentUrl` links.
- Rendered `Status Comment` links in task rows when a task exposes a status comment URL.
- Rendered `Status Comment` in the selected task detail action area when available.
- Documented dashboard status comment links in README.

Validation:

- `npm test` in `frontend/`: first failed because `Status Comment` links were not rendered, then passed after implementation, 6 tests run, 0 failures.

Implemented dashboard task timestamps from `docs/plans/053-dashboard-task-timestamps.md`.

Changes:

- Added frontend coverage for task row creation and update timestamps.
- Rendered `Created` and `Updated` times in each dashboard task row using existing task API fields.
- Preserved the original ISO timestamp through `dateTime` attributes.
- Added wrapping timestamp styling for task rows.
- Documented task timestamp visibility in README.

Validation:

- `npm test` in `frontend/`: first failed because task rows did not render `Created` and `Updated` times, then passed after implementation, 7 tests run, 0 failures.

Implemented dashboard detail empty states from `docs/plans/054-dashboard-detail-empty-states.md`.

Changes:

- Added frontend coverage for task detail sections with missing records.
- Rendered empty states for absent timeline events, Maven test runs, tool calls, and model calls.
- Limited empty states to completed detail-loading states so loading copy remains distinct.
- Documented detail empty-state behavior in README.

Validation:

- `npm test` in `frontend/`: first failed because missing detail records produced blank sections, then passed after implementation, 8 tests run, 0 failures.

Implemented dashboard call durations from `docs/plans/055-dashboard-call-durations.md`.

Changes:

- Added frontend coverage for tool-call and model-call duration rendering.
- Rendered tool-call duration next to the success/failure state in task detail records.
- Rendered model-call duration next to the total token count in task detail records.
- Reused the existing dashboard duration formatter, with no backend API or persistence changes.
- Documented dashboard call durations in README.

Validation:

- `npm test` in `frontend/`: first failed because task detail tool/model call records did not show durations, then passed after implementation, 9 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard task search from `docs/plans/056-dashboard-task-search.md`.

Changes:

- Added a task search input to the React task list panel.
- Filtered the currently loaded frontend task list by task id, repository, issue number, status, trigger comment, and failure reason.
- Kept status filters backed by existing backend query parameters.
- Added a distinct empty state when local search has no matches.
- Documented local dashboard search and recorded backend `GET /api/tasks?query=...` search as future work.

Validation:

- `npm test` in `frontend/`: first failed because the task list had no `Search tasks` searchbox, then passed after implementation, 10 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Planned backend task history search and pagination from `docs/plans/057-task-history-search-pagination-plan.md`.

Changes:

- Defined the target `GET /api/tasks?query=...&status=...&repositoryOwner=...&repositoryName=...&limit=...&offset=...` API shape.
- Documented searchable MySQL fields and stable newest-first sorting.
- Chose to keep the existing list response shape for the first backend-search phase to avoid frontend breakage.
- Split backend service/query, MyBatis, in-memory, controller, and frontend upgrade work into future implementation steps.
- Captured tests needed for query, pagination, escaping, and dashboard integration.

Validation:

- Documentation-only planning change; no runtime tests required.

Implemented backend task search and offset pagination from `docs/plans/058-backend-task-search-pagination.md`.

Changes:

- Added `FixTaskListQuery` as the backend task-list query object.
- Extended `GET /api/tasks` with optional `query` and `offset` parameters while preserving the existing list response shape.
- Moved task-list filtering from controller stream filtering into `FixTaskService#listTasks(FixTaskListQuery)`.
- Implemented equivalent query behavior for default in-memory tasks and MyBatis-backed task storage.
- Kept exact status and repository filters, newest-first sorting, and offset/limit application after filtering.
- Extended the frontend `listTasks()` helper to accept future `{ status, query, limit, offset }` options without changing the current dashboard UI behavior.
- Documented backend task-list search support and the remaining dashboard wiring work.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `query` and `offset` were ignored, then passed after implementation, 28 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: passed after adding equivalent service coverage, 25 tests run, 0 failures.
- `npm test` in `frontend/`: passed after adding API helper coverage for `{ status, query, limit, offset }`, 11 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 251 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard backend search and offset pagination from `docs/plans/059-dashboard-backend-search-pagination.md`.

Changes:

- Wired the dashboard search input to backend `GET /api/tasks?query=...`.
- Preserved status filters when sending backend search requests.
- Removed local-only task filtering from the dashboard coordinator.
- Added `Load more tasks` backed by `offset=tasks.length` and appended subsequent pages.
- Documented backend-backed dashboard search and the remaining pagination metadata limitation.

Validation:

- `npm test -- src/App.test.tsx -t "searches tasks with backend query parameters"`: first failed because the dashboard still only called `/api/tasks?limit=50`, then passed after wiring `searchQuery` into `listTasks()`.
- `npm test -- src/App.test.tsx -t "preserves status filter when searching backend task history"`: passed, preserving `query` and `status` together.
- `npm test -- src/App.test.tsx -t "loads the next backend task page with offset pagination"`: first failed because there was no `Load more tasks` button, then passed after adding offset pagination.
- `npm test` in `frontend/`: passed, 13 tests run, 0 failures.

Implemented task list pagination metadata from `docs/plans/060-task-list-pagination-metadata.md`.

Changes:

- Changed `GET /api/tasks` response data from a plain task array to a task page object.
- Added `FixTaskPageVo` with `items`, `limit`, `offset`, and `hasMore`.
- Computed `hasMore` by internally requesting one extra task beyond the requested page size.
- Updated the frontend task API type and dashboard state to consume `page.items` and `page.hasMore`.
- Kept task detail, metrics, queue, control, and audit endpoints unchanged.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `data` was still an array, then passed after adding `FixTaskPageVo`; a follow-up failure from shared test data was fixed by filtering pagination metadata tests to a dedicated repository, 29 tests run, 0 failures.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because `tasks.find` received a page object, then passed after using `page.items`.
- `npm test` in `frontend/`: passed, 13 tests run, 0 failures.

Implemented task list total count from `docs/plans/061-task-list-total-count.md`.

Changes:

- Added `total` to the `GET /api/tasks` task page response.
- Added `FixTaskService#countTasks(FixTaskListQuery)` so count logic can use the same filters as task listing without `limit` or `offset`.
- Implemented matching count behavior in both in-memory and MyBatis-backed task services.
- Updated the React dashboard task list to show loaded task count versus total matching count.
- Updated frontend API types and tests for the `total` response field.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_return_task_list_pagination_metadata test`: first failed because `$.data.total` was missing, then passed after adding the count field.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests#should_count_tasks_before_limit_and_offset,MyBatisFixTaskServiceTests#should_count_tasks_with_query_filters_without_limit_or_offset test`: passed, 2 tests run, 0 failures.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because the task list still rendered `2 visible tasks`, then passed after rendering `2 of 2 tasks visible`.
- `npm test -- src/api.test.ts`: passed, 1 test run, 0 failures.

Implemented dashboard failure cause summary from `docs/plans/062-dashboard-failure-cause-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/failure-causes`.
- Added `FixTaskFailureCauseSummaryVo`.
- Extended `FixTaskMetricsService` with `failureCauses()`.
- Classified failed task reasons into `MAVEN_TESTS`, `GITHUB_AUTH`, `MODEL_ERROR`, `SANDBOX_REJECTION`, and `UNKNOWN`.
- Rendered a React failure-cause summary panel in the dashboard.
- Added a frontend API helper for the new metrics endpoint.
- Forced full Spring Boot controller/application tests to use the `default` profile so local `.env` values such as `SPRING_PROFILES_ACTIVE=docker` do not activate MyBatis-backed services in unit test context.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_failure_cause,TaskControllerTests#should_get_task_failure_cause_summary test`: first failed because `failureCauses()` was missing, then failed once due to unstable ordering, then passed after adding the service method and fixed cause order.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because the dashboard did not render `Failure causes`, then passed after adding the panel.
- `mvn -pl PatchPilot test`: passed, 256 tests run, 0 failures.
- `SPRING_PROFILES_ACTIVE=docker mvn -pl PatchPilot clean -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test`: first reproduced the missing `FixTaskModelCallMapper` context failure, then passed after adding explicit `@ActiveProfiles("default")`.
- `SPRING_PROFILES_ACTIVE=docker mvn -pl PatchPilot clean test`: passed, 256 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 14 tests run, 0 failures.
- `npm run build` in `frontend/`: passed.

Implemented dashboard model cost summary from `docs/plans/063-dashboard-model-cost-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/model-usage`.
- Added `FixTaskModelUsageSummaryVo`.
- Extended `FixTaskMetricsService` with `modelUsage()`.
- Aggregated prompt tokens, completion tokens, total tokens, successful model calls, failed model calls, and estimated USD cost from recorded model-call rows.
- Added configurable model cost inputs under `patchpilot.agent.cost.prompt-token-usd` and `patchpilot.agent.cost.completion-token-usd`, both defaulting to `0`.
- Added a frontend API helper, type, and `ModelUsagePanel`.
- Rendered model usage next to failure causes in the dashboard operational summaries.
- Documented the model usage endpoint and dashboard cost summary in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_model_usage_and_estimated_cost,TaskControllerTests#should_get_task_model_usage_summary test`: passed, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getModelUsageSummary()` and `Model usage` UI were missing, then failed once on ambiguous `Completion` label, then passed after adding the API helper, panel, and clearer `Completion tokens` label, 15 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 258 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 15 tests run, 0 failures.
- `npm run build` in `frontend/`: passed.

Implemented dashboard latency summary from `docs/plans/064-dashboard-latency-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/latency`.
- Added `FixTaskLatencySummaryVo`.
- Extended `FixTaskMetricsService` with `latency()`.
- Aggregated completed task duration, model-call duration, tool-call duration, and test-run duration.
- Added a frontend API helper, type, and `LatencyPanel`.
- Rendered latency next to failure causes and model usage in the dashboard operational summaries.
- Documented the latency endpoint and dashboard latency summary in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_latency_across_tasks_model_calls_tool_calls_and_test_runs test`: first failed because `DefaultFixTaskMetricsService` did not accept a tool-call service and `FixTaskMetricsService#latency()` did not exist.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_latency_across_tasks_model_calls_tool_calls_and_test_runs,TaskControllerTests#should_get_task_latency_summary test`: passed after adding the latency VO, service method, HTTP endpoint, and tool-call duration aggregation, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getLatencySummary()` and `Latency` UI were missing, then passed after adding the API helper, panel, and dashboard wiring, 16 tests run, 0 failures.

Implemented dashboard configuration summary from `docs/plans/065-dashboard-configuration-summary.md`.

Changes:

- Added `GET /api/configuration/summary`.
- Added `ConfigurationSummaryVo` and `ConfigurationController`.
- Returned provider, model, base URL, workspace root, queue policy, model-cost configuration status, and secret configured/missing booleans.
- Kept API key, GitHub token, and webhook secret values out of the response.
- Added frontend `ConfigurationSummary` type and `getConfigurationSummary()` API helper.
- Added `ConfigurationPanel` to the dashboard, rendered above queue state.
- Documented the configuration endpoint and dashboard panel in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests test`: first failed with 404 because the endpoint did not exist, then passed after adding the controller and VO, 1 test run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getConfigurationSummary()` and `Configuration` UI were missing, then passed after adding the API helper, panel, and dashboard wiring, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 261 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 17 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard configuration health hints from `docs/plans/066-dashboard-configuration-health.md`.

Changes:

- Added `ConfigurationPanel` health evaluation for required secret status.
- Added advisory checks for missing model cost, invalid queue attempts, negative retry delay, and very low visibility timeout.
- Rendered `Configuration healthy`, setup issue counts, advisory counts, and terse issue rows.
- Updated the dashboard default test fixture to represent a healthy configuration.
- Documented configuration health hints in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because no health summary or issue rows existed, then passed after adding panel health evaluation and styles, 3 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 20 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard queue health hints from `docs/plans/067-dashboard-queue-health.md`.

Changes:

- Added `QueuePanel` health evaluation using existing queue summary data.
- Rendered `Queue has failures`, `Queue delayed`, `Queue active`, and `Queue idle` states.
- Added count details for failed, delayed, and running queue items.
- Preserved the existing queue summary cards and queue row list.
- Documented queue health hints in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/QueuePanel.test.tsx`: first failed because queue health labels did not exist, then passed after adding health evaluation and styles, 4 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 24 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard detail evidence summary from `docs/plans/068-dashboard-detail-evidence-summary.md`.

Changes:

- Added an `Execution evidence` strip to `TaskDetailPanel`.
- Summarized timeline, test-run, tool-call, and model-call counts from the existing detail summary response.
- Surfaced latest test status as `PASS`, `FAIL`, or `None` before the detailed Maven output.
- Added focused component coverage for populated evidence and missing latest test evidence.
- Documented the detail evidence strip in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the evidence strip did not exist, then passed after adding the component rendering and styles, 2 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 26 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.
