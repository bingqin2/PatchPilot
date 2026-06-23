# Adapter Demo Repositories

These directories are minimal repository fixtures for PatchPilot language-adapter detection. They document the supported repository shapes and are used by backend tests to verify that each adapter selects the expected `language`, `buildSystem`, and allowlisted verification command.

The fixtures are intentionally small. They are not replacement sample applications, and the backend test suite does not execute their verification commands. Use them to inspect adapter detection behavior and to create real external smoke repositories when needed.

| Fixture | Adapter | Verification command |
| --- | --- | --- |
| `java-maven` | Java/Maven | `mvn test` |
| `java-gradle` | Java/Gradle | `gradle test` |
| `go-module` | Go | `go test ./...` |
| `node-npm` | Node/npm | `npm test` |
| `node-pnpm` | Node/pnpm | `pnpm test` |
| `node-yarn` | Node/yarn | `yarn test` |
| `node-bun` | Node/Bun | `bun test` |
| `python-tox` | Python/tox | `tox` |
| `python-nox` | Python/nox | `nox` |
| `python-hatch` | Python/Hatch | `hatch test` |
| `python-pytest` | Python/pytest | `python3 -m pytest` |
| `python-poetry` | Python/Poetry | `poetry run pytest` |
| `python-uv` | Python/uv | `uv run pytest` |
