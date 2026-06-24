# Execution Log

This file records dated implementation progress, validation commands, and important outcomes.

## 2026-06-23

Implemented the post-edit model review gate from `docs/plans/134-post-edit-model-review-gate.md`.

Changes:

- Added `PatchReviewGenerator` to ask the configured model for JSON-only post-edit review decisions.
- Added patch review domain objects for `APPROVE` and `REJECT` decisions plus review generation failures.
- Extended `PlannedPatchWorkflow` so model-generated file edits are reviewed before writing to the workspace.
- Rejected edits now fail before file writes, preventing tests, commits, pushes, and Pull Request creation from continuing with a mismatched patch.
- Preserved the manual `/agent fix replace <path> <content>` smoke path without invoking the review gate.
- Added tests for review parsing, unsupported review decisions, approved edits, rejected edits, and Spring wiring.

Validation:

- `mvn -pl PatchPilot -Dtest=PatchReviewGeneratorTests,PlannedPatchWorkflowTests test`: first failed because the patch review generator/domain objects did not exist.
- `mvn -pl PatchPilot -Dtest=PatchReviewGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,PatchPilotApplicationTests test`: passed after implementation and Spring wiring updates, 21 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 488 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

## 2026-06-23

Implemented model-generated file edits from `docs/plans/133-model-generated-file-edits.md`.

Changes:

- Added a `FileEditPlanGenerator` that asks the configured model for JSON-only full-file edits.
- Added file edit domain records for edit context, proposed edits, edit plans, and edit generation failures.
- Extended `PlannedPatchWorkflow` so `/agent fix` can apply model-generated edits when no manual `replace` instruction is present.
- Preserved the existing `/agent fix replace <path> <content>` workflow for smoke tests and demos.
- Added workflow guards so generated edits can only touch fix-plan target files and cannot modify sensitive paths such as `.env`, `.git`, GitHub workflows, or private key files.
- Added tests for edit-plan parsing, manual replace compatibility, generated edit application, unauthorized paths, sensitive paths, and blank generated content.

Validation:

- `mvn -pl PatchPilot -Dtest=FileEditPlanGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests test`: first failed because the new edit generator/domain objects did not exist, then passed after implementation, 13 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=FileEditPlanGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,PatchPilotApplicationTests test`: passed after Spring bean wiring updates, 20 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 484 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

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

Implemented dashboard task deep links from `docs/plans/069-dashboard-task-deep-links.md`.

Changes:

- Initialized selected task state from the `taskId` URL query parameter.
- Updated `taskId` in the URL when operators select a task row.
- Preserved existing fallback behavior when no matching task is loaded.
- Documented dashboard task deep links in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "taskId URL parameter"`: first failed because URL task selection and URL updates did not exist, then passed after adding URL-backed task selection, 2 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 28 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented configurable frontend backend URL from `docs/plans/070-configurable-frontend-backend-url.md`.

Changes:

- Added a Vite proxy target helper that defaults to `http://127.0.0.1:8080`.
- Allowed `PATCHPILOT_FRONTEND_BACKEND_URL` and `VITE_PATCHPILOT_BACKEND_URL` to override the frontend dev proxy target.
- Documented IDEA `18080` frontend proxy usage in `.env.example`, README, and frontend design docs.

Validation:

- `npx vitest run --config vitest.config.ts viteProxy.test.ts`: first failed because `backendProxyTarget` did not exist, then passed after adding the helper, 3 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 31 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Follow-up fix:

- Loaded the repository root `.env` from Vite config so `PATCHPILOT_FRONTEND_BACKEND_URL` works when `npm run dev` is launched inside `frontend/`.
- Added coverage for parsing the frontend backend URL from `.env` content.

Validation:

- `npx vitest run --config vitest.config.ts viteProxy.test.ts`: first failed because `.env` parsing was not implemented, then passed after adding repository `.env` loading, 4 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 32 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard API error guidance from `docs/plans/071-dashboard-api-error-guidance.md`.

Changes:

- Added a shared frontend API request helper for GET and POST calls.
- Converted fetch failures and JSON parsing failures into an actionable backend/proxy guidance message.
- Preserved backend-provided JSON error messages for valid PatchPilot error envelopes.
- Added API tests for empty and non-JSON responses.
- Documented the dashboard backend/proxy error behavior in README and frontend design docs.

Validation:

- `npm test -- --run src/api.test.ts`: first failed because raw JSON parsing errors surfaced, then passed after adding guarded response parsing, 7 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 34 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard copy task link from `docs/plans/072-dashboard-copy-task-link.md`.

Changes:

- Added a `Copy link` action to `TaskDetailPanel`.
- Generated shareable selected-task links from the current dashboard URL by setting `taskId`.
- Preserved existing query parameters when adding or replacing `taskId`.
- Added a short success or failure status after clipboard writes.
- Documented the copyable task link in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because `taskLinkFor` and the `Copy link` button did not exist, then passed after adding link generation and clipboard behavior, 4 tests run, 0 failures.

Implemented dashboard backend health status from `docs/plans/073-dashboard-backend-health-status.md`.

Changes:

- Added a frontend `BackendHealth` type and `getBackendHealth()` helper for `GET /health`.
- Loaded backend health during dashboard refresh.
- Displayed backend status, service name, and timestamp in `ConfigurationPanel`.
- Added an unavailable backend state when health data is not loaded.
- Documented backend health visibility in README and frontend design docs.

Validation:

- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because `getBackendHealth()` and backend health UI did not exist, then passed after adding the API helper and panel rendering, 11 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx`: first failed because a custom App test fetch mock did not handle `/health`, then passed after adding the health fixture, 14 tests run, 0 failures.

Implemented dashboard refresh state from `docs/plans/074-dashboard-refresh-state.md`.

Changes:

- Disabled the top-level refresh button while dashboard refresh is in flight.
- Changed the refresh button label and accessible name to `Refreshing`.
- Added a compact `Dashboard refreshing` status region during top-level data loading.
- Documented refresh progress feedback in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "dashboard refresh progress"`: first failed because no refresh status region or disabled refreshing button existed, then passed after adding refresh state UI, 1 test run, 0 failures.

Implemented dashboard last refresh time from `docs/plans/075-dashboard-last-refresh-time.md`.

Changes:

- Tracked a `lastRefreshedAt` timestamp after successful top-level dashboard refreshes.
- Rendered `Last refreshed` under the dashboard title.
- Added a reusable compact date-time formatter for title-level timestamps.
- Documented last-refresh feedback in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard"`: first failed because no `Last refreshed` timestamp existed, then passed after adding the refresh timestamp state and title rendering, 1 test run, 0 failures.

Implemented task detail aggregate API from `docs/plans/076-task-detail-aggregate-api.md`.

Changes:

- Added `GET /api/tasks/{taskId}/detail` to return the selected task audit summary, timeline events, test runs, tool calls, and model calls in one response.
- Preserved the existing narrower task detail endpoints for direct debugging.
- Added frontend `FixTaskDetail` typing and `getTaskDetail()`.
- Switched the dashboard selected-task loader from five detail requests to the aggregate detail endpoint.
- Documented the aggregate task detail endpoint in README and the frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id,TaskControllerTests#should_return_404_for_missing_task_detail test`: first failed because `/api/tasks/{taskId}/detail` did not exist, then passed after adding the response type and controller endpoint, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/App.test.tsx`: first failed because `getTaskDetail()` did not exist and the dashboard still called five detail endpoints, then passed after adding the helper and switching the selected-task loader, 24 tests run, 0 failures.

Implemented task detail queue status from `docs/plans/077-task-detail-queue-status.md`.

Changes:

- Extended `FixTaskQueueQueryService` with `findByTaskId(String taskId)`.
- Implemented MyBatis-backed task queue lookup by task id, returning the latest queue item by update time.
- Included the selected task's latest queue item in `GET /api/tasks/{taskId}/detail`.
- Added frontend task-detail typing for optional queue item data.
- Rendered queue status, attempt count, last error, available time, and locked time in `TaskDetailPanel`.
- Documented selected-task queue visibility in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id test`: first failed at test compilation because `findByTaskId` did not exist, then passed after adding the service method, queue lookup, and detail response field.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the task detail panel did not render queue state, then passed after adding queue item typing and rendering, 14 tests run, 0 failures.

Implemented task detail queue history from `docs/plans/078-task-detail-queue-history.md`.

Changes:

- Extended `FixTaskQueueQueryService` with `listByTaskId(String taskId)`.
- Reused the task-scoped queue list to keep `queueItem` as the latest queue record and return `queueItems` as the full selected-task queue history.
- Included queue history in `GET /api/tasks/{taskId}/detail`.
- Added frontend task-detail typing for queue history.
- Rendered a `Queue History` section with queue item id, status, attempt count, available time, locked time, and last error.
- Documented task-detail queue history in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id,MyBatisFixTaskQueueQueryServiceTests#should_list_queue_items_by_task_id test`: first failed because `listByTaskId` did not exist, then passed after adding the service method and aggregate detail response field, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because `Queue History` was not rendered, then passed after adding queue history rendering and scoped assertions, 15 tests run, 0 failures.

Implemented task report copy from `docs/plans/079-task-report-copy.md`.

Changes:

- Added `GET /api/tasks/{taskId}/report` to return a Markdown task diagnostic report.
- Built the report from aggregate task records: metadata, status, failure reason, queue state, timeline, test runs, tool calls, and model calls.
- Added frontend `getTaskReport(taskId)`.
- Added a `Copy report` action to selected task details.
- Wired the dashboard action through `App` so report content is fetched from the backend and copied to the clipboard.
- Documented the report endpoint and dashboard copy action in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id,TaskControllerTests#should_return_404_for_missing_task_report test`: first failed because `/api/tasks/{taskId}/report` did not exist, then passed after adding the endpoint and report generator, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: first failed because `getTaskReport`, the `Copy report` button, and App wiring did not exist, then passed after adding the API helper and UI flow, 33 tests run, 0 failures.

Implemented dashboard manual task creation from `docs/plans/080-dashboard-manual-task-creation.md`.

Changes:

- Added `POST /api/tasks` for manual dashboard-created tasks.
- Added `CreateFixTaskDto`, `CreateManualFixTaskCommand`, and `ManualFixTaskService`.
- Kept manual creation on the same durable task, timeline, dispatcher, and queue path as webhook-created work.
- Rejected invalid manual task requests and duplicate active work for the same repository issue.
- Added frontend `createTask()` and a `ManualTaskForm`.
- Wired the dashboard form to create a task, select the created task id, refresh dashboard data, show success, and preserve form values on creation failure.
- Documented manual task creation in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_create_manual_task_and_dispatch_it,TaskControllerTests#should_return_bad_request_for_invalid_manual_task_request,TaskControllerTests#should_return_conflict_when_manual_task_already_active_for_issue test`: first failed because `POST /api/tasks` returned 405, then passed after adding the endpoint and manual task service, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_create_manual_task_and_dispatch_it,TaskControllerTests#should_return_bad_request_for_invalid_manual_task_request,TaskControllerTests#should_return_conflict_when_manual_task_already_active_for_issue,DefaultManualFixTaskServiceTests test`: passed after isolating controller tests from async worker execution and adding service-level coverage, 5 tests run, 0 failures.
- `npm test -- src/api.test.ts src/App.test.tsx -t "creates manual task|manual task creation"`: first failed because `createTask` and the manual form did not exist, then failed once because handled creation errors still surfaced as unhandled rejections, then passed after adding the API helper, form, App wiring, and handled-error preservation, 2 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 272 tests run, 0 failures.
- `cd frontend && npm test`: passed, 47 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard task detail route from `docs/plans/081-dashboard-task-detail-route.md`.

