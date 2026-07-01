# External Exposure Closeout Archive Evidence Bundle Implementation Plan

**Goal:** Surface the latest archived external exposure closeout in the top-level demo evidence bundle and copied runbook so reviewers can verify temporary public URL shutdown from one artifact.

**Architecture:** Reuse the existing demo evidence bundle pattern by projecting `ExternalExposureCloseoutArchiveVo` into a compact archive evidence VO. The backend aggregates readiness, next action, download actions, and no-side-effect evidence; the frontend renders the same evidence with compatibility fallbacks for older responses.

**Tech Stack:** Java 17, Spring Boot, JUnit 5, React, TypeScript, Vitest.

## Tasks

- [x] Add RED backend tests proving the evidence bundle reports the latest external exposure closeout archive and requires one before READY.
- [x] Add RED runbook coverage proving the copied Markdown repeats the closeout archive id, session, public URL, webhook URL, and next action.
- [x] Add RED frontend tests proving the demo evidence panel renders the external exposure closeout archive evidence card and fallback guidance.
- [x] Implement the backend archive evidence VO, aggregation, status/next-action logic, and runbook output.
- [x] Implement frontend types, fallback data, and evidence card rendering.
- [x] Update product docs and execution log with the new top-level evidence behavior.
- [x] Run backend/frontend tests, build, whitespace check, and staged secret scan before committing.

## Acceptance Criteria

- `GET /api/demo/evidence-bundle` includes `externalExposureCloseoutArchiveEvidence`.
- Missing or non-ready closeout archives make the bundle `NEEDS_ATTENTION` or `BLOCKED`.
- The runbook includes the latest closeout archive id, session id, public URL, webhook URL, archive freshness, and download actions.
- The dashboard shows a dedicated external exposure closeout archive card without hiding missing legacy data.
- The feature does not create tasks, call the model, mutate Git, write to GitHub, or reopen public exposure.
