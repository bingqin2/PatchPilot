# 089 Authorized Trigger Policy

## Goal

Prevent unauthorized users or repositories from creating PatchPilot tasks before any queue, workspace, model, or GitHub write operation starts.

## Scope

- Add optional safety allowlists for trigger users and repositories.
- Apply the same policy to GitHub webhook comments and dashboard/manual task creation.
- Preserve the existing open local-dev behavior when allowlists are empty.
- Document operator configuration and validation.

## Design

`CommandSafetyGate` now evaluates a `SafetyGateRequest` containing repository owner, repository name, trigger user, and trigger comment. It still rejects unsupported or dangerous `/agent fix` content, then applies optional allowlists:

- `patchpilot.safety.allowed-trigger-users`
- `patchpilot.safety.allowed-repositories`

Empty allowlists mean unrestricted local-dev behavior. Non-empty allowlists are case-insensitive and reject unmatched requests before task creation.

## Validation

- Unit-test the safety gate allowlist behavior directly.
- Service-test webhook rejection before task creation.
- Service-test manual task rejection before task creation.
- Controller-test the manual task API using Spring property binding.
- Run the backend test suite before handoff.
