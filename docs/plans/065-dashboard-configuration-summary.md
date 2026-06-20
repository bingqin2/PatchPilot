# Dashboard Configuration Summary

## Goal

Show non-sensitive runtime configuration in the operations dashboard so local demos and environment debugging do not require terminal inspection.

## Scope

- Add a backend endpoint at `GET /api/configuration/summary`.
- Return only non-secret values and boolean secret/configuration status.
- Add a compact frontend dashboard panel for configuration.
- Keep the feature read-only.

## Backend Design

Add `ConfigurationSummaryVo` with:

- `agentProvider`
- `agentModel`
- `agentBaseUrl`
- `agentApiKeyConfigured`
- `githubTokenConfigured`
- `githubWebhookSecretConfigured`
- `workspaceRootDir`
- `queueMaxAttempts`
- `queueRetryDelayMs`
- `queueVisibilityTimeoutMs`
- `modelCostConfigured`

Add a small `ConfigurationController` under a new `configuration` package. It should read `AgentProperties`, `GitHubProperties`, `WorkspaceProperties`, `TaskQueueProperties`, and the `patchpilot.github.webhook-secret` value. It must not return token, API key, or webhook secret contents.

## Frontend Design

Add `getConfigurationSummary()` in `frontend/src/api.ts`, a matching TypeScript type, and `ConfigurationPanel`. Render the panel near queue/status observability so it is visible during local setup checks.

## Testing

- Controller test for `GET /api/configuration/summary`.
- Assert secrets are represented only as booleans.
- Frontend API helper test for the new endpoint.
- Dashboard render test for `Configuration`, provider/model, workspace root, and configured/missing secret states.

## Acceptance Criteria

- The API never returns raw API key, GitHub token, or webhook secret.
- Missing secrets render as `Missing`.
- Present secrets render as `Configured`.
- Existing dashboard refresh flow loads the new endpoint.
