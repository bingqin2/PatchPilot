# 296 Final External Review Delivery Certificate

## Goal

Certify the latest archived final external-review delivery closure as the terminal reviewer-facing proof. After the frozen package delivery finalization has been archived, operators should be able to inspect one read-only certificate, download its Markdown report, and see the linked package archive, delivery receipt, Pull Request, and target/channel evidence from the dashboard.

## Scope

- Derive a certificate from the latest final external-review package delivery finalization archive.
- Report certified/not-certified state, readiness status, next action, package archive, delivery receipt, task, Pull Request, delivery target/channel, receipt freshness, generated time, and evidence checks.
- Expose read-only JSON and Markdown download endpoints.
- Add frontend API helpers, App loading/refresh wiring, dashboard certificate rendering, and certificate report download.
- Update product, README, frontend design, and progress docs.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run tests, mutate Git, archive records, record receipts, push branches, create Pull Requests, or write GitHub comments.
- Do not replace the finalization archive history; the certificate is a read-only summary derived from that durable evidence.

## Verification

- Backend tests cover certified and missing-archive certificate states plus controller JSON/download routes.
- Frontend tests cover certificate API helpers, App startup loading, dashboard rendering, and report download wiring.
- Run backend focused tests, frontend API/App tests, full backend tests, full frontend tests, frontend production build, and `git diff --check` before merging.
