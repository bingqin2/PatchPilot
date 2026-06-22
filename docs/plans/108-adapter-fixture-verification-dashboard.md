# 108 Adapter Fixture Verification Dashboard

## Goal

Make PatchPilot's supported adapter matrix self-verifying from the dashboard. Operators should be able to see that each checked-in demo fixture is detected by the expected language adapter without running a terminal smoke command.

## Scope

- Add `GET /api/language-adapters/fixtures`.
- Verify each supported adapter's `docs/demo-repositories/*` fixture using the real `LanguageAdapterRegistry`.
- Return fixture name, fixture path, expected language/build system/command, actual language/build system/command, detection reason, and pass/fail status.
- Keep failed or missing fixtures visible in the API response instead of throwing.
- Copy `docs/demo-repositories` into the backend Docker runtime image so Docker Compose can serve fixture verification results.
- Add a dashboard panel that shows fixture verification status next to the supported adapter matrix.
- Keep fixture verification API failures local to that panel so the rest of the dashboard can still load.
- Update README, frontend design notes, adapter smoke checklist, and execution log.

## Non-Goals

- Running each fixture's actual test command.
- Cloning GitHub repositories.
- Calling the model.
- Creating tasks, branches, commits, pushes, Pull Requests, or issue comments.
- Allowing arbitrary fixture paths from user input.

## Validation

- Backend service and controller tests for fixture verification output.
- Docker runtime packaging test for copied demo fixtures.
- Frontend API and panel tests.
- Dashboard integration test showing fixture verification results.
- Full backend test suite, frontend test suite, frontend production build, and whitespace check before handoff.
