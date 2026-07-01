# Live Trigger Dry Run Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a read-only live GitHub issue-comment dry run so operators can verify an `/agent fix` comment before posting it on GitHub.

**Architecture:** Reuse the existing `TriggerEvaluationService` with `TriggerEvaluationSource.ISSUE_COMMENT`, but expose it through a GitHub-named operator API with an explicit no-side-effect contract. Surface the same result in the dashboard as a dedicated live trigger dry-run panel, separate from manual task creation and demo launch readiness.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, React, TypeScript, Vitest.

## Global Constraints

- The dry run must not create tasks, enqueue work, record rate-limit usage, run Git commands, call push/PR tools, or write GitHub comments.
- The endpoint must require the existing admin API token when admin security is configured.
- The frontend must explain that the dry run is read-only and must render the exact decision chain returned by the backend.
- The feature must include backend tests, frontend API/component/App tests, README/product/progress updates, and full verification.

---

### Task 1: Backend Live Trigger Dry Run API

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubTriggerDryRunController.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubTriggerDryRunRequestDto.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/domain/GitHubTriggerDryRunVo.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubTriggerDryRunControllerTests.java`
- Modify: `docs/plans/312-live-trigger-dry-run.md`

**Interfaces:**
- Consumes: `TriggerEvaluationService.evaluate(EvaluateTriggerCommand)`
- Produces: `POST /api/github/trigger-dry-run` returning `GitHubTriggerDryRunVo`

- [x] **Step 1: Write failing backend controller tests**

Add tests that call `POST /api/github/trigger-dry-run` with an admin token, assert `WOULD_CREATE_TASK` for an allowed issue comment, assert `BLOCKED` for an unsafe comment, assert the side-effect contract is present, and assert no task is created.

- [x] **Step 2: Run backend tests to verify RED**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubTriggerDryRunControllerTests test
```

Expected: FAIL because the controller, DTO, and VO do not exist.

- [x] **Step 3: Implement backend API**

Create a controller under `/api/github/trigger-dry-run` that validates request fields, forces `TriggerEvaluationSource.ISSUE_COMMENT`, delegates to `TriggerEvaluationService`, and wraps the result with repository, issue URL, summary, next action, and side-effect contract.

- [x] **Step 4: Run backend tests to verify GREEN**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubTriggerDryRunControllerTests test
```

Expected: PASS.

### Task 2: Frontend Live Trigger Dry Run Panel

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Create: `frontend/src/dashboard/components/LiveTriggerDryRunPanel.tsx`
- Create: `frontend/src/dashboard/components/LiveTriggerDryRunPanel.test.tsx`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`

**Interfaces:**
- Consumes: `postGitHubTriggerDryRun(input: GitHubTriggerDryRunInput)`
- Produces: dashboard panel labelled `Live trigger dry run`

- [x] **Step 1: Write failing frontend API/component/App tests**

Add API tests for the new endpoint, component tests for allowed and blocked dry-run results, and an App smoke assertion that submits the panel and copies evidence.

- [x] **Step 2: Run frontend tests to verify RED**

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveTriggerDryRunPanel.test.tsx src/App.test.tsx -t "live trigger dry run|GitHub trigger dry run"
```

Expected: FAIL because the API helper and panel do not exist.

- [x] **Step 3: Implement frontend API and panel**

Add typed input/result interfaces, API helper, App state/handler, and a panel that renders repository, issue, trigger user, comment, decision status, issue context state, gate decisions, read-only contract, next action, and copyable Markdown evidence.

- [x] **Step 4: Run frontend tests to verify GREEN**

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveTriggerDryRunPanel.test.tsx src/App.test.tsx -t "live trigger dry run|GitHub trigger dry run"
```

Expected: PASS.

### Task 3: Documentation, Full Verification, and Integration

**Files:**
- Modify: `README.md`
- Modify: `docs/product/spec.md`
- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/312-live-trigger-dry-run.md`

- [x] **Step 1: Document usage**

Document the curl command, admin token requirement, read-only side-effect contract, and how the dashboard panel should be used before posting a live `/agent fix`.

- [x] **Step 2: Run full backend/frontend verification**

Run:

```bash
mvn -q -pl PatchPilot test
npm --prefix frontend test -- --reporter=dot
npm --prefix frontend run build
git diff --check
```

Expected: all commands pass.

- [ ] **Step 3: Commit and merge**

Commit this complete feature slice, merge it back to `main`, delete `312-live-trigger-dry-run`, and push `main` if credentials permit.
