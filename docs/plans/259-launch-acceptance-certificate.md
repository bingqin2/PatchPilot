# Launch Acceptance Certificate

## Goal

Create one final read-only launch acceptance certificate from the latest archived launch acceptance closeout. This gives a self-hosted PatchPilot operator a single shareable endpoint and dashboard card that says whether the demo is certified for external review.

## Scope

- Add a backend certificate read model and service derived from the latest closeout archive.
- Add `GET /api/demo/launch-acceptance-certificate` and Markdown download support.
- Require the latest closeout archive to be `READY` and accepted before the certificate reports certified.
- Surface the certificate in the launch evidence dashboard panel with archive, session, Pull Request, receipt, generated time, next action, and download controls.
- Update docs and progress logs with validation evidence.

## Non-Goals

- Do not create tasks, call the model, run tests, mutate Git, send GitHub comments, open Pull Requests, or send external messages.
- Do not replace existing closeout archive endpoints; the certificate is the final read-only summary over those archives.

## Validation

- Backend tests first fail because the certificate service and controller endpoints do not exist.
- Frontend tests first fail because the API helper, type, App wiring, and dashboard card do not exist.
- Full backend tests, frontend tests, frontend build, and whitespace checks pass before merge.
