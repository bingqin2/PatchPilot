# Dashboard Configuration Health

## Goal

Turn the read-only dashboard configuration summary into actionable setup health so local demos expose missing or weak configuration without terminal inspection.

## Scope

- Keep using `GET /api/configuration/summary`.
- Add frontend-only health evaluation in `ConfigurationPanel`.
- Show a compact status for missing required secrets.
- Show informational warnings for optional-but-useful settings.
- Keep the panel read-only and avoid exposing secret values.

## Health Rules

Critical setup issues:

- `agentApiKeyConfigured` is false.
- `githubTokenConfigured` is false.
- `githubWebhookSecretConfigured` is false.

Advisory setup issues:

- `modelCostConfigured` is false.
- `queueMaxAttempts` is less than `1`.
- `queueRetryDelayMs` is less than `0`.
- `queueVisibilityTimeoutMs` is less than `1000`.

## Frontend Design

Add a small health summary at the top of `ConfigurationPanel`:

- `Configuration healthy` when no critical or advisory issues exist.
- `{n} setup issue(s)` when critical issues exist.
- `{n} advisory item(s)` when only advisory issues exist.

Render issue rows below the existing configuration cards. Rows should be terse and operational, for example:

- `Agent API key is missing`
- `GitHub token is missing`
- `Webhook secret is missing`
- `Model cost is not configured`

## Testing

- Dashboard render test for a healthy configuration state.
- Dashboard render test for missing secret and advisory states.
- Keep the existing API helper test unchanged because the backend contract does not change.

## Acceptance Criteria

- Missing required secrets are visible without opening task failure logs.
- Optional advisories do not block the healthy secret state.
- No raw secret values appear in the UI.
