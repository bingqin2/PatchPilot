# 240 Demo Handoff Share Instructions

## Goal

Give operators one final read-only sharing guide after the handoff share center is ready. The guide should say who to send the package to, which artifacts to attach, which checks to perform before sending, and provide a copyable message template.

## Problem

Plans 238 and 239 made the final send/no-send state visible, but operators still had to manually turn that state into a handoff message. This creates room for missing attachments or sending a package before the share center is ready.

## Scope

- Add a backend `DemoHandoffShareInstructionsVo` read model derived from the existing handoff share center.
- Expose `GET /api/demo/handoff-share-instructions` and a Markdown download endpoint.
- Include recommended recipients, required attachments, pre-send checks, subject, body, generated time, and a read-only side-effect contract.
- Render the instructions in the dashboard session snapshot with copy and download actions.
- Refresh instructions after archiving a new handoff package.
- Update README, product spec, architecture, frontend design, and the execution log.

## Out of Scope

- Sending emails, GitHub comments, Slack messages, or any external notification.
- Persisting delivery recipients.
- Creating new archives or mutating GitHub from the instructions endpoint.

## Validation

- Backend RED: focused tests fail because share instructions do not exist.
- Frontend RED: API and session snapshot tests fail because instructions are not loaded or rendered.
- GREEN: focused backend and frontend tests pass.
- Final verification: full backend tests, full frontend tests, frontend build, and `git diff --check`.
