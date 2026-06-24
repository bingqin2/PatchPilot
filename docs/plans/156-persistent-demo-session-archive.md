# Plan 156: Persistent Demo Session Archive

## Goal

Persist demo session archives for database-backed local runs so a backend restart does not erase the operator's latest copyable demo reports.

## Backend Scope

- Add Flyway migration `V25__create_demo_session_archive.sql`.
- Add `DemoSessionArchiveEntity`, `DemoSessionArchiveMapper`, and conversion helpers.
- Introduce `DemoSessionArchiveRepository` as the storage boundary used by `DemoSessionArchiveService`.
- Keep `InMemoryDemoSessionArchiveRepository` for the default database-free profile.
- Add `MyBatisDemoSessionArchiveRepository` for `local`, `docker`, and `idea` profiles.
- Keep the API contract unchanged: `POST /api/demo/session-archives` creates an archive and `GET /api/demo/session-archives` lists recent archives.
- Keep archive creation local to PatchPilot state: no task creation, model call, test execution, Git mutation, GitHub comment, or Pull Request write.

## Frontend Scope

- No UI contract change is required.
- Re-run the dashboard archive/API tests to prove the existing frontend continues to work with the persistent backend storage boundary.

## Validation

- Migration test for the new archive table and indexes.
- In-memory repository test for newest-first ordering and capping.
- MyBatis repository tests for insert and newest-first list conversion.
- Existing archive service and controller tests.
- Frontend archive/API/App tests.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
