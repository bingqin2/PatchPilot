# 107 Python Advanced Runner Adapters

## Goal

Add first-class Python runner support for tox, nox, and hatch projects. PatchPilot should detect these explicit project test entrypoints before broader Python adapters, run only fixed allowlisted verification commands, expose the support in the adapters API/dashboard, and keep the behavior demonstrable through fixtures.

## Scope

- Add Python/tox detection for `tox.ini` or `[tool.tox]` in `pyproject.toml`, with fixed command `tox`.
- Add Python/nox detection for `noxfile.py`, with fixed command `nox`.
- Add Python/hatch detection for `[tool.hatch.envs.default.scripts]` or `[tool.hatch.envs.test.scripts]` in `pyproject.toml`, with fixed command `hatch test`.
- Prefer tox, nox, and hatch before Poetry, uv, and plain pytest when their runner signals are present.
- Extend the command allowlist only for `tox`, `nox`, and `hatch test`.
- Install tox, nox, and hatch in the backend runtime image used by Docker Compose.
- Add fixtures under `docs/demo-repositories/python-tox`, `python-nox`, and `python-hatch`.
- Add the adapters to `GET /api/language-adapters` and dashboard test data.
- Update README, product docs, backend command standard, frontend design notes, and execution log.

## Non-Goals

- Running arbitrary tox environments, nox sessions, or hatch script names.
- Installing project dependencies outside the runner's normal fixed test command.
- Adding generic Python command execution.
- Supporting every Python build backend.

## Validation

- Adapter unit tests for positive and rejected detection cases.
- Registry and Spring registration tests proving runner adapters are preferred before broader Python adapters.
- Command allowlist tests proving only fixed commands are accepted.
- Runtime packaging tests proving the Docker image contains the required runner tools.
- Catalog/controller/frontend tests proving the dashboard exposes the new support.
- Full backend test suite, frontend test suite, frontend production build, and whitespace check before handoff.
