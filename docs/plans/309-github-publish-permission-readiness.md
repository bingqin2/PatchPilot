# GitHub Publish Permission Readiness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a read-only operator diagnostic that tells whether the configured GitHub token has repository permissions needed for push, Pull Request creation, and issue feedback before a live `/agent fix` demo.

**Architecture:** Add a dedicated GitHub repository-permissions probe that reads the existing `GET /repos/{owner}/{repo}` API response and parses only non-secret permission booleans. A backend readiness service exposes those permissions as explicit checks, and the frontend operator checklist renders a permission card next to existing GitHub readiness signals.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, React/Vite, Vitest.

## Global Constraints

- The diagnostic must be read-only and must not run `git push`, create branches, open Pull Requests, write issue comments, or expose token values.
- Reuse the current GitHub credential configuration and admin-protected readiness controller.
- Show concrete next actions for missing token, missing repository target, read-only token, and partial permissions.
- Keep the slice complete: backend probe/service/API, frontend operator UI, docs/progress notes, and regression tests.

---

### Task 1: Backend Permission Probe and Readiness API

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubRepositoryPermissionProbeResult.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubPublishPermissionReadinessCheckVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/domain/GitHubPublishPermissionReadinessVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubRepositoryPermissionProbe.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubRepositoryPermissionHttpProbe.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubPublishPermissionReadinessService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessController.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubRepositoryPermissionHttpProbeTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubPublishPermissionReadinessServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/github/credential/GitHubCredentialReadinessControllerTests.java`

**Interfaces:**
- Produces: `GitHubRepositoryPermissionProbe#check(String token, String owner, String repository): GitHubRepositoryPermissionProbeResult`
- Produces: `GitHubPublishPermissionReadinessService#getReadiness(String owner, String repository): GitHubPublishPermissionReadinessVo`
- Produces: `GET /api/github/publish-permission-readiness?owner={owner}&repository={repository}`

- [x] **Step 1: Write failing probe tests**

Assert the HTTP probe sends a read-only `GET /repos/{owner}/{repo}` request, parses `default_branch` and `permissions.pull/push/admin/maintain`, URL-encodes path segments, and fails with stable messages for non-200 responses or missing permissions.

- [x] **Step 2: Write failing service tests**

Cover READY when token can read and push, NEEDS_ATTENTION when only pull is true, NEEDS_ATTENTION when repository target is missing, and BLOCKED when token is missing. Assert no token value appears in messages or evidence.

- [x] **Step 3: Write failing controller test**

Assert the admin-protected endpoint returns the non-sensitive permission readiness and requires an admin token.

- [x] **Step 4: Implement probe, VO, service, and controller route**

Use the existing `GitHubProperties` token source. Aggregate checks for repository read access, branch push permission, Pull Request publication, and issue feedback permission guidance. Treat `push`, `admin`, or `maintain` as write-capable.

- [x] **Step 5: Run focused backend tests**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=GitHubRepositoryPermissionHttpProbeTests,GitHubPublishPermissionReadinessServiceTests,GitHubCredentialReadinessControllerTests test
```

Expected: PASS.

### Task 2: Frontend Operator Permission Surface

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.tsx`
- Modify: `frontend/src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`

**Interfaces:**
- Consumes: `GET /api/github/publish-permission-readiness`
- Produces: `githubPublishPermissionReadiness` prop in the operator setup checklist.

- [x] **Step 1: Write failing API and UI tests**

Assert API URL construction, App loading with selected repository target, and checklist rendering for permission status, push-capable state, PR-capable state, issue-feedback guidance, and side-effect contract.

- [x] **Step 2: Implement frontend types, API helper, and App loading**

Load permission readiness beside the existing publish readiness call. Use repository filters when present; otherwise use backend defaults.

- [x] **Step 3: Render permission card in the operator checklist**

Add a checklist row and detailed card that separates read, push, PR, and issue-feedback permission checks with concrete operator actions.

- [x] **Step 4: Run focused frontend tests**

Run:

```bash
npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx -t "operator setup|publish permission|GitHub publish permission"
```

Expected: PASS.

### Task 3: Documentation and Full Verification

**Files:**
- Modify: `README.md`
- Modify: `docs/progress/execution-log.md`

- [x] **Step 1: Document the endpoint**

Add the endpoint contract, permission meaning, side-effect guarantee, and local curl example to README.

- [x] **Step 2: Record execution evidence**

Append the feature summary and verification evidence to `docs/progress/execution-log.md`.

- [x] **Step 3: Run full verification**

Run:

```bash
mvn -q -pl PatchPilot test
npm --prefix frontend test -- --reporter=dot
npm --prefix frontend run build
git diff --check
```

Expected: all pass; existing warnings are acceptable if no failures occur.
