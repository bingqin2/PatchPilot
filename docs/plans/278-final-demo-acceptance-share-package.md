# 278 Final Demo Acceptance Share Package

## Goal

Turn the final demo acceptance summary into a reviewer-facing share package with send-ready status, required attachments, pre-send checks, message template, and Markdown download.

## Why This Matters

The demo can now prove final acceptance from launch and task certificates. Operators still need one packaged artifact they can copy or download before sharing the final acceptance evidence externally.

## Scope

- Add a read-only backend share package service that wraps `DemoAcceptanceSummaryService`.
- Add `GET /api/demo/final-acceptance-share-package` and `GET /api/demo/final-acceptance-share-package/report/download`.
- Add frontend API helpers, TypeScript types, and dashboard rendering inside the final acceptance panel.
- Include copy/download actions for the package message and Markdown report.
- Update README, product docs, frontend design docs, and execution log.

## Non-Goals

- Do not send email, comments, or external messages.
- Do not create tasks, call the model, run tests, mutate Git, archive records, record receipts, or write to GitHub.
- Do not add persistence; this is a current read-only package derived from existing certificate archives.

## Validation

- Backend RED tests first for accepted and not-accepted package states.
- Controller RED tests first for JSON and Markdown download endpoints.
- Frontend RED tests first for API helpers and dashboard package rendering/copy/download behavior.
- Full backend tests, full frontend tests, frontend build, and `git diff --check` before merge.
