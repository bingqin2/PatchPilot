# 271 Handoff Certificate Share Gate

## Goal

Make the final demo handoff share path enforce task evidence acceptance certificate readiness. The session report already shows task certificate proof; the share center, share instructions, and finalization gate should also refuse to treat a handoff as send-ready/finalized until the task certificate is archived, certified, and linked to task evidence.

## Scope

- Reuse the latest task evidence acceptance certificate archive as a structured handoff share signal.
- Add certificate status, archive id, target task, Pull Request, next action, and download actions to the handoff share center API and Markdown report.
- Block handoff share readiness when the certificate is missing, not certified, or blocked.
- Include the certificate archive in handoff share instructions attachments and pre-send checks.
- Add a task certificate check and evidence note to handoff finalization.
- Render the certificate gate in the dashboard session share-center/finalization panels.

## Out of Scope

- Creating new certificate persistence tables.
- Changing task certificate archive creation rules.
- Sending handoff evidence externally.
- Parsing archived handoff Markdown to infer certificate state.

## Validation

- RED backend tests first for share center and finalization certificate gate behavior.
- RED frontend test first for share-center/finalization certificate rendering.
- Focused backend and frontend tests after implementation.
- Full backend and frontend regression, production build, and whitespace check before merge.
