# 147 Repository Preflight Diagnostics

## Goal

Add an operator-facing repository preflight diagnostic that uses the real language adapter registry before a live `/agent fix` run. The diagnostic should prove whether a local fixture or workspace path is supported without creating a task, calling the model, running tests, mutating Git, or opening a Pull Request.

## Backend Scope

- Add `POST /api/repository-preflight`.
- Accept `repositoryPath` as a backend-local path.
- Resolve relative paths from the current backend working directory, with a parent-directory fallback for Maven module execution.
- Return supported status, language, build system, verification command, detection reason, operator action, and supported adapter catalog for unsupported results.
- Reject blank paths with `400`.

## Frontend Scope

- Add a `RepositoryPreflightPanel` to the dashboard near the supported adapter panels.
- Let operators enter a local repository path and run the preflight manually.
- Show supported/unsupported status, selected adapter metadata, command, reason, next action, and adapter options for unsupported results.

## Validation

- Backend service and controller tests cover supported detection, unsupported detection, missing directory, and blank path validation.
- Frontend API, component, and App-level tests cover POST behavior and dashboard wiring.
- Full backend and frontend verification should run before merge.
