# External Exposure Closeout Gate Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add one read-only gate that tells an operator whether a temporary public URL exposure has been safely closed after a live demo or smoke test.

**Architecture:** Reuse the external exposure handoff package and session tracker as inputs. A new backend service computes `READY`, `NEEDS_ATTENTION`, or `BLOCKED` from the latest session and handoff evidence, exposes JSON plus Markdown download endpoints, and the dashboard renders the result in the existing external exposure panel.

**Tech Stack:** Java 17, Spring Boot, JUnit 5, React, TypeScript, Vite, Vitest.

## Global Constraints

- Keep this feature read-only: no task creation, model calls, public URL probing, GitHub webhook mutation, Git mutation, Pull Request creation, GitHub comments, or archive writes.
- Use existing external exposure package structure under `io.patchpilot.backend.security.exposure`.
- Follow TDD: backend tests fail before service/controller code exists; frontend tests fail before helpers/UI exist.
- Keep the feature as one complete slice: backend API, frontend operator UI, docs, progress log, and verification.

---

## Task 1: Backend Closeout Gate

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/domain/ExternalExposureCloseoutVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutController.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutControllerTests.java`

**Interfaces:**
- Consumes: `ExternalExposureSessionService.listRecentSessions()` and `ExternalExposureHandoffPackageService.getHandoffPackage()`.
- Produces: `ExternalExposureCloseoutVo getCloseout()` and endpoints `GET /api/security/external-exposure-closeout`, `GET /api/security/external-exposure-closeout/report/download`.

**Behavior:**
- `READY`: latest session is `CLOSED`, has `closedBy`, `closedAt`, `closeNotes`, a linked readiness archive id, and the current handoff package is `READY`.
- `BLOCKED`: latest session is `ACTIVE`, because the temporary public URL is still exposed.
- `NEEDS_ATTENTION`: no session exists, latest session is closed with missing close evidence, or current handoff evidence is no longer ready.

**Verification:**
- RED: `mvn -q -pl PatchPilot -Dtest=ExternalExposureCloseoutServiceTests,ExternalExposureCloseoutControllerTests test` fails because the closeout VO/service/controller do not exist.
- GREEN: the same command passes after implementation.

**Status:** Complete.

## Task 2: Frontend Closeout Panel

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/dashboard/components/ExternalExposureReadinessPanel.tsx`
- Modify: `frontend/src/dashboard/components/ExternalExposureReadinessPanel.test.tsx`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`

**Interfaces:**
- Consumes: `GET /api/security/external-exposure-closeout` and report download endpoint.
- Produces: an `Exposure closeout` section with status, summary, latest session id, linked archive id, evidence notes, next actions, side-effect contract, and report download.

**Verification:**
- RED: `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot` fails because API helpers and UI are missing.
- GREEN: focused frontend tests pass after implementation.
- App wiring: `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot` passes.

**Status:** Complete.

## Task 3: Docs, Progress, and Final Verification

**Files:**
- Modify: `README.md`
- Modify: `docs/product/spec.md`
- Modify: `docs/product/frontend-design.md`
- Modify: `docs/product/architecture.md`
- Modify: `docs/progress/execution-log.md`

**Verification:**
- `mvn -q -pl PatchPilot test`
- `npm --prefix frontend test -- --reporter=dot`
- `npm --prefix frontend run build`
- `git diff --check`
- Secret scan staged diff before commit.

**Status:** Complete.
