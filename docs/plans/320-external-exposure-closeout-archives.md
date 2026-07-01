# External Exposure Closeout Archives Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Preserve exact external exposure closeout evidence as local archives after a temporary public URL session is shut down.

**Architecture:** Reuse `ExternalExposureCloseoutService` as the source of truth and add a capped archive repository with default in-memory storage plus MyBatis storage for `local`, `docker`, and `idea` profiles. Expose archive, list, and download endpoints under `/api/security/external-exposure-closeout`, then render the archive controls and history in the existing external exposure dashboard panel.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Flyway, JUnit 5, React, TypeScript, Vite, Vitest.

## Global Constraints

- Archive creation is PatchPilot-local evidence only: no task creation, model calls, public URL probing, GitHub webhook mutation, Git mutation, Pull Request creation, GitHub comments, or external sending.
- Preserve exact generated Markdown from the closeout gate at archive time.
- Keep this as one complete slice: backend API, persistence, frontend operator UI, docs, progress log, and verification.
- Follow RED/GREEN: tests must fail before new archive production code exists.

---

## Task 1: Backend Closeout Archive API and Storage

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/domain/ExternalExposureCloseoutArchiveVo.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/domain/entity/ExternalExposureCloseoutArchiveEntity.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/convert/ExternalExposureCloseoutArchiveConvert.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/mapper/ExternalExposureCloseoutArchiveMapper.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/service/ExternalExposureCloseoutArchiveRepository.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/service/impl/InMemoryExternalExposureCloseoutArchiveRepository.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/service/impl/MyBatisExternalExposureCloseoutArchiveRepository.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutArchiveService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutController.java`
- Create: `PatchPilot/src/main/resources/db/migration/V65__create_external_exposure_closeout_archive.sql`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutArchiveServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/ExternalExposureCloseoutControllerTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/convert/ExternalExposureCloseoutArchiveConvertTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/mapper/ExternalExposureCloseoutArchiveMigrationTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/security/exposure/service/MyBatisExternalExposureCloseoutArchiveRepositoryTests.java`

**Interfaces:**
- Consumes: `ExternalExposureCloseoutService.getCloseout()`.
- Produces: `POST /api/security/external-exposure-closeout/archives`, `GET /api/security/external-exposure-closeout/archives`, and `GET /api/security/external-exposure-closeout/archives/{archiveId}/report/download`.

**Behavior:**
- Archive records freeze status, ready flag, summary, next action, latest session id/status, public URL, webhook URL, linked readiness archive id, handoff status, archive freshness, counts, generated time, archived time, evidence notes, next actions, download actions, side-effect contract, and Markdown report.
- Archive list returns newest first and caps at 20.
- Download returns the exact archived Markdown, not a recomputed closeout report.

**Verification:**
- RED: `mvn -q -pl PatchPilot -Dtest=ExternalExposureCloseoutArchiveServiceTests,ExternalExposureCloseoutControllerTests,ExternalExposureCloseoutArchiveConvertTests,ExternalExposureCloseoutArchiveMigrationTests,MyBatisExternalExposureCloseoutArchiveRepositoryTests test` fails because archive types and endpoints do not exist.
- GREEN: the same command passes after implementation.

**Status:** Complete.

## Task 2: Frontend Closeout Archive Controls

**Files:**
- Modify: `frontend/src/types.ts`
- Modify: `frontend/src/api.ts`
- Modify: `frontend/src/api.test.ts`
- Modify: `frontend/src/dashboard/components/ExternalExposureReadinessPanel.tsx`
- Modify: `frontend/src/dashboard/components/ExternalExposureReadinessPanel.test.tsx`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.test.tsx`

**Interfaces:**
- Consumes: closeout archive, list, and download endpoints.
- Produces: an `Archive closeout` action, recent archived closeout list, archived status/summary counts, and archived Markdown download action in the external exposure panel.

**Behavior:**
- Archive button is disabled until a live closeout result is loaded.
- After archive creation, the new archive appears at the top of recent closeouts without losing the current closeout gate state.
- Downloading an archived report uses the archive endpoint, not the current closeout download endpoint.

**Verification:**
- RED: `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot` fails because frontend archive helpers and UI are missing.
- GREEN: the same command passes after implementation.
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
