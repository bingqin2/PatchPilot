# Plan 254: Launch Share Delivery Finalization

## Goal

Close the final demo launch evidence loop by letting operators record local delivery receipts for archived launch evidence and inspect one final acceptance gate before sharing PatchPilot as a complete issue-to-PR demo.

## Why This Matters

PatchPilot can now create, archive, and inspect final launch evidence, but the evidence chain stops at "ready to share." A credible self-hosted demo needs a local proof step showing the package was actually delivered externally, which archive/session it references, and whether that receipt is fresh for the latest archived launch evidence.

## Scope

- Add launch-evidence delivery receipt storage with in-memory and MySQL-backed repositories.
- Add protected API endpoints to create, list, and download launch-evidence delivery receipts.
- Extend the launch evidence share center with receipt-recorded status, latest receipt metadata, freshness, and evidence notes.
- Add a read-only launch evidence finalization gate and Markdown download endpoint.
- Surface receipt recording, receipt history, and finalization status in the launch evidence dashboard panel.
- Repeat the final launch delivery/finalization status in docs and progress logs.

## API Contract

- `POST /api/demo/launch-evidence-share-delivery-receipts`
- `GET /api/demo/launch-evidence-share-delivery-receipts`
- `GET /api/demo/launch-evidence-share-delivery-receipts/{receiptId}/report/download`
- `GET /api/demo/launch-evidence-finalization`
- `GET /api/demo/launch-evidence-finalization/report/download`

Receipt creation is a PatchPilot-local evidence write only. It must not send external messages, create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Validation

- Backend tests first fail for missing receipt/finalization contracts, then pass after implementation.
- Frontend tests first fail because the launch evidence panel cannot record receipts or show finalization status, then pass after UI/API updates.
- Full backend tests, frontend tests, frontend production build, and `git diff --check` must pass before merge.
