# Controlled File Tools MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add controlled repository file read/write and diff tools for future agent workflows.

**Architecture:** Put agent-callable side-effect boundaries under `agent.tool`, keep path validation reusable in the workspace module, and keep git command execution inside `workspace.runner`. This phase exposes internal Java tools only; it does not call model providers, edit files automatically from tasks, run tests in cloned repositories, push branches, or create Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, `java.nio.file`, `ProcessBuilder`.

## Global Constraints

- Keep the backend as one Spring Boot module under `PatchPilot/`.
- Follow package-by-domain structure from `docs/product/backend-code-standard.md`.
- All file operations must stay inside the prepared repository directory.
- Tool input must accept repository-relative paths only.
- Reject blank, absolute, and `..` path traversal inputs.
- Git commands must be constructed by backend code only; never execute model-generated shell strings.
- Do not call model providers, run Maven commands, push branches, or create Pull Requests in this phase.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/WorkspacePathResolver.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/FileReadTool.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/FileWriteTool.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/DiffTool.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/WorkspacePathResolverTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/FileToolsTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/DiffToolTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/005-file-tools-mvp.md`

## Implementation Tasks

### Task 1: Add Workspace Path Resolver

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/WorkspacePathResolver.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/WorkspacePathResolverTests.java`

**Interfaces:**

- `WorkspacePathResolver#resolveRepositoryPath(Path repositoryDir, String relativePath)` returns a normalized `Path`.
- It rejects blank paths, absolute paths, and paths that normalize outside `repositoryDir`.

- [x] **Step 1: Write failing resolver tests**

Add tests for valid nested paths, blank path rejection, absolute path rejection, and `../outside` rejection.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests test
```

Expected: compilation failure because `WorkspacePathResolver` does not exist.

- [x] **Step 3: Implement resolver**

Implement the resolver with `Path.of(relativePath)`, `isAbsolute()`, `repositoryDir.toAbsolutePath().normalize()`, `resolve(...).normalize()`, and `startsWith(repositoryRoot)`.

- [x] **Step 4: Run test to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 2: Add File Read And Write Tools

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/FileReadTool.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/FileWriteTool.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/FileToolsTests.java`

**Interfaces:**

- `FileReadTool#read(Path repositoryDir, String relativePath)` returns file content as UTF-8 text.
- `FileWriteTool#write(Path repositoryDir, String relativePath, String content)` writes UTF-8 text and creates parent directories when needed.
- Both tools use `WorkspacePathResolver`.

- [x] **Step 1: Write failing file tool tests**

Add tests that read an existing file, write a nested file, and reject path traversal for reads and writes.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=FileToolsTests test
```

Expected: compilation failure because the tools do not exist.

- [x] **Step 3: Implement file tools**

Implement UTF-8 read/write using `Files.readString`, `Files.createDirectories`, and `Files.writeString`.

- [x] **Step 4: Run test to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 3: Add Diff Tool And Runner Support

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/DiffTool.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/DiffToolTests.java`

**Interfaces:**

- `GitCommandRunner#diff(Path repositoryDir)` returns `GitCommandResult`.
- The command uses fixed arguments: `git -C <repositoryDir> diff --`.
- `DiffTool#diff(Path repositoryDir)` returns diff output and throws when git exits non-zero.

- [x] **Step 1: Write failing diff runner tests**

Add a temporary git repository test that modifies a tracked file and verifies `GitCommandRunner#diff(...)` includes the changed line.

- [x] **Step 2: Run runner test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test
```

Expected: compilation failure because `diff` does not exist.

- [x] **Step 3: Implement runner diff**

Add `diff(Path repositoryDir)` to `GitCommandRunner` using fixed arguments.

- [x] **Step 4: Write failing diff tool tests**

Add tests that verify `DiffTool` returns runner output and throws `IllegalStateException("git diff failed: ...")` when the runner exits non-zero.

- [x] **Step 5: Run tool test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=DiffToolTests test
```

Expected: compilation failure because `DiffTool` does not exist.

- [x] **Step 6: Implement diff tool**

Add `DiffTool` with constructor-injected `GitCommandRunner`.

- [x] **Step 7: Run tests to verify pass**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,DiffToolTests test
```

Expected: `BUILD SUCCESS`.

### Task 4: Validate And Update Docs

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/005-file-tools-mvp.md`

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

- [x] **Step 4: Runtime smoke check**

Call `GET /api/tasks` after Docker restart. Expected: successful API response. File tools are internal only in this phase, so unit tests are the runtime boundary check.

- [x] **Step 5: Update docs**

Record validation evidence in `docs/progress/execution-log.md` and mark completed checkboxes in this plan.

## Acceptance Checklist

- [x] Repository-relative path resolution rejects blank, absolute, and traversal paths.
- [x] File read tool reads UTF-8 files inside the repository.
- [x] File write tool writes UTF-8 files inside the repository and creates parent directories.
- [x] File read/write tools cannot escape the repository directory.
- [x] Diff runner uses controlled `git -C <repo> diff --` arguments.
- [x] Diff tool returns diff output and fails explicitly on non-zero git exit.
- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] Docker backend rebuilds and starts.
