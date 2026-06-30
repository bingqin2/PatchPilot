# Self-Hosted Launch Publish Gates Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add GitHub publish-path and publish-permission gates to the final self-hosted launch readiness package so operators cannot treat the system as launch-ready while push, Pull Request, or issue-feedback permissions are missing.

**Architecture:** Extend `SelfHostedLaunchReadinessService` to consume `GitHubPublishReadinessService` and `GitHubPublishPermissionReadinessService` beside the existing demo readiness and evidence bundle sources. Convert their string statuses into `DemoReadinessStatus` checks, include them in the aggregate launch status, next actions, and Markdown report, then surface those checks in the existing dashboard panel.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, React/Vite, Vitest.

## Global Constraints

- The launch readiness package remains read-only: it must not create tasks, call the model, run tests, mutate Git, create Pull Requests, write issue comments, archive records, or write to GitHub.
- The GitHub token value must never appear in launch readiness responses, Markdown reports, tests, or frontend output.
- The feature must be one complete operator-visible slice: backend aggregation, frontend display/tests, docs, progress log, and full verification.

---

### Task 1: Backend Launch Publish Gates

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/demo/SelfHostedLaunchReadinessService.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/demo/SelfHostedLaunchReadinessServiceTests.java`

**Interfaces:**
- Consumes: `GitHubPublishReadinessService#getReadiness(String owner, String repository): GitHubPublishReadinessVo`
- Consumes: `GitHubPublishPermissionReadinessService#getReadiness(String owner, String repository): GitHubPublishPermissionReadinessVo`
- Produces: additional `DemoSelfHostedLaunchCheckVo` entries named `GitHub publish path` and `GitHub publish permissions`

- [x] **Step 1: Write failing backend tests**

Add tests proving the launch package is `READY` only when both GitHub publish checks are ready, `NEEDS_ATTENTION` when publish permissions are read-only, and `BLOCKED` when the publish path is blocked.

Run:

```bash
mvn -q -pl PatchPilot -Dtest=SelfHostedLaunchReadinessServiceTests test
```

Expected: FAIL because the launch package does not include the two new checks.

- [x] **Step 2: Implement backend aggregation**

Inject the two GitHub readiness services in production, add a test constructor that accepts suppliers, map `READY` to `DemoReadinessStatus.READY`, `BLOCKED` to `DemoReadinessStatus.BLOCKED`, and everything else to `DemoReadinessStatus.NEEDS_ATTENTION`.

- [x] **Step 3: Verify backend focused tests**

Run:

```bash
mvn -q -pl PatchPilot -Dtest=SelfHostedLaunchReadinessServiceTests test
```

Expected: PASS.

### Task 2: Frontend Launch Panel Coverage

**Files:**
- Modify: `frontend/src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx`
- Modify: `frontend/src/App.test.tsx`

**Interfaces:**
- Consumes: existing `DemoSelfHostedLaunchReadiness.checks`
- Produces: visible launch readiness cards for `GitHub publish path` and `GitHub publish permissions`

- [x] **Step 1: Write failing frontend tests**

Update the panel and App smoke fixtures to include the two new checks. Assert the UI renders the check names, statuses, and operator actions so the final go/no-go readout shows publish blockers.

Run:

```bash
npm --prefix frontend test -- --run src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx src/App.test.tsx -t "self-hosted launch|operational task dashboard"
```

Expected: FAIL until fixture expectations and rendered evidence are aligned.

- [x] **Step 2: Implement frontend fixture/display alignment**

Keep the panel generic over `readiness.checks`; update tests and App fixture expectations to prove the new backend checks appear in the final launch readout without adding a second bespoke panel.

- [x] **Step 3: Verify frontend focused tests**

Run:

```bash
npm --prefix frontend test -- --run src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx src/App.test.tsx -t "self-hosted launch|operational task dashboard"
```

Expected: PASS.

### Task 3: Documentation and Full Verification

**Files:**
- Modify: `README.md`
- Modify: `docs/product/spec.md`
- Modify: `docs/progress/execution-log.md`

- [x] **Step 1: Document the strengthened launch gate**

Update docs to say self-hosted launch readiness now includes GitHub publish path and publish permission gates, including push, Pull Request, and issue-feedback capability.

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

Commit the complete slice, merge back to `main`, delete `310-self-hosted-launch-publish-gates`, and push `main` if local credentials permit.
