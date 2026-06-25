# 197 GitHub Credential Readiness

## Goal

Expose a non-mutating GitHub credential readiness check so operators can verify `PATCHPILOT_GITHUB_TOKEN` before starting a live `/agent fix` issue-to-PR run.

## Scope

- Add an admin-protected backend endpoint at `GET /api/github/credential-readiness`.
- Probe GitHub with a read-only `GET https://api.github.com/user` request using the configured token.
- Return only non-sensitive readiness fields: configured flag, status, message, latency, checked time, and operator action.
- Add a `GitHub credentials` check to demo readiness and block live-demo readiness when GitHub rejects the token.
- Show GitHub credential readiness in the dashboard operator setup checklist.

## Non-Goals

- Do not create tasks, comments, commits, pushes, Pull Requests, or model-call audits.
- Do not expose the raw GitHub token, authenticated user details, or repository contents.
- Do not replace the existing GitHub write-path error handling; this is a preflight signal.

## Validation

- Backend focused tests cover missing token, successful probe, rejected token, non-sensitive controller output, admin-token protection, HTTP probe headers, and demo readiness aggregation.
- Frontend focused tests cover the new API helper, operator setup checklist display, and App-level loading.
