# 303 Final Release Bundle Delivery Certificate

## Goal

Create one read-only terminal certificate for the delivered final external-review release bundle. After the latest release-bundle delivery finalization has been frozen as an archive, operators should be able to open a single JSON/Markdown proof that the reviewer-facing release bundle was archived, delivered, receipt-fresh, and tied to the final Pull Request evidence.

## Backend Scope

- Add `DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo` and `DemoFinalExternalReviewReleaseBundleDeliveryCertificateService`.
- Derive the certificate from the latest `DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo`.
- Report `READY` and `certified=true` only when the latest archive is `READY`, finalized, and release-bundle receipt-fresh.
- Expose:
  - `GET /api/demo/final-external-review-release-bundle/delivery-certificate`
  - `GET /api/demo/final-external-review-release-bundle/delivery-certificate/report/download`

## Frontend Scope

- Add TypeScript type and API helpers for the release-bundle delivery certificate.
- Load the certificate in the dashboard App alongside the release-bundle finalization evidence.
- Render a certificate card in the final demo acceptance panel with status, linked archive/receipt/certificate/package proof, checks, evidence notes, and Markdown download.

## Non-Goals

- Do not create a new archive table in this slice.
- Do not record delivery receipts or mutate Git/GitHub.
- Do not replace the existing release-bundle, receipt, finalization, or finalization archive flows.

## Validation

- Backend service and controller RED/GREEN tests.
- Frontend API and panel RED/GREEN tests.
- Full backend and frontend verification before merging to `main`.
