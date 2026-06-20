# 050 Dashboard Issue Links

## Goal

Add GitHub Issue links to the React dashboard so operators can jump from a PatchPilot task back to the issue that triggered it.

## Scope

- Build issue URLs from existing task fields:
  - `repositoryOwner`
  - `repositoryName`
  - `issueNumber`
- Show `Open Issue` links in task rows.
- Show `Open Issue` in the selected task detail header.
- Keep existing Pull Request links unchanged.
- Do not change backend APIs or persisted task data.

## Tasks

1. Add frontend test coverage for generated GitHub Issue links.
2. Add a small issue URL helper in the dashboard component.
3. Render `Open Issue` links in the task list and task detail header.
4. Document dashboard issue links and record validation evidence.

## Acceptance Criteria

- Every visible task row includes an `Open Issue` link.
- The selected task detail header includes an `Open Issue` link.
- Issue links use `https://github.com/{owner}/{repo}/issues/{issueNumber}`.
- Existing PR links continue to work.
- `npm test` and `npm run build` pass under `frontend/`.
