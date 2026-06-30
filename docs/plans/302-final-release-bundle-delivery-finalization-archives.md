# 302 Final Release Bundle Delivery Finalization Archives

## Goal

Freeze the READY final external-review release bundle delivery finalization into durable local evidence so the terminal reviewer handoff proof can be downloaded later even if live read models change.

## Backend Scope

- Add a final release-bundle delivery finalization archive VO, entity, converter, mapper, repository, migration, and service.
- Require the live finalization gate to be `READY` and finalized before archive creation.
- Expose:
  - `POST /api/demo/final-external-review-release-bundle/delivery-finalization/archives`
  - `GET /api/demo/final-external-review-release-bundle/delivery-finalization/archives`
  - `GET /api/demo/final-external-review-release-bundle/delivery-finalization/archives/{archiveId}/report/download`
- Record protected admin audit evidence for archive creation.
- Add archive evidence to the top-level demo evidence bundle.

## Frontend Scope

- Add API helpers and types for final release-bundle delivery finalization archives.
- Show archive readiness in the demo evidence bundle.
- Add archive creation, archive history, load errors, and report downloads to the final demo acceptance panel.
- Refresh the evidence bundle and archive list after archive creation.

## Validation

- Focused backend archive/service/controller/evidence bundle tests.
- Frontend API and dashboard smoke tests.
- Full backend and frontend test suites before merge.
