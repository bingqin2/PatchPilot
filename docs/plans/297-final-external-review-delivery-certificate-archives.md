# 297 Final External Review Delivery Certificate Archives

## Goal

Freeze the certified final external-review delivery certificate as durable local evidence after the live certificate becomes `READY` and certified.

## Scope

- Add backend archive persistence for final external-review delivery certificates.
- Guard archive creation so only `READY` and certified certificates can be frozen.
- Expose create, list, and archived-report download API endpoints.
- Show recent certificate archives in the final demo acceptance panel.
- Let operators archive the current certificate and download historical Markdown certificate reports.
- Document the API, no-side-effect boundary, and verification evidence.

## Backend

- Add `DemoFinalExternalReviewDeliveryCertificateArchiveVo`, entity, mapper, converter, repository interface, in-memory repository, MyBatis repository, and `V57__create_demo_final_external_review_delivery_certificate_archive.sql`.
- Add `DemoFinalExternalReviewDeliveryCertificateArchiveService` with `archiveCurrentCertificate`, `listRecentArchives`, and `findArchive`.
- Add `POST /api/demo/final-external-review-delivery-certificate/archives`, `GET /api/demo/final-external-review-delivery-certificate/archives`, and `GET /api/demo/final-external-review-delivery-certificate/archives/{archiveId}/report/download`.
- Record protected admin audit evidence for archive creation.

## Frontend

- Add TypeScript types and API helpers for certificate archive create/list/download.
- Load archive history during dashboard refresh.
- Add archive and archived-report download controls under `Final external-review delivery certificate`.
- Show archive errors and recent archived certificate records beside the live certificate.

## Validation

- Backend focused archive/controller tests.
- Full backend Maven test suite.
- Frontend API and full dashboard smoke tests.
- Production dashboard build.
