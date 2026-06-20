# GitHub Smoke Run After Storage Fix Plan

**Goal:** Verify the MySQL-backed IDEA/local workflow after expanding test-run output storage.

**Scope:** This phase is documentation-only unless the smoke run exposes a new backend defect. It records a real GitHub webhook run through the Cloudflare temporary URL, local IDEA backend on port `18080`, Docker MySQL on `3307`, Maven verification, branch push, and Pull Request creation.

## Tasks

- [x] Start the IDEA/local backend with the `idea` profile.
- [x] Restart Cloudflare Tunnel against `http://127.0.0.1:18080`.
- [x] Update the GitHub webhook temporary URL.
- [x] Trigger `/agent fix replace docs/demo.md PatchPilot storage smoke test`.
- [x] Verify task detail, queue summary, test-runs, and timeline APIs.
- [x] Record smoke-run evidence in `docs/progress/execution-log.md`.

## Acceptance Criteria

- Webhook delivery creates a task.
- Task completes successfully.
- Test-run output is persisted and readable from the API.
- Pull Request is created on GitHub.
- Any non-blocking follow-up is documented.
