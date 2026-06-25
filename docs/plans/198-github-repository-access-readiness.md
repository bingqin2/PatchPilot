# 198 GitHub Repository Access Readiness

## Goal

Expose a non-mutating GitHub repository access readiness check so operators can verify that `PATCHPILOT_GITHUB_TOKEN` can read the target demo repository before starting a live `/agent fix` run.

## Scope

- Add an admin-protected backend endpoint at `GET /api/github/repository-access-readiness`.
- Accept `owner` and `repository` query parameters and trim them before probing GitHub.
- Probe GitHub with a read-only `GET https://api.github.com/repos/{owner}/{repository}` request using the configured token.
- Return only non-sensitive readiness fields: token configured flag, repository configured flag, repository full name, status, message, default branch, latency, checked time, and operator action.
- Add a `Repository access` check to the dashboard operator setup checklist.
- Let the dashboard use repository owner/name filters first, then the first loaded task's repository, to choose the repository access probe target.

## Non-Goals

- Do not create tasks, comments, commits, pushes, Pull Requests, queue rows, or model-call audits.
- Do not expose the raw GitHub token, repository contents, GitHub response body, or account details.
- Do not replace task execution GitHub error handling; this is a pre-demo readiness signal.
- Do not make demo readiness require a repository target, because the readiness endpoint has no selected repository context.

## Validation

- Backend focused tests cover missing repository input, missing token, successful repository access, rejected repository access, non-sensitive controller output, admin-token protection, HTTP probe headers, URL encoding, and default-branch parsing.
- Frontend focused tests cover the new API helper, operator setup checklist display, failed repository-access guidance, and App-level loading.
