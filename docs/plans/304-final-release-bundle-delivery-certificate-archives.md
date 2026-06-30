# 304 - Final Release Bundle Delivery Certificate Archives

## Goal

Freeze the terminal final external-review release-bundle delivery certificate into durable, downloadable archives. This closes the last evidence gap after a certificate is generated: reviewers can inspect the current certificate, archive it, and later download the exact same markdown report without depending on mutable runtime state.

## Scope

- Add backend archive model, converter, repository, service, mapper, and Flyway migration for release-bundle delivery certificate archives.
- Add API endpoints to archive the current READY certificate, list recent archives, and download an archived report.
- Record an operator safety audit when a certificate archive is created.
- Extend the dashboard API client and final acceptance panel with archive, history, and download controls.
- Cover converter, service, controller, API client, and UI behavior with tests.

## Out of Scope

- Reworking the existing final external-review release bundle certificate readiness logic.
- Changing the release-bundle delivery receipt or finalization archive schemas.
- Adding remote artifact storage; archives remain in the existing repository persistence layer.

## Verification

- `mvn -q -pl PatchPilot test`
- `cd frontend && npm test -- --reporter=dot`
- `cd frontend && npm run build`
- `git diff --check`
