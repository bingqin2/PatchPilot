# 324 Live Trigger Launch Package

## Goal

Create one final read-only launch package before an operator posts a real `/agent fix` GitHub issue comment.

This package should combine the existing live launch gate with the latest external exposure operator handoff checklist archive, so the operator can see whether the exact issue/comment is safe to post, what evidence was frozen, and what report to keep for handoff.

## Scope

- Add a backend service and controller endpoint for a live trigger launch package.
- Reuse the existing live launch gate input contract: repository owner/name, issue number, trigger user, and exact trigger comment.
- Include latest operator handoff archive evidence and block launch when it is missing or not ready.
- Return copyable issue/comment details, evidence notes, next actions, side-effect contract, and Markdown report.
- Add typed frontend API helpers and dashboard actions to create and download the package.
- Update product, architecture, frontend, README, and execution log documentation.

## Safety Contract

The launch package is read-only. It must not create tasks, enqueue work, call the model directly, mutate Git, push branches, open Pull Requests, write GitHub comments, archive new records, or expose secrets.

## Validation Checklist

- [x] Backend RED test proves launch package service/controller are missing.
- [x] Frontend RED test proves launch package API/panel behavior is missing.
- [x] Focused backend tests pass.
- [x] Focused frontend tests pass.
- [x] Full backend tests pass.
- [x] Full frontend tests pass.
- [x] Frontend production build passes.
- [x] Diff check and secret scan pass.
