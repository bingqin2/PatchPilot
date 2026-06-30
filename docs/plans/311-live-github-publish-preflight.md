# Live GitHub Publish Preflight Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a read-only live GitHub publish preflight that tells operators whether the target repository is clean enough for a live `/agent fix` publish run.

**Architecture:** Add a GitHub preflight probe for non-mutating branch and Pull Request metadata, aggregate it with the existing publish path and permission readiness services, expose it through `/api/github/live-publish-preflight`, and surface the result in the dashboard operator setup checklist.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, React/Vite, Vitest.

## Global Constraints

- The preflight must be read-only: it must not run `git push`, create branches, open Pull Requests, write issue comments, create tasks, call the model, or mutate GitHub.
- The GitHub token value must never appear in API responses, Markdown, tests, logs, or frontend output.
- The feature must be one complete operator-visible slice: backend probe/service/controller, frontend display/tests, docs, progress log, and verification.

---

### Task 1: Backend Live Publish Preflight

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubLivePublishPreflightProbe.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubLivePublishPreflightHttpProbe.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubLivePublishPreflightService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubLivePublishPreflightProbeResult.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubLivePublishPreflightCheckVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubLivePublishPreflightVo.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessController.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubLivePublishPreflightServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubLivePublishPreflightHttpProbeTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessControllerTests.java`

**Interfaces:**
- Consumes: `GitHubPublishReadinessService#getReadiness(String owner, String repository)`
- Consumes: `GitHubPublishPermissionReadinessService#getReadiness(String owner, String repository)`
- Produces: `GET /api/github/live-publish-preflight`

- [x] **Step 1: Write failing backend tests**

Add tests proving the preflight is `READY` when publish readiness, permissions, branch inventory, and open PR inventory are clean; `NEEDS_ATTENTION` when old PatchPilot branches or open PatchPilot PRs exist; and `BLOCKED` when token or repository configuration is missing.

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubLivePublishPreflightServiceTests,GitHubLivePublishPreflightHttpProbeTests,GitHubCredentialReadinessControllerTests test
```

Expected: FAIL because the preflight types, probe, service, and endpoint do not exist.

- [x] **Step 2: Implement backend preflight**

Add a read-only HTTP probe that calls GitHub `GET /repos/{owner}/{repository}/branches?per_page=100` and `GET /repos/{owner}/{repository}/pulls?state=open&per_page=100`, then returns PatchPilot branch names and open PatchPilot PR URLs. Aggregate those facts with publish readiness and permission readiness.

- [x] **Step 3: Verify backend focused tests**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubLivePublishPreflightServiceTests,GitHubLivePublishPreflightHttpProbeTests,GitHubCredentialReadinessControllerTests test
```

Expected: PASS.

### Task 2: Frontend Operator Surface

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`

**Interfaces:**
- Consumes: `GitHubLivePublishPreflight`
- Produces: visible operator setup checklist row and live publish preflight detail card

- [x] **Step 1: Write failing frontend tests**

Update API, panel, and App tests so the operator setup checklist renders live publish preflight status, stale branch/open PR counts, side-effect contract, and next action.

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx -t "live publish preflight|operator setup"
```

Expected: FAIL until the frontend types, API loader, App state, and panel rendering exist.

- [x] **Step 2: Implement frontend wiring**

Load `/api/github/live-publish-preflight?owner=bingqin2&repository=PatchPilot` with the other setup probes, pass it into the operator setup checklist, and render the preflight checks plus stale artifact evidence.

- [x] **Step 3: Verify frontend focused tests**

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx -t "live publish preflight|operator setup"
```

Expected: PASS.

### Task 3: Documentation and Full Verification

**Files:**
- Modify: `README.md`
- Modify: `docs/product/spec.md`
- Modify: `docs/progress/execution-log.md`

- [x] **Step 1: Document the live preflight**

Document that the live publish preflight checks branch and Pull Request metadata through read-only GitHub API requests and should be run before the operator posts a live `/agent fix`.

- [x] **Step 2: Run full verification**

Run:

```bash
mvn -q -pl PatchPilot test
npm --prefix frontend test -- --reporter=dot
npm --prefix frontend run build
git diff --check
```

Expected: all pass; existing Vite large-chunk warnings are acceptable.

- [ ] **Step 3: Commit and merge**

Commit the complete slice, merge back to `main`, delete `311-live-github-publish-preflight`, and push `main` if local credentials permit.
