# Repository Inspection Tools Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add safe, read-only repository inspection tools for listing files and searching code inside a prepared workspace.

**Architecture:** Keep agent-callable tools under `agent.tool`. Add a small shared scanner that walks only inside the repository path, skips noisy/generated directories, and returns repository-relative paths. This phase remains internal Java tooling only; it does not call model providers, edit files, push branches, or create Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, `java.nio.file`.

## Global Constraints

- Use Java filesystem APIs; do not shell out to `find`, `grep`, or model-generated commands.
- Never follow paths outside the prepared repository directory.
- Skip noisy/generated directories such as `.git`, `target`, `build`, `node_modules`, and `.idea`.
- Keep outputs deterministic by sorting repository-relative paths.
- Limit outputs so future model prompts do not receive unbounded repository contents.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/RepositoryFileScanner.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/RepoTreeTool.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/CodeSearchTool.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/RepositoryInspectionToolsTests.java`

Modify:

- `docs/progress/execution-log.md`
- `docs/plans/008-repository-inspection-tools.md`

## Task 1: Add Repository Tree Tool

**Interfaces:**

- `RepoTreeTool#tree(Path repositoryDir)` returns a newline-separated file list.
- `RepositoryFileScanner#listFiles(Path repositoryDir, int maxFiles)` returns sorted repository-relative paths.

- [x] Write failing tests that verify `RepoTreeTool` lists sorted regular files and skips `.git` and `target`.
- [x] Run `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test` and verify it fails because the new tools do not exist.
- [x] Add `RepositoryFileScanner` with repository-root normalization, skipped directory segments, sorted output, and max file limiting.
- [x] Add `RepoTreeTool` that returns `String.join("\n", scanner.listFiles(...))`.
- [x] Run the targeted test and verify it passes.

## Task 2: Add Code Search Tool

**Interfaces:**

- `CodeSearchTool#search(Path repositoryDir, String query)` returns newline-separated `path:line: text` matches.

- [x] Add failing tests that verify `CodeSearchTool` finds matching lines with path and line number, skips generated directories, rejects blank queries, and limits output.
- [x] Run the targeted test and verify it fails because `CodeSearchTool` does not exist.
- [x] Add `CodeSearchTool` using `RepositoryFileScanner`, UTF-8 reads, binary/unreadable file skipping, and a fixed maximum of 50 matches.
- [x] Run the targeted test and verify it passes.

## Task 3: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Repository tree output is sorted and repository-relative.
- [x] Repository tree skips noisy/generated directories.
- [x] Code search returns `path:line: text` matches.
- [x] Code search rejects blank queries.
- [x] Code search output is bounded.
- [x] Full Maven verification passes.
