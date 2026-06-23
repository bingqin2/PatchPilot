# 119 Dashboard Admin Token Prompt

## Goal

Make the dashboard recover from protected operator APIs without requiring browser DevTools. This completes the admin-token guard UX for temporary Cloudflare URL demos and local operator sessions.

## Scope

- Detect the backend `Admin token is required` API error in the dashboard alert.
- Show an inline password input labeled `Admin API token`.
- Save the submitted token to browser `localStorage` under `patchpilot.adminToken`.
- Reuse the existing frontend API helper so later requests send `X-PatchPilot-Admin-Token`.
- Retry dashboard loading immediately after saving the token.
- Document the prompt and the optional console prefill path.

## Verification

- Add App-level coverage for the 401 recovery flow.
- Run focused frontend coverage for the new prompt.
- Run the full frontend test suite.
- Run the production frontend build.
