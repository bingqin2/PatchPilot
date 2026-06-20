# Issue Comment Permission Diagnostics Plan

**Goal:** Make GitHub issue comment permission failures actionable during smoke tests and task timeline inspection.

**Scope:** This phase only improves diagnostics for GitHub Issue comment create/update failures. It does not change task execution, retry behavior, token loading, PR creation, or webhook handling.

## Tasks

- [x] Add regression coverage for issue comment creation returning HTTP `403`.
- [x] Add regression coverage for issue comment update returning HTTP `403`.
- [x] Append a clear fine-grained token permission hint to GitHub Issue comment HTTP `403` failures.
- [x] Document the required `Issues: Read and write` token permission and backend restart requirement.
- [x] Record validation evidence in `docs/progress/execution-log.md`.

## Acceptance Criteria

- HTTP `403` from GitHub Issue comment creation includes the required token permission hint.
- HTTP `403` from GitHub Issue comment update includes the same hint.
- Non-`403` failures keep their concise existing messages.
- Existing task execution remains best-effort for issue comments.
