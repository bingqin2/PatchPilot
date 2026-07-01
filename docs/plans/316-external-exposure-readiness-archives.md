# 316 External Exposure Readiness Archives

## Goal

Preserve the external exposure readiness gate as local evidence before an operator exposes PatchPilot through a temporary public URL.

## Scope

- Add backend archive APIs under `/api/security/external-exposure-readiness/archives`.
- Store the latest 20 archive rows with status, safe-to-expose flag, counts, timestamp, summary, and Markdown report.
- Use an in-memory repository for the default profile and MySQL/Flyway/MyBatis persistence for `local`, `docker`, and `idea`.
- Add dashboard archive controls, recent archive history, and Markdown report downloads to the existing external exposure readiness panel.
- Update README, product spec, architecture notes, and execution log.

## Non-Goals

- Do not create tasks, call the model, run tests, mutate Git, open Pull Requests, write GitHub comments, or change Cloudflare/GitHub settings.
- Do not expose secret values in archives or reports.
- Do not replace the live launch gate or GitHub webhook setup checks.

## Acceptance Criteria

- `POST /api/security/external-exposure-readiness/archives` captures the current readiness gate result as a local archive.
- `GET /api/security/external-exposure-readiness/archives` lists newest archives first and caps the visible history at 20.
- `GET /api/security/external-exposure-readiness/archives/{archiveId}/report/download` downloads the exact archived Markdown report.
- The dashboard loads recent archives, can create a new archive, prepends it to the visible list, and downloads archived reports.
- The archive path keeps the same safety boundary as other local evidence writes.

## Verification

- Backend RED/GREEN tests:
  - `ExternalExposureReadinessArchiveServiceTests`
  - `ExternalExposureReadinessArchiveControllerTests`
  - `MyBatisExternalExposureReadinessArchiveRepositoryTests`
  - `ExternalExposureReadinessArchiveMigrationTests`
- Frontend RED/GREEN tests:
  - `api.test.ts`
  - `ExternalExposureReadinessPanel.test.tsx`
  - `App.test.tsx`
- Final checks:
  - `mvn -q -pl PatchPilot test`
  - `npm --prefix frontend test -- --reporter=dot`
  - `npm --prefix frontend run build`
  - `git diff --check`
