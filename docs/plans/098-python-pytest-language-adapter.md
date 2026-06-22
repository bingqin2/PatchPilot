# Python Pytest Language Adapter

## Goal

Support Python repositories that clearly use pytest. These repositories should pass language preflight, run a fixed allowlisted `python3 -m pytest` verification command, and continue through the existing issue-to-PR workflow.

## Scope

- Add `PythonPytestLanguageAdapter`.
- Detect pytest through `pytest.ini`, `[tool.pytest.ini_options]` in `pyproject.toml`, or a pytest dependency in `requirements.txt`.
- Return the fixed verification command `python3 -m pytest`.
- Register the Python/pytest adapter after Java/Maven, Java/Gradle, and Node/npm adapters.
- Extend `CommandExecutionGuard` to allow only `python3 -m pytest`.
- Install `python3` and `python3-pytest` in the backend runtime image for Docker Compose execution.
- Update tests, supported-repository documentation, command allowlist documentation, decisions, and progress logs.

## Non-Goals

- Support Poetry, Pipenv, tox, nox, uv, or custom Python runners in this step.
- Run arbitrary Python commands or user/model-selected pytest arguments.
- Install repository dependencies automatically before verification.
- Add adapter-aware dashboard labels.

## Validation Plan

- Verify pytest configuration and dependency detection.
- Verify Python repositories without pytest signals are unsupported.
- Verify the command guard allows `python3 -m pytest` and still rejects arbitrary commands.
- Verify the generic verification runner can execute the fixed Python command.
- Verify Spring registers the Python/pytest adapter.
- Verify the runtime Dockerfile includes `python3` and `python3-pytest`.
- Run full backend tests and whitespace checks before merging.
