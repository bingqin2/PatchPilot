# 179 Repository Preflight Evidence Report

## Goal

Make local repository preflight results exportable as one Markdown evidence report. Operators should be able to prove whether a backend-local path is supported, which adapter would run, which verification command is allowlisted, and what must change before posting a real `/agent fix` comment.

## Scope

- Add a dashboard action to copy a repository preflight report after a preflight result exists.
- Include supported/unsupported status, repository path, selected adapter, verification command, detection reason, operator action, configured allowed roots, and any current API error.
- Include supported adapter options when the repository is unsupported.
- Keep the existing `POST /api/repository-preflight` contract unchanged.
- Do not create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Frontend Design

- `RepositoryPreflightPanel` continues to own the local path form and result rendering.
- The copy action appears only after the operator has a result to export.
- The Markdown report mirrors the visible evidence rather than adding another backend endpoint.
- Unsupported repository reports include adapter options so operators can decide whether to add a project marker, add a new adapter, or avoid triggering a task.

## Validation

- Component tests cover supported and unsupported report exports.
- App integration tests cover running preflight through the dashboard and copying the resulting Markdown.
- Full frontend tests and production build should pass.
- Backend tests should continue to pass because this feature reuses the existing read-only preflight API.