Changes:

- Added `/tasks/{taskId}` as the canonical selected-task dashboard route.
- Kept legacy `?taskId={taskId}` URLs compatible for previously copied links.
- Updated task selection to write path-based routes while preserving unrelated query parameters and hash fragments.
- Updated copyable task links to generate `/tasks/{taskId}` URLs.
- Documented route-based task deep links in README and frontend design docs.

Validation:

- `npm test -- src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx -t "task detail route|taskId URL parameter|selected task route|shareable task link|deep link"`: first failed because the dashboard still only read and wrote `?taskId=...`, then passed after adding path-route parsing, writing, and copy-link generation, 6 tests run, 0 failures.
- `cd frontend && npm test`: passed, 49 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard filter URL state from `docs/plans/082-dashboard-filter-url-state.md`.

Changes:

- Initialized dashboard status and search filters from `status` and `query` URL parameters.
- Treated invalid `status` URL values as `ALL`.
- Updated status filter changes to write `status` to the URL and remove `status=ALL`.
- Updated search changes to write `query` to the URL and remove it when cleared.
- Preserved selected task routes, unrelated query parameters, and hash fragments while syncing filter state.
- Documented URL-backed filtered investigation views in README and frontend design docs.

Validation:

- `npm test -- src/App.test.tsx -t "filter URL state|task detail route with filters|syncs status filter|syncs search query|removes cleared search"`: first failed because the dashboard ignored URL filter state and did not write filter changes back to the URL, then passed after adding filter parsing and URL sync helpers, 4 tests run, 0 failures.
- `cd frontend && npm test`: passed, 53 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard filter reset action from `docs/plans/083-dashboard-filter-reset-action.md`.

Changes:

- Added a `Clear filters` action to the dashboard task list that appears only when status or search filters are active.
- Reset status to `ALL`, cleared the search input, removed `status` and `query` from the URL, and preserved the selected `/tasks/{taskId}` route, unrelated query parameters, and hash fragments.
- Let the existing dashboard refresh effect reload the default `GET /api/tasks?limit=50` task page after clearing filters.
- Added responsive task-search layout styling so the reset action stays aligned on desktop and wraps cleanly on narrow screens.
- Documented the reset behavior in README and frontend design notes.

Validation:

- `npm test -- src/App.test.tsx -t "clear filters"`: first failed because the dashboard did not expose a `Clear filters` button, then passed after adding the reset action and URL cleanup behavior, 2 tests run, 0 failures.
- `cd frontend && npm test`: passed, 55 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard sort control from `docs/plans/084-dashboard-sort-control.md`.

Changes:

- Added backend task-list sorting through `GET /api/tasks?sort=createdAtDesc|createdAtAsc`, with newest-first as the default.
- Added `FixTaskSort` and carried sort direction through `FixTaskListQuery`, in-memory task listing, and MyBatis-backed task listing.
- Kept sorting before offset/limit pagination so `Load more` continues to page through a stable order.
- Rejected invalid backend sort values with `sort must be createdAtDesc or createdAtAsc`.
- Added a dashboard `Sort tasks` control with `Newest first` and `Oldest first` options.
- Stored non-default sort state as `sort=createdAtAsc` in the URL, restored valid sort state on load, and ignored invalid frontend sort values by falling back to newest-first.
- Preserved active sort when clearing status/search filters and included sort in load-more requests.
- Documented task-list sort behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_sort_tasks_oldest_first,TaskControllerTests#should_return_bad_request_for_invalid_task_list_sort,InMemoryFixTaskServiceTests#should_list_tasks_oldest_first_when_requested,MyBatisFixTaskServiceTests#should_list_tasks_oldest_first_when_requested test`: first failed because controller ignored `sort` and services always returned newest-first, then passed after parsing and applying sort, 4 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "sort|offset pagination"`: first failed because the dashboard had no `Sort tasks` control and did not include sort in requests, then passed after adding API sort parameters, URL state, the control, and load-more propagation, 6 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 276 tests run, 0 failures.
- `cd frontend && npm test`: passed, 59 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard repository filters from `docs/plans/085-dashboard-repository-filters.md`.

Changes:

- Added task-list repository owner and repository name filters to the dashboard.
- Passed trimmed `repositoryOwner` and `repositoryName` values from the frontend API helper to `GET /api/tasks`.
- Restored repository filters from the URL and kept them synchronized while preserving selected task routes, existing status/search/sort state, unrelated query parameters, and hash fragments.
- Included repository filters in `Load more` pagination requests.
- Updated `Clear filters` to reset status, search, repository owner, and repository name while preserving active sort state.
- Added a repository-specific empty state for task lists narrowed only by repository filters.
- Documented repository-filter behavior in README and frontend design notes.

Validation:

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "repository filter|backend task search sort|offset pagination"`: first failed because `listTasks` omitted `repositoryOwner` and `repositoryName`, the task list had no repository filter controls, and pagination did not carry repository filters; then passed after adding API parameters, URL state, task-list controls, reset behavior, and pagination propagation, 5 tests run, 0 failures.
- `cd frontend && npm test`: passed, 62 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.
- `mvn -pl PatchPilot test`: passed, 276 tests run, 0 failures.

Implemented dashboard created time filters from `docs/plans/086-dashboard-created-time-filters.md`.

Changes:

- Added optional backend `createdAfter` and `createdBefore` task-list filters using ISO-8601 instant values.
- Applied inclusive created-time filtering in both in-memory and MyBatis-backed task listing.
- Returned a parameter-specific HTTP 400 response for invalid created-time filter values.
- Passed trimmed created-time filter values from the frontend API helper to `GET /api/tasks`.
- Added `Filter created after` and `Filter created before` controls to the dashboard task list.
- Restored created-time filters from the URL and kept them synchronized with status, search, repository, selected task route, hash fragments, and non-default sort state.
- Included created-time filters in refresh and `Load more` pagination requests.
- Updated `Clear filters` to reset status, search, repository, and created-time filters while preserving active sort state.
- Documented created-time filter behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_filter_tasks_by_created_time_range+should_return_bad_request_for_invalid_created_time_filter,InMemoryFixTaskServiceTests#should_list_tasks_with_created_time_range,MyBatisFixTaskServiceTests#should_list_tasks_with_created_time_range test`: first failed because task-list queries had no created-time fields and services ignored created time; then passed after adding backend parsing and service filters, 4 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "created time filter|backend task search sort|offset pagination"`: first failed because `listTasks` omitted `createdAfter`/`createdBefore`, the dashboard had no created-time controls, and pagination did not carry created-time filters; then passed after adding API parameters, URL state, task-list controls, reset behavior, and pagination propagation, 5 tests run, 0 failures.
- `cd frontend && npm test`: passed, 65 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `mvn -pl PatchPilot test`: passed, 280 tests run, 0 failures.
- `git diff --check`: passed with no whitespace errors.

Implemented dashboard status filter counts from `docs/plans/087-dashboard-status-filter-counts.md`.

Changes:

- Added `GET /api/tasks/status-counts` for total and per-status task counts.
- Reused the existing task-list query model for search, repository, and created-time count scopes.
- Kept status counts independent from the active status filter, sort, limit, and offset.
- Returned parameter-specific HTTP 400 responses for invalid created-time count filters.
- Added a frontend `getTaskStatusCounts()` API helper and `FixTaskStatusCounts` type.
- Loaded status counts during dashboard refresh and rendered count badges on status filter buttons.
- Preserved status button accessible names as the status labels while showing visual count badges.
- Documented scoped status count behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_count_tasks_by_status_for_filtered_scope+should_return_bad_request_for_invalid_status_count_created_time_filter test`: first failed because `/api/tasks/status-counts` did not exist and was handled as a task id route, then passed after adding the endpoint and status count response, 2 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "status count|status filter counts"`: first failed because `getTaskStatusCounts()` did not exist and status buttons had no count badges, then passed after adding the API helper, dashboard refresh wiring, and button badges, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 6 test files and 68 tests.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented safety gate and language adapter foundation from `docs/plans/088-safety-gate-language-adapter-foundation.md`.

Changes:

- Added a `CommandSafetyGate` that accepts supported `/agent fix` commands and rejects unsafe destructive, secret-exfiltration, or arbitrary shell style instructions before task creation.
- Added `REJECTED` webhook handling so unsafe GitHub comments return a non-task result instead of creating or dispatching work.
- Applied the same safety gate to dashboard-created manual tasks so manual API calls cannot bypass webhook command checks.
- Added a `LanguageAdapter` boundary, `LanguageDetectionResult`, and `JavaMavenLanguageAdapter`.
- Routed `MavenTestRunner` through the Java/Maven adapter for Maven wrapper and `pom.xml` detection while preserving the existing allowlisted `./mvnw test` and `mvn test` behavior.
- Documented the safety gate and adapter boundary in README and architecture docs.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_command_is_unsafe,JavaMavenLanguageAdapterTests test`: first failed because language adapter types did not exist and `REJECTED` was not a webhook status, then passed after adding the adapter and safety gate wiring, 5 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests#should_run_system_maven_when_only_pom_exists test`: first failed because `MavenTestRunner` did not accept the Java/Maven adapter dependency, then passed after routing detection through `JavaMavenLanguageAdapter`.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests,JavaMavenLanguageAdapterTests test`: passed, 13 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,GitHubWebhookControllerTests#should_reject_dangerous_agent_fix_issue_comment,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_command_is_unsafe,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_unsafe test`: passed, 4 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 6 test files and 68 tests.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.

Implemented authorized trigger policy from `docs/plans/089-authorized-trigger-policy.md`.

Changes:

- Added `SafetyProperties` for optional trigger-user and repository allowlists.
- Added `SafetyGateRequest` so safety decisions can use repository owner, repository name, trigger user, and trigger comment together.
- Extended `CommandSafetyGate` to reject unsafe commands first, then reject trigger users or repositories outside configured allowlists.
- Applied the same authorization policy to GitHub webhooks and dashboard/manual task creation before task creation or dispatch.
- Added environment variables `PATCHPILOT_ALLOWED_TRIGGER_USERS` and `PATCHPILOT_ALLOWED_REPOSITORIES`.
- Documented allowlist configuration and updated the safety architecture notes.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_agent_fix_from_unauthorized_trigger_user_before_task_creation+should_reject_agent_fix_for_unauthorized_repository_before_task_creation+should_accept_agent_fix_when_trigger_user_and_repository_are_allowed,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_trigger_user_is_not_allowed+should_reject_manual_task_when_repository_is_not_allowed,TaskControllerTests#should_return_bad_request_when_manual_task_trigger_user_is_not_allowed+should_return_bad_request_when_manual_task_repository_is_not_allowed test`: first failed because `SafetyProperties` did not exist.
- The same target command then failed because Spring selected the no-arg safety gate constructor, so controller tests still created tasks for unauthorized inputs.
- The same target command passed after adding configuration binding and constructor injection, 7 tests run, 0 failures.

Implemented rejected trigger audit log from `docs/plans/090-rejected-trigger-audit-log.md`.

