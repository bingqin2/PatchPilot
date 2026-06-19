# Command Allowlist and Task Sandbox Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make command execution and repository file access explicitly reject non-allowlisted commands and paths outside the configured task workspace root.

**Architecture:** Add a shared command guard under the runner module and use it from Git and Maven runners. Extend repository file path and scan helpers so file reads, writes, tree listing, and code search also require repository roots under `patchpilot.workspace.root-dir`.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, ProcessBuilder, existing `WorkspaceProperties`.

## Global Constraints

- Do not accept arbitrary shell command strings from HTTP, issue comments, or model output.
- Keep command execution constructed by backend code only.
- Preserve existing Git and Maven runner public behavior for allowlisted commands.
- All command working directories must stay under `patchpilot.workspace.root-dir`.
- All repository file reads, writes, tree scans, and searches must stay under `patchpilot.workspace.root-dir`.
- Do not add Docker services or external sandboxing infrastructure in this phase.

---

## Task 1: Add Command Allowlist Guard

- [x] Add `CommandExecutionGuard`.
- [x] Allow only MVP Maven and Git command shapes.
- [x] Reject empty commands.
- [x] Reject non-allowlisted commands such as `rm -rf`.
- [x] Reject command working directories outside the configured workspace root.
- [x] Cover allowed and rejected commands with tests.

## Task 2: Guard Git and Maven Runners

- [x] Inject `CommandExecutionGuard` into `GitCommandRunner`.
- [x] Validate clone, branch, diff, add, commit, and push commands before `ProcessBuilder.start()`.
- [x] Inject `CommandExecutionGuard` into `MavenTestRunner`.
- [x] Validate `./mvnw test` and `mvn test` before `ProcessBuilder.start()`.
- [x] Add runner-level tests proving outside-workspace commands are rejected.

## Task 3: Guard Repository File Access Roots

- [x] Inject `WorkspaceProperties` into `WorkspacePathResolver`.
- [x] Reject repository roots outside the workspace root before resolving relative paths.
- [x] Inject `WorkspaceProperties` into `RepositoryFileScanner`.
- [x] Reject file tree and code search scans outside the workspace root.
- [x] Preserve existing relative path traversal rejection.

## Task 4: Validate And Document

- [x] Run focused command guard and runner tests.
- [x] Run focused file access sandbox tests.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Git and Maven runners enforce command allowlists.
- [x] Commands outside `patchpilot.workspace.root-dir` are rejected before process start.
- [x] File reads, writes, tree scans, and code searches reject repository roots outside the workspace root.
- [x] Existing task execution behavior remains unchanged for allowlisted commands inside a task workspace.
- [x] Full backend tests pass.
