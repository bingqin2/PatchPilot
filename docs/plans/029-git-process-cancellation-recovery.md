# Git Process Cancellation Recovery Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Allow cancellation of running Git clone/commit/push processes and surface clear recovery guidance for half-finished Git workspaces.

**Architecture:** Reuse the task-scoped `TaskProcessRegistry` from plan 028. Add task-aware Git runner methods, pass the task id through workspace preparation, commit, and push, and add a small recovery inspector that detects common Git lock and in-progress operation files after a failed or cancelled Git command.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5.

## Global Constraints

- Do not auto-delete Git lock files or run destructive recovery commands.
- Keep Maven process cancellation behavior unchanged.
- Git recovery output must be sanitized and suitable for task failure messages or tool-call audit output.
- Do not execute git-changing commands from Codex; provide commands for the user.

---

### Task 1: Git Recovery Inspector

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/recovery/GitWorkspaceRecoveryInspector.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/recovery/GitWorkspaceRecoveryInspectorTests.java`

- [x] Write failing tests for detecting `.git/index.lock`, `.git/HEAD.lock`, `.git/MERGE_HEAD`, `.git/rebase-merge`, and `.git/rebase-apply`.
- [x] Implement read-only recovery inspection.
- [x] Return a concise guidance string that names the detected state and says manual cleanup is required.

### Task 2: Task-Aware Git Process Runner

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`

- [x] Add failing tests showing Git processes register with `TaskProcessRegistry` when a task id is provided.
- [x] Add task-aware overloads for clone, branch creation, diff, stage, commit, and push.
- [x] Preserve existing non-task-aware methods for direct callers.
- [x] Unregister Git processes after completion, timeout, interruption, or IO failure.

### Task 3: Wire Task Id Through Workspace And Git Tools

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/CommitTool.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/PushTool.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/service/GitWorkspaceServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/CommitToolTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/PushToolTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

- [x] Add failing tests showing clone, branch creation, commit, and push receive the task id.
- [x] Pass task id from `CloneWorkspaceCommand` to Git clone and branch creation.
- [x] Add task-aware `CommitTool#commitAll(String taskId, Path repositoryDir, String message)`.
- [x] Add task-aware `PushTool#pushBranch(String taskId, Path repositoryDir, String branchName)`.
- [x] Update executor calls to use task-aware commit and push.

### Task 4: Recovery Guidance In Git Failures

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/CommitTool.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/PushTool.java`
- Test: same files as Task 3 plus recovery inspector tests.

- [x] Add failing tests showing lock/in-progress state is appended to clone, branch, commit, and push failure messages.
- [x] Inject `GitWorkspaceRecoveryInspector` into workspace service, commit tool, and push tool.
- [x] Append recovery guidance only when the inspector finds a known state.
- [x] Keep normal failure messages unchanged when no recovery state is detected.

### Task 5: Documentation And Verification

**Files:**
- Modify: `docs/progress/execution-log.md`

- [x] Record implementation summary and validation commands.
- [x] Run targeted tests for recovery, Git runner, workspace service, commit/push tools, and executor.
- [x] Run full `mvn -pl PatchPilot test`.
