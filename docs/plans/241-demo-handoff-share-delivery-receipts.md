# 241 Demo Handoff Share Delivery Receipts

## Goal

Close the post-demo handoff loop by letting operators record local evidence that the prepared handoff package was delivered outside PatchPilot.

## Problem

Plan 240 produced final share instructions, but PatchPilot still had no durable record that an operator actually sent those instructions through email, chat, or another external channel. Without a receipt, post-demo review depends on memory or screenshots outside the dashboard.

## Scope

- Add a persistent `DemoHandoffShareDeliveryReceiptVo` read model with delivery channel, target, operator, notes, delivered time, source archive/session ids, and Markdown evidence.
- Expose `POST /api/demo/handoff-share-delivery-receipts`, `GET /api/demo/handoff-share-delivery-receipts`, and a Markdown report download endpoint.
- Reject receipt creation until the current handoff share instructions are send-ready.
- Record protected admin audit evidence when a receipt is created.
- Render recent delivery receipts in the demo session snapshot, with a small form for recording local delivery evidence and a download action per receipt.
- Update README, product spec, architecture, frontend design, and the execution log.

## Out of Scope

- Sending email, Slack, GitHub comments, or any external notification.
- Verifying external delivery status.
- Creating tasks, calling the model, running tests, mutating Git, or writing to GitHub from the receipt endpoint.

## Validation

- Backend RED: focused tests fail because receipt VO, service, repository, mapper, migration, and endpoints do not exist.
- Frontend RED: API and session snapshot tests fail because receipt helpers, form, list, and download action do not exist.
- GREEN: focused backend and frontend tests pass after implementation.
- Final verification: full backend tests, full frontend tests, frontend production build, and `git diff --check`.
