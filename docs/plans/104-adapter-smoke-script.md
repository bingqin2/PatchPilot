# 104 Adapter Smoke Script

## Goal

Provide a safe local command that demonstrates supported adapter detection using the checked-in demo fixtures. The script should be useful for demos and quick regressions without requiring GitHub, model credentials, Docker, or real Pull Request creation.

## Scope

- Add `scripts/adapter-smoke.sh`.
- Print the supported fixture-to-adapter matrix.
- Run the fast `LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures` test by default.
- Add a `--backend` mode for wider adapter and command-guard coverage.
- Add `docs/agent/adapter-smoke-checklist.md`.
- Add a backend test that verifies the script exists, points at the intended tests, mentions fixtures, and avoids risky commands.
- Update README and execution log.

## Non-Goals

- Do not start Docker Compose.
- Do not call GitHub, webhooks, or the model provider.
- Do not clone repositories, push branches, create commits, or open Pull Requests.
- Do not execute every fixture's real build command in this script.

## Validation

- Verify the script safety test fails before the script exists.
- Verify the script safety test passes after implementation.
- Run `bash scripts/adapter-smoke.sh`.
- Run the full backend test suite.
- Run `git diff --check`.
