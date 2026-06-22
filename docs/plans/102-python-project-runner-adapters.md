# 102 Python Project Runner Adapters

## Goal

Support Python repositories that use Poetry or uv while preserving PatchPilot's explicit language-adapter boundary. A repository must expose a clear pytest signal before PatchPilot runs any Python project-manager command.

## Scope

- Add `PythonPoetryLanguageAdapter` for `pyproject.toml` with `[tool.poetry]`.
- Add `PythonUvLanguageAdapter` for repositories with `uv.lock`.
- Require pytest configuration or dependency signals for both adapters.
- Return fixed verification commands: `poetry run pytest` and `uv run pytest`.
- Prefer Poetry and uv adapters before the broad Python/pytest adapter.
- Extend `CommandExecutionGuard` to allow only fixed Poetry and uv pytest commands.
- Install Poetry and uv in the backend runtime image.
- Update README, product docs, decision log, and execution log.

## Non-Goals

- Do not support tox, nox, pipenv, hatch, or custom runner commands in this step.
- Do not run install, sync, lock, or dependency-management commands.
- Do not support arbitrary pytest arguments selected by users or the model.
- Do not introduce a TOML parser dependency unless detection becomes too broad for string-based signals.

## Validation

- Verify Poetry and uv adapters detect pytest-backed repositories.
- Verify missing runner signals or missing pytest signals are rejected.
- Verify command guard allows only `poetry run pytest` and `uv run pytest`.
- Verify Spring registers Poetry and uv adapters and prefers Poetry before plain pytest.
- Verify the runtime Dockerfile installs Poetry and uv.
- Run focused adapter/runner tests and the full backend test suite before handoff.