Changes:

- Added `RejectedTriggerAuditService` with in-memory and MyBatis implementations.
- Added `rejected_trigger_audit` MySQL migration for rejected `/agent fix` attempts that do not become tasks.
- Added `GET /api/rejected-triggers` with bounded `limit` validation.
- Recorded rejected webhook triggers with source, delivery id, repository, issue number, trigger user, command, reason, and timestamp.
- Recorded rejected manual task creation attempts through the same audit service.
- Documented rejected trigger inspection and the separation between rejected triggers and executable task records.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,TaskControllerTests#should_return_bad_request_when_manual_task_trigger_user_is_not_allowed test`: first failed because the rejected trigger audit model, service, controller, mapper, and migration did not exist; then passed after implementation, 9 tests run, 0 failures.

Implemented actionable command classification from `docs/plans/091-actionable-command-classification.md`.

Changes:

- Added deterministic actionability checks to `CommandSafetyGate`.
- Rejected empty or vague trigger comments before task creation.
- Kept clear patch operations, likely file references, and concrete failure descriptions actionable.
- Routed vague webhook and manual API triggers into the existing rejected trigger audit path.
- Updated trigger examples and safety gate documentation.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,GitHubWebhookServiceTests#should_reject_unactionable_agent_fix_command_before_task_creation,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_not_actionable test`: first failed because vague commands still created tasks; then passed after adding actionability classification, 9 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,GitHubWebhookServiceTests,GitHubWebhookControllerTests,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_not_actionable test`: passed, 27 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.

Implemented model-assisted trigger classification from `docs/plans/092-model-assisted-trigger-classification.md`.

Changes:

- Added optional model-assisted trigger classification after deterministic safety checks and before task creation.
- Added `TriggerIntentClassifier`, model-backed classification request/decision types, and a disabled-by-default no-op path.
- Reused the existing OpenAI-compatible `ModelProviderClient` for classification JSON.
- Rejected model-declined webhook and manual triggers through the existing rejected trigger audit log.
- Added `PATCHPILOT_MODEL_TRIGGER_CLASSIFICATION_ENABLED` to configuration, `.env.example`, and Docker Compose.
- Documented that model classification cannot override deterministic safety rejections.

Validation:

- `mvn -pl PatchPilot -Dtest=ModelTriggerIntentClassifierTests,GitHubWebhookServiceTests#should_reject_when_model_trigger_classifier_declines_execution_before_task_creation+should_not_call_model_trigger_classifier_for_dangerous_command_rejected_by_safety_gate,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_model_trigger_classifier_declines_execution test`: first failed because trigger intent classification types and services did not exist; then passed after implementation, 8 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests test`: passed after adding `modelTriggerClassificationEnabled` to the non-sensitive configuration summary, 1 test run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx -t "configuration|Configuration"`: passed after surfacing trigger classifier state in the configuration panel, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 68 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented trigger rate limit abuse guard from `docs/plans/093-trigger-rate-limit-abuse-guard.md`.

Changes:

- Added `TriggerRateLimitService` with an in-memory sliding-window implementation for local self-hosted runs.
- Added per-trigger-user, per-repository, and per-issue thresholds under `patchpilot.safety`.
- Applied rate-limit checks to GitHub webhooks and manual dashboard task creation after deterministic safety checks and active-task deduplication, but before model trigger classification and task creation.
- Routed rate-limited rejections into the rejected trigger audit log.
- Added `PATCHPILOT_TRIGGER_RATE_LIMIT_*` environment variables to `.env.example`, application configuration, and Docker Compose.
- Exposed rate-limit state through `/api/configuration/summary` and the dashboard configuration panel.
- Documented the operator-facing behavior and current in-memory single-instance limitation.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryTriggerRateLimitServiceTests,GitHubWebhookServiceTests#should_reject_when_trigger_rate_limit_is_exceeded_before_task_creation,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_trigger_rate_limit_is_exceeded,ConfigurationControllerTests test`: first failed because the trigger rate-limit types and service did not exist; then failed on a record factory/accessor naming conflict; then failed until Spring constructor injection was explicit; passed after implementation, 6 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx -t "configuration|Configuration"`: passed after surfacing trigger rate-limit settings in the dashboard configuration panel, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 325 tests run, 0 failures.
- `cd frontend && npm test -- --reporter=dot`: passed, 68 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.

Implemented unsupported repository preflight from `docs/plans/094-unsupported-repository-preflight.md`.

Changes:

- Added `LanguageAdapterRegistry` to select the first supported repository language adapter.
- Ran language-adapter detection immediately after workspace preparation and before patch workflow, diff, tests, commit, push, or Pull Request creation.
- Recorded the preflight as an audited `LanguageAdapterRegistry` tool call.
- Failed unsupported repositories with `Unsupported repository: no supported language adapter detected`.
- Kept Java/Maven as the only supported execution adapter for now and documented the boundary for future Gradle, Node.js, and Python adapters.
- Updated executor tests, product specification, architecture docs, README supported-repository notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests#should_fail_unsupported_repository_before_patch_workflow_or_tests test`: first failed because `LanguageAdapterRegistry` did not exist; then passed after adding the registry and executor preflight.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests,JavaMavenLanguageAdapterTests,WorkspaceFixTaskExecutorTests test`: first failed because existing executor tests expected the old tool-call sequence and cancellation checkpoint numbers; then passed after updating expectations for the new preflight step, 14 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the webhook completion fixture did not create a Maven marker file, so the new preflight correctly failed it as unsupported; then passed after creating `pom.xml` in the test repository fixture, 328 tests run, 0 failures.
- `git diff --check`: passed with no whitespace errors.

Implemented adapter-driven verification runner from `docs/plans/095-adapter-driven-verification-runner.md`.

Changes:

- Added `VerificationRunner` for controlled execution of adapter-provided verification commands.
- Kept `MavenTestRunner` as a Java/Maven compatibility wrapper while delegating process execution to `VerificationRunner`.
- Changed task execution to use the `LanguageDetectionResult.verificationCommand()` selected during language preflight instead of re-running Maven detection inside the runner.
- Preserved command allowlist validation, timeout behavior, task process registration, and PatchPilot environment sanitization.
- Updated runner tests, executor tests, webhook integration test wiring, README, architecture docs, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=VerificationRunnerTests,WorkspaceFixTaskExecutorTests#should_prepare_task_repository_and_run_maven_tests test`: first failed because `VerificationRunner` did not exist; then passed after adding the runner and switching executor verification to the adapter command, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=VerificationRunnerTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests#should_dispatch_created_task_to_completion test`: passed after replacing executor/webhook test doubles with `VerificationRunner`, 12 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the Maven compatibility test still expected `maven test command timed out`; then passed after updating the timeout wording to the generic verification runner message, 330 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests,MavenTestRunnerTests,VerificationRunnerTests test`: passed after generic interruption wording cleanup, 21 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after final verification, 330 tests run, 0 failures.

Implemented Gradle language adapter support from `docs/plans/096-gradle-language-adapter.md`.

Changes:

- Added `JavaGradleLanguageAdapter` for Java/Gradle repositories with `gradlew`, `build.gradle`, or `build.gradle.kts`.
- Selected `./gradlew test` when the Gradle wrapper exists and `gradle test` when only Gradle build files exist.
- Registered Maven and Gradle adapters with deterministic Spring ordering.
- Extended `CommandExecutionGuard` to allow only the fixed Gradle verification commands, not arbitrary Gradle tasks.
- Verified the generic `VerificationRunner` can execute an adapter-provided Gradle wrapper command.
- Updated README, product specification, architecture, and backend command-execution standard to describe Maven and Gradle support.

Validation:

- `mvn -pl PatchPilot -Dtest=JavaGradleLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests test`: first failed because `JavaGradleLanguageAdapter` did not exist; then passed after adding the adapter and Gradle command allowlist, 10 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=JavaGradleLanguageAdapterTests,JavaMavenLanguageAdapterTests,LanguageAdapterRegistryTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests test`: passed after Spring adapter registration checks, 18 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 335 tests run, 0 failures.

Implemented Node/npm language adapter support from `docs/plans/097-node-npm-language-adapter.md`.

Changes:

- Added `NodeNpmLanguageAdapter` for Node.js repositories with `package.json` and a non-empty `scripts.test`.
- Selected the fixed verification command `npm test` for supported Node/npm repositories.
- Rejected missing, invalid, or no-test-script `package.json` files before patch generation or Git mutation.
- Registered the Node/npm adapter after the Java/Maven and Java/Gradle adapters.
- Extended `CommandExecutionGuard` to allow only `npm test`, not arbitrary npm scripts.
- Added Node/npm to the backend runtime Docker image so Docker Compose can execute Node verification.
- Updated README, product specification, architecture, target-state, roadmap, decisions, and backend command-execution standard.

Validation:

- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests test`: first failed because `NodeNpmLanguageAdapter` did not exist; then passed after adding the adapter, npm command allowlist, and Spring registration, 15 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests test`: first failed because the runtime Dockerfile did not install `nodejs npm`; then passed after adding them to the runtime image.
- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after runtime packaging verification, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 341 tests run, 0 failures.

Implemented Python/pytest language adapter support from `docs/plans/098-python-pytest-language-adapter.md`.

Changes:

- Added `PythonPytestLanguageAdapter` for Python repositories with `pytest.ini`, `[tool.pytest.ini_options]` in `pyproject.toml`, or pytest in `requirements.txt`.
- Selected the fixed verification command `python3 -m pytest` for supported Python/pytest repositories.
- Rejected Python repositories without pytest configuration or dependency before patch generation or Git mutation.
- Registered the Python/pytest adapter after Java/Maven, Java/Gradle, and Node/npm adapters.
- Extended `CommandExecutionGuard` to allow only `python3 -m pytest`, not arbitrary Python commands or pytest arguments.
- Added Python and pytest to the backend runtime Docker image so Docker Compose can execute Python verification.
- Updated README, product specification, architecture, target-state, roadmap, decisions, and backend command-execution standard.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonPytestLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `PythonPytestLanguageAdapter` did not exist; then failed because local `python3` lacked pytest; then passed after using a local module fixture for command-path verification and adding the adapter, command allowlist, Spring registration, and runtime packaging, 19 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 347 tests run, 0 failures.

Implemented adapter-aware task metadata from `docs/plans/099-adapter-aware-task-metadata.md`.

Changes:

- Added nullable task metadata fields for selected `language`, `buildSystem`, and `verificationCommand`.
- Added a database migration for adapter metadata on `fix_task`.
- Recorded adapter metadata immediately after successful language-adapter detection.
- Exposed adapter metadata through task API responses by extending `FixTaskVo`.
- Showed adapter metadata in dashboard task rows and selected task detail.
- Renamed task detail test output labels from Maven-specific wording to generic verification wording.
- Updated README, product specification, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,WorkspaceFixTaskExecutorTests,FixTaskAdapterMetadataMigrationTests test`: first failed because the entity, VO, conversion method, service method, executor injection, and migration did not exist; then passed after implementation, 48 tests run, 0 failures.
- `npm test -- --run App.test.tsx TaskDetailPanel.test.tsx`: first failed because the dashboard did not render adapter metadata; then passed after adding the task row/detail display and generic verification labels, 46 tests run, 0 failures.

Implemented adapter filtering and metrics from `docs/plans/100-adapter-filtering-and-metrics.md`.

