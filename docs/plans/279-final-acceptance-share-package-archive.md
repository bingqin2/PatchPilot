# 279 Final Acceptance Share Package Archive

## Goal

Turn the final demo acceptance share package from a live read-only preview into an immutable operator handoff record. This gives a reviewer a stable package archive, history, and downloadable report after the end-to-end issue-to-PR demo is accepted.

## Scope

- Add a backend archive model for the current final acceptance share package.
- Persist archives with both in-memory and MyBatis repositories.
- Expose archive, list, and download endpoints:
  - `POST /api/demo/final-acceptance-share-package/archives`
  - `GET /api/demo/final-acceptance-share-package/archives`
  - `GET /api/demo/final-acceptance-share-package/archives/{archiveId}/report/download`
- Record an operator safety audit event when an archive is created.
- Add dashboard actions to archive the current package, show recent archives, and download an archived report.
- Update product/frontend docs and execution log.

## Non-Goals

- Do not send email, Slack, or GitHub messages.
- Do not create tasks, call the model, run tests, or mutate repositories.
- Do not change the acceptance decision rules from plan 278.

## Verification

- Backend focused tests for archive service, repository conversion, migration, and controller routes.
- Frontend focused tests for API calls and dashboard archive/download behavior.
- Full backend tests, full frontend tests, production frontend build, and whitespace check before merging.
