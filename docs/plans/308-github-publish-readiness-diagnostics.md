# GitHub Publish Readiness Diagnostics Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add an operator-facing readiness diagnostic that explains whether PatchPilot can publish generated work to GitHub before a live `/agent fix` demo.

**Architecture:** The backend exposes a read-only admin API that aggregates GitHub token readiness, demo repository access readiness, configured repository target, and non-mutating publish guidance. The frontend surfaces the result in the operator setup checklist so credential, network, repository-access, and push-readiness failures are visible before a task reaches commit/push.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, React/Vite, Vitest.

## Global Constraints

- The readiness endpoint must not run `git push`, create branches, open Pull Requests, write GitHub comments, or expose secrets.
- Reuse existing GitHub credential and repository-access probes rather than adding a second network client.
- The dashboard must show concrete next actions without displaying token values.
- Keep the slice complete: backend API, frontend operator UI, docs/progress notes, and regression tests.

---

### Task 1: Backend Publish Readiness API

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubPublishReadinessCheckVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubPublishReadinessVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubPublishReadinessService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessController.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubPublishReadinessServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessControllerTests.java`

**Interfaces:**
- Produces: `GitHubPublishReadinessService#getReadiness(String owner, String repository): GitHubPublishReadinessVo`
- Produces: `GET /api/github/publish-readiness?owner={owner}&repository={repository}`

- [x] **Step 1: Write failing service tests**

Cover READY when credentials and repository access are ready; BLOCKED when token is missing; NEEDS_ATTENTION when repository target is missing or access probe fails. Assert the side-effect contract says the endpoint is read-only and does not mutate GitHub.

- [x] **Step 2: Write failing controller test**

Assert the admin-protected endpoint returns the aggregate readiness without token material.

- [x] **Step 3: Implement VO/service/controller**

Aggregate existing `GitHubCredentialReadinessService` and `GitHubRepositoryAccessReadinessService`. Prefer query owner/repository, then configured demo owner/repository. Do not run Git commands.

- [x] **Step 4: Run focused backend tests**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubPublishReadinessServiceTests,GitHubCredentialReadinessControllerTests test
```

Expected: PASS.

### Task 2: Frontend Operator Checklist Surface

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`

**Interfaces:**
- Consumes: `GET /api/github/publish-readiness`
- Produces: `githubPublishReadiness` prop in the operator setup checklist.

- [x] **Step 1: Write failing API and UI tests**

Assert `getGitHubPublishReadiness()` calls the new endpoint and the operator checklist displays status, summary, checks, safe publish command guidance, and side-effect contract.

- [x] **Step 2: Implement frontend types/API/App wiring**

Load publish readiness with the rest of setup data, pass it to the checklist, and handle failed loads through the existing dashboard error path.

- [x] **Step 3: Implement checklist rendering**

Add a dedicated publish readiness row and detailed card with check names/statuses and next actions.

- [x] **Step 4: Run focused frontend tests**

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx
```

Expected: PASS.

### Task 3: Documentation and Full Verification

**Files:**
- Modify: `README.md`
- Modify: `docs/progress/execution-log.md`

- [x] **Step 1: Document the endpoint**

Add the endpoint contract, side effects, and operator use case to README.

- [x] **Step 2: Record execution evidence**

Append the feature summary and verification commands to `docs/progress/execution-log.md`.

- [x] **Step 3: Run full verification**

Run:

```bash
mvn -q -pl PatchPilot test
npm --prefix frontend test -- --reporter=dot
npm --prefix frontend run build
git diff --check
```

Expected: all pass; existing warnings are acceptable if no failures occur.