Changes:

- Added optional `language` and `buildSystem` fields to `FixTaskListQuery` while preserving existing constructors.
- Applied adapter filters in in-memory and MyBatis task list/count queries.
- Included adapter metadata in broad task search text.
- Accepted adapter filters in `GET /api/tasks` and `GET /api/tasks/status-counts`.
- Added scoped metrics overloads so summary, failure causes, model usage, and latency can use the same investigation scope as the task list.
- Accepted search, repository, adapter, and created-time filters in task metrics endpoints.
- Added dashboard language and build-system filters with URL restore/sync, count and metrics propagation, clear-filter support, and load-more propagation.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests#should_list_tasks_with_adapter_metadata_filters,MyBatisFixTaskServiceTests#should_list_tasks_with_adapter_metadata_filters,TaskControllerTests#should_filter_tasks_and_status_counts_by_adapter_metadata test`: first failed because `FixTaskListQuery` did not support adapter fields; then passed after adding query fields and service/controller filters, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_only_tasks_matching_query_scope,TaskControllerTests#should_get_task_metrics_summary_for_adapter_scope test`: first failed because metrics had only global no-argument methods; then passed after adding query-scoped metrics, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx -t "adapter filter|builds backend task search|builds backend task status count|current adapter scope"`: first failed because frontend API requests omitted `language` and `buildSystem` and the dashboard had no adapter controls; then passed after adding API parameters, URL state, task-list controls, metrics propagation, and clear-filter behavior, 6 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: passed after focused backend verification, 92 tests run, 0 failures.
- `npm test`: passed after frontend verification, 73 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 356 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Node/pnpm and Node/yarn package-manager adapter support from `docs/plans/101-node-package-manager-adapters.md`.

Changes:

- Added shared Node package-manager detection for `package.json` parsing and `scripts.test` validation.
- Added `NodePnpmLanguageAdapter` for repositories with `package.json`, `pnpm-lock.yaml`, and a non-empty `scripts.test`.
- Added `NodeYarnLanguageAdapter` for repositories with `package.json`, `yarn.lock`, and a non-empty `scripts.test`.
- Preferred pnpm and yarn adapters before the broader npm adapter when package-manager lockfiles are present.
- Extended `CommandExecutionGuard` to allow only `pnpm test` and `yarn test`, not arbitrary package-manager scripts or install commands.
- Added pnpm and yarn to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Updated README, product specification, architecture, target-state, backend command standard, decisions, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=NodePnpmLanguageAdapterTests,NodeYarnLanguageAdapterTests,CommandExecutionGuardTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `NodePnpmLanguageAdapter` and `NodeYarnLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, Spring registration/order checks, and runtime packaging, 21 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,NodePnpmLanguageAdapterTests,NodeYarnLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after focused adapter and runner verification, 30 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 367 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Python/Poetry and Python/uv project-runner adapter support from `docs/plans/102-python-project-runner-adapters.md`.

Changes:

- Added shared Python pytest-signal detection for pytest configuration and dependency checks.
- Added `PythonPoetryLanguageAdapter` for `[tool.poetry]` projects with pytest configuration or dependency.
- Added `PythonUvLanguageAdapter` for `uv.lock` projects with pytest configuration or dependency.
- Preferred Poetry and uv adapters before the broad Python/pytest adapter when project-manager signals are present.
- Extended `CommandExecutionGuard` to allow only `poetry run pytest` and `uv run pytest`, not install, sync, pip, lock, or arbitrary runner commands.
- Added Poetry and uv to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Updated README, product specification, architecture, target-state, roadmap, backend command standard, decisions, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonPoetryLanguageAdapterTests,PythonUvLanguageAdapterTests,CommandExecutionGuardTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `PythonPoetryLanguageAdapter` and `PythonUvLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, Spring registration/order checks, and runtime packaging, 24 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=PythonPytestLanguageAdapterTests,PythonPoetryLanguageAdapterTests,PythonUvLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after focused Python adapter and runner verification, 33 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 378 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter demo fixtures from `docs/plans/103-adapter-demo-fixtures.md`.

Changes:

- Added minimal fixtures under `docs/demo-repositories/` for Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, Python/pytest, Python/Poetry, and Python/uv.
- Added per-fixture README files that document the expected adapter and verification command.
- Added source and test files to each fixture so the examples are understandable as small repositories instead of bare manifests.
- Added a registry-level backend test that verifies each fixture is detected with the expected `language`, `buildSystem`, and verification command.
- Updated README, roadmap, target-state, and this execution log to make the fixtures a documented demo-readiness asset.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures test`: first failed because `docs/demo-repositories/java-maven` did not exist; then failed because Maven module test execution needed a repository-root-aware fixture path; then passed after adding fixtures and root-relative path resolution, 1 test run, 0 failures.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests test`: passed after full registry verification, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 379 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter smoke script from `docs/plans/104-adapter-smoke-script.md`.

Changes:

- Added `scripts/adapter-smoke.sh` as a safe local adapter detection smoke command.
- Added default detection mode that prints the fixture matrix and runs `LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures`.
- Added `--backend` mode for wider adapter and command-guard coverage.
- Added `docs/agent/adapter-smoke-checklist.md` with scope, commands, expected results, and non-goals.
- Added `AdapterSmokeScriptTests` to keep the script pointed at adapter tests and away from GitHub, model, Docker, push, and webhook operations.
- Updated README and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=AdapterSmokeScriptTests test`: first failed because `scripts/adapter-smoke.sh` did not exist; then passed after adding the script and checklist, 1 test run, 0 failures.
- `bash scripts/adapter-smoke.sh`: passed and ran the fixture detection smoke, 1 test run, 0 failures.
- `bash scripts/adapter-smoke.sh --backend`: passed and ran the wider adapter and command-guard smoke, 39 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 380 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented supported adapters API and dashboard panel from `docs/plans/105-supported-adapters-dashboard.md`.

Changes:

- Added `GET /api/language-adapters` with a read-only catalog for Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, Python/pytest, Python/Poetry, and Python/uv.
- Returned each adapter's language, build system, fixed verification command, detection signals, demo fixture path, and `SUPPORTED` status.
- Added a dashboard `SupportedAdaptersPanel` backed by the new API.
- Kept adapter API failures local to the supported-adapters panel so task, queue, configuration, and health data can still load.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterCatalogServiceTests,LanguageAdapterControllerTests test`: first failed because the catalog service, controller, and VO did not exist; then passed after adding the backend API, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: first failed because the API helper and component did not exist; then failed because the panel error used the same status role as the global refresh indicator; then passed after adding the API helper, panel, App integration, and isolated panel error state, 57 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 382 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `npm run build`: passed after fixing the supported-adapter test fixture type for the `SUPPORTED` literal status.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Node/Bun adapter support from `docs/plans/106-node-bun-language-adapter.md`.

Changes:

- Added `NodeBunLanguageAdapter` for repositories with `package.json`, `bun.lockb` or `bun.lock`, and a non-empty `scripts.test`.
- Preferred Bun before the broad npm adapter when a Bun lockfile is present.
- Extended the command allowlist to permit only `bun test`, not Bun install or arbitrary package scripts.
- Installed Bun in the backend runtime Docker image.
- Added a `docs/demo-repositories/node-bun` fixture with a Bun-compatible test file.
- Added Bun to the supported-adapter API catalog and dashboard test data.
- Updated README, product specification, architecture, target state, roadmap, backend command standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=NodeBunLanguageAdapterTests,CommandExecutionGuardTests,MavenRuntimePackagingTests,PatchPilotApplicationTests,LanguageAdapterRegistryTests,LanguageAdapterCatalogServiceTests test`: first failed because `NodeBunLanguageAdapter` did not exist; then failed because the npm adapter constructor became ambiguous after multi-lockfile support; then failed because the Dockerfile packaging assertion was too order-specific; then passed after adding the adapter, multi-lockfile detection, command allowlist, runtime packaging, Spring registration, catalog entry, and fixture, 26 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: passed after updating dashboard supported-adapter fixtures for Bun, 44 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `LanguageAdapterControllerTests` still expected 8 supported adapters; then passed after updating the controller response assertions for Bun, 388 tests run, 0 failures.

Implemented Python advanced runner adapters from `docs/plans/107-python-advanced-runner-adapters.md`.

Changes:

- Added `PythonToxLanguageAdapter` for repositories with `tox.ini` or `[tool.tox]` in `pyproject.toml`.
- Added `PythonNoxLanguageAdapter` for repositories with `noxfile.py`.
- Added `PythonHatchLanguageAdapter` for repositories with a Hatch test script in `pyproject.toml`.
- Preferred tox, nox, and hatch before Poetry, uv, and plain pytest when explicit runner signals are present.
- Extended `CommandExecutionGuard` to allow only `tox`, `nox`, and `hatch test`, not arbitrary runner environments, sessions, or scripts.
- Added tox, nox, and hatch to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Added `docs/demo-repositories/python-tox`, `python-nox`, and `python-hatch` fixtures.
- Added tox, nox, and hatch to the supported-adapter API catalog and dashboard test data.
- Updated README, product specification, architecture, target state, roadmap, backend command standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonToxLanguageAdapterTests,PythonNoxLanguageAdapterTests,PythonHatchLanguageAdapterTests,CommandExecutionGuardTests,MavenRuntimePackagingTests,PatchPilotApplicationTests,LanguageAdapterRegistryTests,LanguageAdapterCatalogServiceTests,LanguageAdapterControllerTests test`: first failed because `PythonToxLanguageAdapter`, `PythonNoxLanguageAdapter`, and `PythonHatchLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, runtime packaging, Spring registration/order checks, catalog entries, and fixtures, 33 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: first failed because the test queried partial table text too broadly after adding tox and hatch rows; then passed after narrowing assertions to adapter rows and exact rendered cell text, 44 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 398 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter fixture verification dashboard from `docs/plans/108-adapter-fixture-verification-dashboard.md`.

Changes:

- Added `GET /api/language-adapters/fixtures` to verify each supported demo fixture with the real `LanguageAdapterRegistry`.
- Returned fixture name/path, expected and actual language/build system/command, detection reason, and `PASS` or `FAIL` status.
- Kept missing or drifting fixtures visible as failed rows instead of failing the whole endpoint.
- Copied `docs/demo-repositories` into the backend Docker runtime image so Docker Compose can serve the fixture verification API.
- Added a dashboard `AdapterFixtureVerificationPanel` backed by the new API, with fixture API failures isolated to that panel.
- Updated README, architecture notes, frontend design notes, adapter smoke checklist, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterFixtureVerificationServiceTests,LanguageAdapterControllerTests test`: first failed because the fixture verification service and VO did not exist; then passed after adding the backend API, 4 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/AdapterFixtureVerificationPanel.test.tsx src/App.test.tsx`: first failed because the API helper, type, and panel did not exist; then failed because the dashboard intentionally rendered the same fixture path in both adapter catalog and fixture verification panels; then passed after adding the API helper, panel, App integration, and row-scoped dashboard assertions, 58 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterFixtureVerificationServiceTests,LanguageAdapterControllerTests,MavenRuntimePackagingTests test`: passed after adding the Docker runtime fixture copy assertion and implementation, 12 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the fixture verification service had multiple constructors without an explicit Spring injection constructor; then passed after marking the production constructor for injection, 402 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 79 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented demo readiness gate from `docs/plans/109-demo-readiness-gate.md`.

Changes:

- Added `GET /api/demo/readiness` to aggregate demo readiness from backend reachability, required credential configuration, optional model cost configuration, adapter fixture verification, queue state, and recent completed Pull Request evidence.
- Added readiness domain records with `READY`, `NEEDS_ATTENTION`, and `BLOCKED` states plus concrete operator next actions.
- Extracted `ConfigurationSummaryService` so configuration readiness can be reused by both the configuration API and demo readiness service.
- Added a dashboard `DemoReadinessPanel` near the top of the operations page, backed by the new API.
- Kept readiness API failures local to the readiness panel so task, metric, queue, adapter, and configuration data can still load.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoReadinessControllerTests test`: first failed because the demo readiness service, controller, and domain records did not exist; then passed after adding the backend API, 5 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoReadinessPanel.test.tsx src/App.test.tsx`: first failed because duplicate readiness status labels appeared in both the header and check rows; then passed after keeping the textual status in the header and using row markers with accessible labels, 60 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 406 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 83 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter detection explainability from `docs/plans/110-adapter-detection-explainability.md`.

Changes:

- Added nullable `adapter_detection_reason` task persistence through a Flyway migration.
- Stored `LanguageDetectionResult.reason()` when task execution records selected adapter metadata.
- Returned `adapterDetectionReason` through task list/detail APIs and preserved it across status transitions.
- Included adapter language, build system, verification command, and detection reason in copied Markdown task reports.
- Added dashboard task detail evidence for the selected adapter detection reason.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskConvertTests,TaskControllerTests,WorkspaceFixTaskExecutorTests,FixTaskAdapterMetadataMigrationTests test`: first failed because adapter metadata methods and task records did not expose `adapterDetectionReason`; then passed after adding persistence, conversion, service, controller, and executor support, 102 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the task detail evidence strip did not render detection reason; then passed after adding the frontend field and display, 8 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id test`: first failed because copied task reports did not include adapter evidence; then passed after adding the report adapter section, 1 test run, 0 failures.

Implemented generated diff risk gate from `docs/plans/111-generated-diff-risk-gate.md`.

Changes:

- Added `GeneratedDiffRiskGate` to inspect generated workspace diffs after `DiffTool` and before adapter verification, test-run recording, commit, push, or Pull Request creation.
- Blocked sensitive file changes, secret-like added lines, binary patches, too many changed files, and too many changed lines with deterministic reasons.
- Recorded the risk gate as an audited `GeneratedDiffRiskGate` tool call so task detail APIs, copied reports, and dashboard records can explain why execution stopped.
- Added dashboard task detail evidence for generated-diff risk-gate blocks.
- Updated README, architecture notes, target state, backend code standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests test`: first failed because binary generated diffs were still accepted; then passed after adding binary diff detection, 5 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because task detail evidence did not surface risk-gate blocks; then passed after rendering the blocked marker, 9 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests,WorkspaceFixTaskExecutorTests test`: passed after executor integration, 15 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx src/api.test.ts`: passed after dashboard/API focused verification, 66 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 413 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 84 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review queue from `docs/plans/112-risk-review-queue.md`.

Changes:

- Added `PENDING_REVIEW` as an explicit active task status for generated-diff risk-gate rejections.
- Mapped `Generated diff rejected: ...` executor failures to `markPendingReview(...)`, a `PENDING_REVIEW` timeline event, and an edited GitHub status comment.
- Preserved risk rejection reasons in `failureReason` and kept `GeneratedDiffRiskGate` tool calls as detailed audit evidence.
- Added pending-review counts to task status-count and metrics APIs.
- Allowed cancelling pending-review tasks while blocking retry until a future human approval flow exists.
- Added dashboard `PENDING_REVIEW` status filtering, count badges, task pills, cancel affordance, and risk evidence.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests,DefaultFixTaskControlServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because `PENDING_REVIEW`, `markPendingReview(...)`, pending-review counts, timeline events, and status-comment updates did not exist.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests,DefaultFixTaskControlServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests,IssueCommentToolTests test`: passed after backend implementation, 81 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard still expected two visible tasks after adding a pending-review fixture; then passed after updating the fixture counts and assertions, 66 tests run, 0 failures.

Implemented risk review approval from `docs/plans/113-risk-review-approval.md`.

Changes:

- Added `riskReviewApprovedAt` task persistence through a Flyway migration.
- Added `POST /api/tasks/{id}/approve-review` and `FixTaskControlService.approveReviewTask(...)`.
- Restricted approval to `PENDING_REVIEW` tasks, then changed approved tasks back to `PENDING`, cleared the risk failure reason, enqueued the task, and recorded a `REVIEW_APPROVED` timeline event.
- Added workspace resume support so an approved task continues from the existing task workspace instead of re-running model patch generation, diff generation, or the generated-diff risk gate.
- Kept adapter detection, verification, commit, push, Pull Request creation, queue records, and GitHub human review in the resumed path.
- Added dashboard `Approve review` action for pending-review tasks and kept retry hidden for pending-review states.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because the approval API, review approval field, timeline event, service transition, and workspace resume method did not exist; then passed after backend implementation, 113 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: first failed because the approve-review API and detail action did not exist; then passed after frontend implementation, 69 tests run, 0 failures.

Implemented risk review diff inspection from `docs/plans/114-risk-review-diff-inspection.md`.

Changes:

- Added `FixTaskGeneratedDiffVo` and populated it from the latest successful `DiffTool` tool-call output in `GET /api/tasks/{taskId}/detail`.
- Added a generated-diff section to copied Markdown task reports.
- Added a dashboard generated-diff preview in selected task detail so `PENDING_REVIEW` approvals can inspect the exact patch before resuming.
- Updated README, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_latest_generated_diff_in_task_detail test`: first failed because `/detail` did not expose `generatedDiff`; then passed after backend projection, 1 test run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx`: first failed because the generated-diff preview did not exist; then passed after adding the panel, 11 tests run, 0 failures.
- `npm test -- api.test.ts`: passed after API fixture coverage, 16 tests run, 0 failures.
- `npm test -- App.test.tsx`: passed after dashboard integration coverage, 43 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id test`: passed after report coverage, 1 test run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because existing controller tests compared JSONPath decimal values as `Double` against `BigDecimal`; then passed after using a numeric test matcher for decimal JSON values, 427 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 88 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review approval audit from `docs/plans/115-risk-review-approval-audit.md`.

Changes:

- Added `ApproveReviewDto` and `ApproveReviewCommand` so `POST /api/tasks/{taskId}/approve-review` requires an approver and approval reason.
- Added `riskReviewApprovedBy` and `riskReviewApprovalReason` to task VO/entity conversion, in-memory persistence, MyBatis persistence, search fields, and MySQL schema migration.
- Preserved existing `riskReviewApprovedAt` behavior while clearing all approval metadata on fresh retries and new pending-review states.
- Recorded approval metadata in the review-approved timeline event, copied task reports, executor resume audit summary, task APIs, and dashboard detail.
- Replaced one-click dashboard approval with a compact approval form that disables submission until both fields are filled.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests,DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because `ApproveReviewCommand` did not exist.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because approve-review still sent no request body and the detail panel had no approval form or metadata display.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests,DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskConvertTests,FixTaskMigrationTests test`: passed after backend implementation, 112 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: passed after frontend implementation, 71 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `WorkspaceFixTaskExecutorTests` still expected the old approval tool-call input summary; then passed after updating the resume fixture to include approver metadata, 429 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 89 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review approval authorization from `docs/plans/116-risk-review-approval-authorization.md`.

Changes:

- Added `patchpilot.review-approval.allowed-operators` / `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS` for explicit risk-review approver authorization.
- Exposed normalized review approvers in `GET /api/configuration/summary`.
- Rejected unauthorized `POST /api/tasks/{taskId}/approve-review` calls with `403` before task mutation, queue enqueue, or timeline recording.
- Updated the dashboard configuration panel to show review approvers and warn when none are configured.
- Replaced free-text approval operator entry with a configured approver selector, and disabled approval when the allowlist is empty.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,ConfigurationSummaryServiceTests test`: first failed because `ReviewApprovalProperties` did not exist; then passed after backend implementation, 69 tests run, 0 failures.
- `npm test -- src/dashboard/components/ConfigurationPanel.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because review approvers were not rendered and the approval form still used free text; then passed after frontend implementation, 16 tests run, 0 failures.

Implemented safety policy readiness summary from `docs/plans/117-safety-policy-readiness-summary.md`.

Changes:

- Extended `GET /api/configuration/summary` with non-sensitive safety policy fields for trigger-user allowlists, repository allowlists, and review-approval approvers.
- Added a `Safety policy` check to `GET /api/demo/readiness`, marking open trigger/repository allowlists and missing review approvers as operator attention items.
- Rendered trigger-user and repository allowlist state in the dashboard configuration panel.
- Updated dashboard readiness fixtures so the existing demo readiness panel shows safety policy checks alongside credentials, queue, adapters, and recent PR evidence.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests test`: first failed because `ConfigurationSummaryVo` did not expose safety policy fields; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- src/dashboard/components/ConfigurationPanel.test.tsx src/dashboard/components/DemoReadinessPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render trigger-user and repository allowlist state; then passed after frontend implementation, 65 tests run, 0 failures.

Implemented admin API token guard from `docs/plans/118-admin-api-token-guard.md`.

Changes:

- Added optional `patchpilot.security.admin-token` / `PATCHPILOT_ADMIN_TOKEN` for protecting operator APIs when PatchPilot is reachable through a public temporary tunnel.
- Added `AdminApiSecurityFilter` so configured deployments require `X-PatchPilot-Admin-Token` or `Authorization: Bearer <token>` for `/api/**` operator calls while keeping `/health`, actuator health, and `/api/github/webhook` public for health checks and GitHub deliveries.
- Exposed non-sensitive `adminTokenConfigured` state in `GET /api/configuration/summary`.
- Added admin-token readiness guidance to the demo `Safety policy` check.
- Updated the dashboard configuration panel and frontend API helper so a locally stored browser token is sent as `X-PatchPilot-Admin-Token` without changing request shapes when no token is stored.
- Updated `.env.example`, Docker Compose, README, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests,AdminApiSecurityFilterTests test`: first failed because the admin token field and filter did not exist.
- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because frontend API calls did not send the admin header and the configuration panel did not render the admin token state.
- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests,ConfigurationSummaryServiceTests,DemoReadinessServiceTests,AdminApiSecurityFilterTests test`: passed after backend implementation, 11 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: passed after frontend implementation, 20 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 439 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 91 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard admin token prompt from `docs/plans/119-dashboard-admin-token-prompt.md`.

Changes:

- Added an inline `Admin API token` password prompt when the dashboard receives the backend `Admin token is required` response.
- Saved the submitted token to browser `localStorage` under `patchpilot.adminToken` and reused the existing API helper so later requests include `X-PatchPilot-Admin-Token`.
- Retried dashboard loading immediately after saving the token so operators can recover from a protected temporary URL without opening browser DevTools.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "prompts for admin token"`: first failed because the dashboard rendered only the backend/proxy alert and had no admin-token prompt; then passed after frontend implementation, 1 test run, 0 failures.
- `npm test`: passed after full frontend verification, 92 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard admin token management from `docs/plans/120-dashboard-admin-token-management.md`.

Changes:

- Added a dashboard header token manager that reports whether the current browser has a saved admin token.
- Added `Dashboard admin token` input support for saving or replacing the local `patchpilot.adminToken` value.
- Added `Clear admin token` so operators can remove the local credential during rotation or unauthenticated testing.
- Refreshed dashboard data after saving or clearing the token so the current credential state is used immediately.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "manages stored admin token"`: first failed because the dashboard header did not show saved-token state or management actions; then passed after frontend implementation, 1 test run, 0 failures.
- `npm test`: passed after full frontend verification, 93 tests run, 0 failures.
- `npm run build`: passed after production frontend build.

Implemented dashboard API connectivity check from `docs/plans/121-dashboard-api-connectivity-check.md`.

Changes:

- Added a top-of-page `ConnectivityPanel` that separates backend `/health`, browser admin-token state, and protected API reachability.
- Loaded `/health` before the protected dashboard API batch so operators can still see that the backend is up when protected APIs reject missing or wrong admin tokens.
- Added corrective next-action text for backend/proxy failures and admin-token failures.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "connectivity"`: first failed because the dashboard had no connectivity panel and could not distinguish backend-up/admin-token-missing failures; then passed after frontend implementation, 2 tests run, 0 failures.
- `npm test`: first failed because the existing configuration assertions matched the new connectivity `Backend UP` text as well; then passed after scoping the assertion to the Configuration panel, 95 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard operator setup checklist from `docs/plans/122-dashboard-operator-setup-checklist.md`.

Changes:

- Added `OperatorSetupChecklistPanel` near the top of the dashboard.
- Derived read-only setup checks from already loaded dashboard data instead of adding a new backend API.
- Covered backend connectivity, required credentials, safety policy, adapter fixtures, queue health, and recent Pull Request evidence.
- Added next setup actions for failed queue health and missing recent PR evidence.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "operator setup"`: first failed because the dashboard had no operator setup checklist; then failed again because recent PR evidence incorrectly preferred task-list fallback over demo readiness; then passed after checklist implementation and readiness precedence, 1 test run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "operator setup|every operator"`: passed after adding the all-ready scenario, 2 tests run, 0 failures.
- `npm test`: first failed because an existing demo-readiness assertion matched the same next-action text rendered by the new setup checklist; then passed after scoping the assertion to the Demo readiness panel, 97 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented webhook delivery diagnostics from `docs/plans/123-webhook-delivery-diagnostics.md`.

Changes:

- Added a `webhook_delivery_diagnostic` read model with in-memory and MyBatis services plus a Flyway migration.
- Recorded delivery outcomes for invalid signatures, malformed requests, unsupported events, ignored non-commands, safety/rate/model rejections, duplicate deliveries, active-task collisions, and created tasks.
- Exposed `GET /api/github/webhook-deliveries?limit=...` for curl and dashboard inspection without storing raw payloads or signatures.
- Added a dashboard `WebhookDeliveryPanel` backed by the new API so operators can diagnose temporary URL, signature, ignored-event, rejection, duplicate, and task-created outcomes without relying only on GitHub's delivery page.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,GitHubWebhookControllerTests,MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticMigrationTests test`: first failed because the webhook service did not accept a diagnostic recorder and no migration existed; then passed after backend implementation, 26 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the dashboard had no named webhook-deliveries region; then passed after adding an accessible panel label, 67 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 447 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 98 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `mvn -pl PatchPilot -Dtest=MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticControllerTests test`: passed after the final diagnostic list-ordering adjustment, 4 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented webhook redelivery guidance from `docs/plans/124-webhook-redelivery-guidance.md`.

Changes:

- Added derived `redeliveryRecommended` and `operatorAction` fields to webhook delivery diagnostics without changing the persisted diagnostic table.
- Classified invalid signatures, malformed requests, and backend processing failures as fix-then-redeliver cases.
- Classified ignored, rejected, duplicate, active-task, and task-created outcomes as non-redelivery cases with safer next actions.
- Rendered redelivery guidance in the dashboard webhook delivery panel so operators know when to use GitHub's `Redeliver` button.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=WebhookDeliveryDiagnosticControllerTests,InMemoryWebhookDeliveryDiagnosticServiceTests test`: first failed because `WebhookDeliveryDiagnosticVo` did not expose redelivery guidance fields; then passed after backend implementation, 5 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the dashboard did not render `Redeliver after fix`; then passed after frontend implementation, 67 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=WebhookDeliveryDiagnosticControllerTests,InMemoryWebhookDeliveryDiagnosticServiceTests,MyBatisWebhookDeliveryDiagnosticServiceTests test`: passed after MyBatis guidance coverage, 8 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 449 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 98 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace verification.

Implemented live demo smoke checklist from `docs/plans/125-live-demo-smoke-checklist.md`.

Changes:

- Added `GET /api/demo/smoke-checklist` as a read-only final pre-demo checklist.
- Derived ordered readiness, webhook delivery, task execution, and Pull Request evidence from existing readiness, delivery diagnostics, and task history data.
- Kept duplicate, ignored, rejected, and active-task webhook deliveries as attention states instead of treating any task id as successful smoke evidence.
- Added a dashboard `DemoSmokeChecklistPanel` near the existing setup and readiness panels.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoSmokeChecklistServiceTests,DemoReadinessControllerTests test`: first failed because the smoke checklist API/service/domain records did not exist; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the smoke checklist API helper and panel did not exist; then passed after frontend implementation and isolated request handling, 68 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 454 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 99 tests run, 0 failures.
- `npm run build`: passed after production frontend build.

Implemented rejected trigger dashboard visibility from `docs/plans/126-rejected-trigger-dashboard.md`.

Changes:

- Added a typed frontend API helper for `GET /api/rejected-triggers?limit=20`.
- Added a dashboard `RejectedTriggerPanel` that shows recent refused `/agent fix` attempts with source, repository, issue, trigger user, delivery id, command text, timestamp, and rejection reason.
- Kept rejected-trigger API failures local to the panel so the rest of the dashboard can still load.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- api.test.ts App.test.tsx`: first failed because the API helper and dashboard region did not exist; then passed after frontend implementation, 69 tests run, 0 failures.
- `npm test -- api.test.ts App.test.tsx RejectedTriggerPanel.test.tsx`: passed after component empty/error coverage, 71 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 102 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `mvn -pl PatchPilot test`: passed after full backend verification, 454 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.
- Conflict-marker scan over README, docs, and frontend sources: no conflict markers found.

Implemented Go language adapter support from `docs/plans/127-go-language-adapter.md`.

Changes:

- Added `GoLanguageAdapter` for `go.mod` repositories with fixed verification command `go test ./...`.
- Added `go test ./...` to the command allowlist while rejecting arbitrary Go commands.
- Added a minimal `docs/demo-repositories/go-module` fixture and included it in registry, catalog, fixture verification, Spring context, and dashboard supported-adapter coverage.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GoLanguageAdapterTests,LanguageAdapterRegistryTests,CommandExecutionGuardTests test`: first failed because `GoLanguageAdapter` did not exist; then passed after adapter and allowlist implementation, 12 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,LanguageAdapterCatalogServiceTests,LanguageAdapterFixtureVerificationServiceTests test`: passed after catalog, fixture, and Spring context coverage, 10 tests run, 0 failures.
- `npm test -- App.test.tsx SupportedAdaptersPanel.test.tsx`: first failed because dashboard fixture-count assertions still expected 12 adapters; then passed after updating Go visibility, 51 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `LanguageAdapterControllerTests` still expected 12 catalog and fixture rows; then passed after controller API assertions were updated for Go, 457 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 102 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace verification.
- Conflict-marker scan over README, product docs, plan, progress log, demo fixtures, backend sources, backend tests, and frontend sources: no conflict markers found.

Implemented unsupported repository guidance from `docs/plans/128-unsupported-repository-guidance.md`.

Changes:

- Added structured `repositorySupportGuidance` to task detail responses when a task fails with an unsupported repository reason.
- Reused the existing supported adapter catalog so guidance automatically lists current languages, build systems, verification commands, and detection signals.
- Added a `Repository Support Guidance` section to copied task reports for unsupported repository failures.
- Added a dashboard task detail guidance panel that explains why PatchPilot refused to execute and which supported project markers/tests to add before retrying.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because task detail and report did not expose repository support guidance; then passed after backend implementation, 59 tests run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx`: first failed because no accessible repository support guidance panel existed; then passed after frontend implementation, 14 tests run, 0 failures.

Implemented safe refusal issue comments from `docs/plans/129-safe-refusal-issue-comments.md`.

Changes:

- Added optional refusal comment metadata to rejected trigger audit commands, VOs, in-memory storage, and MyBatis entities.
- Added a Flyway migration for rejected trigger `comment_id` and `comment_url`.
- Added `IssueCommentTool.commentRejected` with a safe body that explains refusal without echoing the raw rejected command.
- Updated webhook rejection paths to attempt a refusal comment before recording rejected trigger audits, while keeping rejection successful if comment creation fails.
- Rendered refusal comment links in the dashboard rejected triggers panel when GitHub comment URLs are available.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditMigrationTests test`: first failed because refusal comment fields and methods did not exist.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,IssueCommentToolTests,RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditMigrationTests test`: passed after backend implementation, 32 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the rejected triggers panel did not expose a `Refusal comment` link; then passed after frontend implementation, 71 tests run, 0 failures.

Implemented operator retry for rejected triggers from `docs/plans/130-operator-retry-rejected-trigger.md`.

Changes:

- Added rejected-trigger lookup by audit id to in-memory and MyBatis-backed audit services.
- Added `RejectedTriggerRetryService` and `POST /api/rejected-triggers/{id}/retry`.
- Reused the existing manual task creation flow for retries so safety gates, active-task checks, rate limits, and model trigger classification still apply.
- Added a task timeline `REQUEUED` event that links the new task back to the rejected trigger audit id and prior rejection reason.
- Added a dashboard retry button for rejected trigger rows with per-row loading state and refresh after success.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests test`: first failed because `RejectedTriggerRetryService` did not exist.
- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests test`: passed after backend implementation, 7 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,GitHubWebhookServiceTests test`: passed after audit lookup coverage and legacy fake updates, 28 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the retry API helper and rejected-trigger retry buttons did not exist.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: passed after frontend implementation, 74 tests run, 0 failures.

Implemented retry audit links for rejected triggers from `docs/plans/131-retry-rejected-trigger-audit-link.md`.

Changes:

- Added `retriedTaskId` and `retriedAt` metadata to rejected trigger audit records.
- Added a Flyway migration for MySQL-backed rejected trigger retry metadata.
- Marked a rejected trigger audit as retried after the retry flow creates a new task and records the retry timeline event.
- Returned retry metadata from rejected-trigger API responses.
- Added a dashboard `Retried task` link that opens the generated task through the existing `/tasks/{id}` detail route.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests test`: first failed because retry metadata fields, service methods, and the migration did not exist.
- `mvn -pl PatchPilot -Dtest=DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests test`: passed after backend implementation, 33 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the dashboard did not render a `Retried task` link.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: passed after frontend implementation, 75 tests run, 0 failures.
- `npm run build`: first failed because the retried task click handler did not preserve TypeScript's non-null narrowing; then passed after extracting a render helper.
- `mvn -pl PatchPilot test`: passed after full backend verification, 472 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 107 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented issue context ingestion from `docs/plans/132-issue-context-ingestion.md`.

Changes:

- Added a GitHub issue context client/service that reads issue title, body, URL, and recent comments through the configured GitHub token.
- Loaded issue context during task execution before patch planning and recorded that read as an audited tool call.
- Passed issue context into the fix-plan prompt so model planning can use the issue body and discussion, not only the `/agent fix` trigger comment.
- Added issue context to task detail and markdown report responses, with safe fallback when GitHub context cannot be loaded for dashboard inspection.
- Rendered issue title, source link, body summary, and recent comments in the dashboard task detail panel.
- Explicitly enabled Maven compiler annotation processing so Lombok-generated methods work under newer JDKs.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueContextClientTests,IssueContextServiceTests,FixPlanGeneratorTests,PlanDrivenPatchWorkflowTests,WorkspaceFixTaskExecutorTests test`: passed after backend workflow implementation, 22 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: passed after task detail/report issue context API implementation, 59 tests run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx App.test.tsx api.test.ts`: passed after dashboard issue context rendering, 87 tests run, 0 failures.
- `npm run build`: passed after frontend type and fixture updates.
- `mvn -pl PatchPilot test`: passed after webhook test fake issue context service was added, 478 tests run, 0 failures.

Implemented patch review evidence visibility from `docs/plans/135-patch-review-evidence-visibility.md`.

Changes:

- Added task-level patch review records with decision, reason, confidence, required follow-up, edited files, and created timestamp.
- Added in-memory and MyBatis-backed patch review services plus a Flyway migration for MySQL persistence.
- Recorded post-edit review evidence before writing model-generated files, including rejected reviews that stop execution.
- Added latest patch review evidence to task detail API responses and markdown task reports.
- Rendered patch review evidence in the dashboard task detail panel with approved and blocked review gate states.

Validation:

- `mvn -pl PatchPilot -Dtest=PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,TaskControllerTests,FixTaskPatchReviewConvertTests,InMemoryFixTaskPatchReviewServiceTests,MyBatisFixTaskPatchReviewServiceTests,FixTaskPatchReviewMigrationTests test`: first failed because the new patch review service/entity/mapper did not exist; then passed after backend implementation, 75 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard did not render a patch review section; then passed after frontend implementation, 38 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 493 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 110 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo session report export from `docs/plans/154-demo-session-report-export.md`.

Changes:

- Added `GET /api/demo/session-report` as a read-only Markdown export over the current demo session snapshot.
- Added `DemoSessionReportService` to format session id, status, summary, generated time, share summary, recent Pull Request, recent task, operator checklist, script steps, health contract, next actions, and embedded runbook.
- Added frontend `getDemoSessionReport` and a `Copy session report` action to `DemoSessionSnapshotPanel`.
- Wired `App.tsx` to fetch the session report only when the operator clicks the copy action.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because `DemoSessionReportService` did not exist; then passed after backend implementation, 9 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: first failed because `getDemoSessionReport` and the copy button did not exist; then passed after frontend implementation, 96 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 582 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 148 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo session snapshot from `docs/plans/153-demo-session-snapshot.md`.

Changes:

- Added `GET /api/demo/session-snapshot` as a read-only aggregate over one current demo evidence bundle, derived script, derived runbook, operator checklist, health contract, share summary, and next actions.
- Added `DemoSessionSnapshotService` and `DemoSessionSnapshotVo`, with deterministic session ids based on generated time and a health contract stating the endpoint does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Added frontend `getDemoSessionSnapshot`, typed `DemoSessionSnapshot` models, and `DemoSessionSnapshotPanel`.
- Wired the dashboard to load and render the snapshot near the existing demo evidence and script panels without blocking the rest of the dashboard when the snapshot endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionSnapshotServiceTests,DemoReadinessControllerTests test`: first failed because `DemoSessionSnapshotService`, `DemoSessionSnapshotVo`, and the endpoint did not exist; then passed after backend implementation, 8 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: first failed because `getDemoSessionSnapshot`, `DemoSessionSnapshotPanel`, and App-level loading did not exist; then passed after frontend implementation, 93 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 579 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 145 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented review rejection recovery from `docs/plans/136-review-rejection-recovery.md`.

Changes:

- Added a shared patch review rejection classifier so comments, reports, and metrics use one durable definition for model patch review blocks.
- Updated failed task status comments to call out `PATCH_REVIEW_REJECTED` and explain that retry asks the model for a fresh patch.
- Classified model patch review blocks as `PATCH_REVIEW_REJECTION` in failure-cause metrics instead of generic auth or model failures.
- Added review gate and recovery guidance to markdown task reports.
- Added dashboard recovery guidance for rejected patch reviews while preserving the existing retry action for failed tasks.

Validation:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_update_failed_status_comment_with_patch_review_recovery_guidance test`: first failed because failed comments used generic failure copy.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_failure_cause test`: first failed because patch review rejection was counted under GitHub auth due the word authentication.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx -t "marks rejected patch reviews as review gate blocks"`: first failed because the dashboard lacked retry regeneration guidance.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: passed after backend implementation, 75 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: passed after dashboard implementation, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 494 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 110 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented review retry lineage from `docs/plans/137-review-retry-lineage.md`.

Changes:

- Added task retry lineage fields for source task id, source status, source failure reason, and retry timestamp.
- Persisted retry lineage in both in-memory and MySQL-backed task services.
- Added a Flyway migration for durable retry lineage columns.
- Included retry lineage in task API responses and markdown task reports.
- Rendered retry lineage in the dashboard task detail panel so operators can inspect recovery context for retried review rejections and other terminal failures.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,FixTaskMigrationTests test`: first failed because retry lineage fields and persistence did not exist; then passed after backend implementation, 102 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts`: first failed because the dashboard did not render retry lineage; then passed after frontend implementation, 39 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 498 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 111 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented sensitive diff policy visibility from `docs/plans/138-sensitive-diff-policy.md`.

Changes:

- Added a shared `GeneratedDiffSafetyPolicy` for generated-diff risk thresholds, sensitive path matching, binary diff detection, and secret-like added lines.
- Wired `GeneratedDiffRiskGate` and `PlannedPatchWorkflow` to the shared policy so planning-time target validation and post-generation diff review use the same protected path rules.
- Extended protected path coverage to Git metadata and package-manager credential files such as `.npmrc`, `.pypirc`, `.netrc`, and Maven `settings.xml`.
- Extended `GET /api/configuration/summary` with non-sensitive generated-diff policy state.
- Rendered generated-diff risk-gate state and protected path pattern count in the dashboard configuration panel with advisories for disabled or empty policy state.

Validation:

- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests test`: first failed because the configuration summary and planned workflow tests did not yet share the new policy fields; then passed after backend implementation, 27 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render generated-diff policy state and later because the advisory count changed; then passed after frontend implementation, 75 tests run, 0 failures.

Implemented rejected trigger categories from `docs/plans/139-rejected-trigger-categories.md`.

Changes:

- Added stable rejected-trigger categories for empty or unsupported commands, non-actionable requests, dangerous instructions, user/repository allowlist failures, rate limits, and model-classifier refusals.
- Extended safety gate, rate-limit, and model trigger decisions to carry a category next to the existing operator-facing reason.
- Persisted rejected-trigger categories in in-memory and MySQL-backed audit records.
- Added a Flyway migration for `rejected_trigger_audit.category` plus a category/created index.
- Included `category` in rejected-trigger API responses.
- Rendered rejected-trigger category badges in the dashboard so operators can diagnose vague, malicious, unauthorized, blocked, rate-limited, or model-rejected attempts without parsing reason text.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because rejected-trigger category fields and constructors did not exist.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts`: first failed because the API response lacked `category` and the dashboard did not render category badges.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests test`: passed after backend implementation, 46 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts`: passed after frontend implementation, 24 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full Java 17 backend verification, 505 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 111 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger category filtering from `docs/plans/140-rejected-trigger-category-filtering.md`.

Changes:

- Added optional `category` filtering to `GET /api/rejected-triggers`.
- Supported category filtering in in-memory and MyBatis-backed rejected-trigger audit services while preserving the existing limit-only API path.
- Extended the dashboard API helper to request rejected triggers by category.
- Added a rejected-trigger category select to the dashboard and preserved its state in the URL as `rejectedCategory`.
- Updated product docs to describe category-filtered rejected-trigger diagnosis.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because rejected-trigger audit services only supported limit-only listing; then passed after backend implementation, 15 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the API helper treated options as a raw limit and the dashboard had no category select; then passed after frontend implementation, 78 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full Java 17 backend verification, 508 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 114 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger abuse summary from `docs/plans/141-rejected-trigger-abuse-summary.md`.

Changes:

- Added `GET /api/rejected-triggers/summary` for read-only recent rejection counts.
- Summarized rejected-trigger audits by category, source, top trigger users, and top repositories using the existing rejected-trigger audit store.
- Added typed frontend support for the summary endpoint.
- Rendered a compact rejected-trigger summary above the dashboard audit rows.
- Made category summary rows clickable so operators can apply the existing URL-backed rejected-category filter from the summary.
- Updated product docs and README to document the summary endpoint and dashboard behavior.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because summary VO records and service/controller support did not exist; then passed after backend implementation, 18 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the API helper and dashboard had no rejected-trigger summary; then passed after frontend implementation, 80 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 511 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 116 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger auto quarantine from `docs/plans/142-rejected-trigger-auto-quarantine.md`.

Changes:

- Added a configurable rejected-trigger quarantine policy based on recent rejected-trigger audit records.
- Applied quarantine to GitHub webhook triggers and manual dashboard-created tasks before rate-limit checks, model classification, task creation, queueing, workspace cloning, or execution.
- Added the `ABUSE_QUARANTINED` rejected-trigger category for trigger-user and repository cooldowns.
- Exposed quarantine policy state in `GET /api/configuration/summary`, demo readiness, and the dashboard configuration panel.
- Updated the rejected-trigger dashboard summary/filter path to include abuse-quarantined records.
- Documented the self-hosted/private-demo quarantine behavior in product docs and README.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests test`: first failed because quarantine decision/request/service types did not exist.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because the dashboard did not expose quarantine policy or the `ABUSE_QUARANTINED` category.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests,RejectedTriggerQuarantineServiceTests test`: passed after backend implementation, 30 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: passed after frontend implementation, 6 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests,RejectedTriggerQuarantineServiceTests,DemoReadinessServiceTests test`: passed after adding demo-readiness and invalid-threshold coverage, 36 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: passed after adding API and app fixtures, 83 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 519 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 116 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented durable trigger quarantine from `docs/plans/143-durable-trigger-quarantine.md`.

Changes:

- Added durable trigger quarantine domain records for trigger-user and repository scopes, including reason, category, evidence count, window, start, expiry, and timestamps.
- Added in-memory and MyBatis-backed `TriggerQuarantineRecordService` implementations plus the `trigger_quarantine` Flyway migration.
- Updated rejected-trigger quarantine checks to consult active durable records before recomputing thresholds from rejected-trigger audit history.
- Created or extended quarantine records when recent rejected-trigger evidence crosses the configured threshold.
- Exposed `GET /api/trigger-quarantines` for active or historical quarantine inspection.
- Added frontend API typing and dashboard loading for active trigger quarantines.
- Rendered active trigger-user and repository quarantines in the rejected-trigger panel above individual audit rows.
- Updated README and product docs to describe durable quarantine state and the new operator endpoint.

Validation:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=InMemoryTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,RejectedTriggerQuarantineServiceTests test`: first failed because quarantine domain records, persistence service, and controller did not exist.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: first failed because the frontend API helper and active quarantine panel did not exist.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=MyBatisTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,RejectedTriggerQuarantineServiceTests,TriggerQuarantineMigrationTests test`: passed after backend implementation, including SQL ordering before quarantine list limits, 18 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: passed after frontend implementation, 81 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 531 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 117 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented manual trigger quarantine controls from `docs/plans/144-manual-trigger-quarantine-controls.md`.

Changes:

- Added manual trigger quarantine creation and release APIs under `/api/trigger-quarantines`.
- Added quarantine operator and release metadata to domain records, MyBatis entities, list responses, and Flyway migrations.
- Updated active quarantine lookup to ignore released records while preserving historical records.
- Prevented released threshold quarantines from being immediately recreated from the same pre-release rejection evidence.
- Added dashboard controls to create trigger-user or repository quarantines and release active quarantines from the rejected-trigger panel.
- Updated README and product docs to document the operator API and dashboard behavior.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=InMemoryTriggerQuarantineServiceTests,MyBatisTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,TriggerQuarantineMigrationTests test`: first failed because service APIs, release metadata, and V23 migration did not exist; then passed after backend implementation, 18 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RejectedTriggerQuarantineServiceTests test`: first failed because a manually released quarantine could be recreated from the same rejected-trigger evidence; then passed after release suppression logic.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RejectedTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests,MyBatisTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,TriggerQuarantineMigrationTests test`: passed after backend release suppression fix, 26 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: first failed because frontend API helpers and manual quarantine controls did not exist; then passed after frontend implementation, 30 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: passed after App-level dashboard wiring, 85 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 539 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 121 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented operator safety audit log from `docs/plans/145-operator-safety-audit-log.md`.

Changes:

- Added durable `operator_safety_audit` records for manual safety mutations.
- Added in-memory and MyBatis-backed `OperatorSafetyAuditService` implementations plus the V24 Flyway migration.
- Exposed `GET /api/operator-safety-audits` for recent operator safety audit rows.
- Recorded audit rows when operators create or release trigger quarantines through `/api/trigger-quarantines`.
- Added frontend API typing and dashboard loading for recent operator safety audits.
- Rendered operator safety audit rows in the rejected-trigger panel so quarantine create/release actions show operator, reason, target, and time.
- Updated README and product docs to describe traceable manual safety controls.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=OperatorSafetyAuditControllerTests,InMemoryOperatorSafetyAuditServiceTests,MyBatisOperatorSafetyAuditServiceTests,OperatorSafetyAuditMigrationTests,TriggerQuarantineControllerTests test`: first failed because the operator safety audit model, service, controller, mapper, and migration did not exist; then passed after implementation, 10 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: first failed because `listOperatorSafetyAudits` and the operator safety audit panel did not exist; then passed after frontend implementation, 86 tests run, 0 failures.

Implemented quarantine evidence drilldown from `docs/plans/146-quarantine-evidence-drilldown.md`.

Changes:

- Added `TriggerQuarantineEvidenceVo` and `TriggerQuarantineEvidenceService` as a read model for one quarantine, its matching rejected-trigger evidence, and its operator safety audit actions.
- Exposed `GET /api/trigger-quarantines/{id}/evidence` with not-found and limit validation behavior.
- Added in-memory and MyBatis-backed query methods for quarantine lookup by id, rejected-trigger evidence by quarantine target, and operator audit evidence by resource id.
- Added frontend API typing and `getTriggerQuarantineEvidence`.
- Added an `Inspect evidence` action to active quarantine rows in the rejected-trigger panel.
- Rendered a compact evidence drilldown with matching rejected `/agent fix` attempts and manual safety actions.
- Updated README and product docs to document the evidence endpoint and dashboard behavior.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=TriggerQuarantineControllerTests,DefaultTriggerQuarantineEvidenceServiceTests,InMemoryRejectedTriggerAuditServiceTests,InMemoryOperatorSafetyAuditServiceTests,InMemoryTriggerQuarantineServiceTests test`: first failed because `TriggerQuarantineEvidenceVo`, `TriggerQuarantineEvidenceService`, and `DefaultTriggerQuarantineEvidenceService` did not exist; then passed after backend implementation, 24 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: first failed because the new frontend API/helper props and evidence UI were not implemented; then passed after frontend implementation, 33 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: passed after App-level evidence loading, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest='*TriggerQuarantine*Tests,*RejectedTriggerAuditServiceTests,*OperatorSafetyAuditServiceTests,DefaultTriggerQuarantineEvidenceServiceTests' test`: first failed because MyBatis rejected-trigger query tests needed entity metadata initialization and the newest-first mock no longer matched SQL ordering; then passed after fixing the tests, 53 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 557 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 125 tests run, 0 failures.
- `npm run build`: first failed because one component test fixture omitted the new evidence props caught by TypeScript; then passed after fixing the fixture.
- `git diff --check`: passed after whitespace verification.

Implemented repository preflight diagnostics from `docs/plans/147-repository-preflight-diagnostics.md`.

Changes:

- Added `POST /api/repository-preflight` as a local adapter-detection diagnostic that does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Returned supported status, language, build system, verification command, detection reason, operator action, and adapter guidance for unsupported repository paths.
- Added frontend API typing and `preflightRepository`.
- Added a dashboard `RepositoryPreflightPanel` so operators can run the real language adapter registry against a local path before posting `/agent fix`.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RepositoryPreflightServiceTests,RepositoryPreflightControllerTests test`: first failed because the repository preflight request, response, service, and controller did not exist; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RepositoryPreflightPanel.test.tsx src/api.test.ts`: first failed because the dashboard preflight panel did not exist; then passed after frontend implementation.
- `npm test -- --run src/dashboard/components/RepositoryPreflightPanel.test.tsx src/api.test.ts src/App.test.tsx`: passed after App-level dashboard wiring, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 563 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 130 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented repository preflight scope policy from `docs/plans/148-repository-preflight-scope-policy.md`.

Changes:

- Added `patchpilot.repository-preflight.allowed-root-dirs` and `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS`.
- Limited `POST /api/repository-preflight` to resolved paths under configured allowed roots before adapter detection.
- Kept relative root handling compatible with both repository-root and `PatchPilot/` Maven module working directories.
- Exposed normalized repository-preflight allowed roots through `GET /api/configuration/summary`.
- Added dashboard configuration visibility for repository-preflight allowed roots and a health advisory when the list is empty.
- Updated README, product spec, architecture notes, frontend design notes, decisions, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RepositoryPreflightServiceTests,RepositoryPreflightControllerTests,ConfigurationSummaryServiceTests,DemoReadinessServiceTests test`: first failed because the configuration-summary test expected the non-module relative path; then passed after updating the expected module-friendly fallback path, 14 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render repository-preflight roots or the empty-root advisory; then passed after frontend implementation, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 565 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 130 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.

Implemented repository preflight scope readiness from `docs/plans/149-repository-preflight-scope-readiness.md`.

Changes:

- Added a `Repository preflight scope` check to demo readiness.
- Flagged readiness attention when `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS` does not cover checked-in demo fixtures.
- Added repository-preflight scope to the operator setup checklist.
- Displayed configured repository-preflight allowed roots directly in the dashboard preflight panel.
- Updated README, product spec, frontend design notes, and this execution log.

Validation:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`: first failed because demo readiness did not include the repository-preflight scope check and a sibling root prefix was incorrectly treated as allowed; then passed after backend implementation, 7 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/dashboard/components/RepositoryPreflightPanel.test.tsx`: first failed because the checklist and preflight panel did not show scope readiness or allowed roots; then passed after frontend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because the checklist ignored the backend readiness result for repository-preflight scope; then passed after using the backend readiness check when available, 3 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=MyBatisTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests test`: passed after replacing stale fixed-date quarantine expirations with future instants, 14 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 567 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 133 tests run, 0 failures.
- `npm run build`: first failed because the new checklist fixture used `FixTaskStatusCounts` fields in a `FixTaskQueueSummary`; then passed after fixing the fixture.
- `git diff --check`: passed after whitespace verification.

Implemented demo evidence bundle from `docs/plans/150-demo-evidence-bundle.md`.

Changes:

- Added `GET /api/demo/evidence-bundle` as a read-only aggregate over demo readiness, smoke checklist, non-sensitive configuration, adapter fixture verification, queue summary, recent tasks, webhook delivery diagnostics, rejected-trigger summary, and active trigger quarantines.
- Added demo evidence response models with summary counts, latest evidence records, recent Pull Request URL, generated timestamp, and next actions.
- Added a dashboard `DemoEvidenceBundlePanel` near the existing setup/readiness area.
- Wired frontend refresh loading to fetch and render the evidence bundle without blocking the rest of the dashboard when the endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoReadinessControllerTests test`: first failed because the evidence bundle service, response models, and endpoint did not exist; then passed after backend implementation, 5 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because the frontend API and component did not exist; then passed after frontend implementation, 32 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: passed after App-level dashboard wiring, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 570 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 136 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo runbook export from `docs/plans/151-demo-runbook-export.md`.

Changes:

- Added `GET /api/demo/runbook` as a read-only Markdown export over the existing demo evidence bundle.
- Added `DemoRunbookService` to format status, summary, generated time, recent PR, recent task, latest webhook delivery, adapter fixture counts, queue counts, rejected-trigger counts, active quarantines, readiness checks, smoke checklist steps, and next actions.
- Added `getDemoRunbook` to the frontend API layer.
- Added a `Copy runbook` action to `DemoEvidenceBundlePanel` that fetches the Markdown only on click and copies it to the clipboard.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoRunbookServiceTests,DemoReadinessControllerTests test`: first failed because `DemoRunbookService` did not exist; then failed on a stale readiness field name and webhook fixture constructor; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because `getDemoRunbook` and the `Copy runbook` button did not exist; then passed after frontend implementation, 34 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: passed after App-level copy wiring, 92 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 573 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 139 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo script and health contract from `docs/plans/152-demo-script-and-health-contract.md`.

Changes:

- Added `GET /api/demo/script` as a read-only script read model over the existing demo evidence bundle.
- Added `DemoScriptService`, `DemoScriptVo`, and `DemoScriptStepVo` to return ordered operator actions for backend/dashboard access, configuration and safety posture, repository support, controlled `/agent fix` triggering, task execution tracking, and Pull Request evidence review.
- Included verification commands, success criteria, troubleshooting panel names, current evidence, next actions, generated time, and a health contract stating the endpoint does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Added frontend `getDemoScript`, typed `DemoScript` models, and `DemoScriptPanel`.
- Wired the dashboard to load and render the script near the existing demo evidence/readiness panels without blocking the rest of the dashboard when the script endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoScriptServiceTests,DemoReadinessControllerTests test`: first failed because the script service, response models, and endpoint did not exist; then passed after backend implementation, 7 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoScriptPanel.test.tsx src/App.test.tsx`: first failed because `getDemoScript`, `DemoScriptPanel`, and App-level rendering did not exist; then passed after frontend implementation, 92 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 576 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 142 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.
