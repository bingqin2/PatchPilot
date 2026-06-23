# 132 Issue Context Ingestion

## Goal

Feed real GitHub issue context into PatchPilot's planning workflow and surface the issue summary in task detail views.

This moves PatchPilot closer to a credible issue-to-PR agent: the model should plan from the issue title, issue body, recent discussion, and trigger comment instead of relying only on `/agent fix ...`.

## Scope

- Add a GitHub issue context client that fetches issue title, body, URL, and recent comments through the existing GitHub token configuration.
- Add a service-facing issue context model with a bounded comments list and sanitized nullable fields.
- Load issue context in the task executor after workspace preparation and before patch planning.
- Pass issue context into the patch workflow and fix-plan prompt.
- Audit issue context loading as a tool call so operators can diagnose missing GitHub permissions.
- Add issue context to task detail/report responses for operator inspection.
- Render issue title and issue URL in the dashboard task detail panel.

## Non-Goals

- Do not store full raw issue comments in the database in this slice.
- Do not add embeddings, semantic search, or cross-issue retrieval.
- Do not change `/agent fix` trigger safety, allowlists, rate limits, or model trigger classification.
- Do not expose raw GitHub API errors containing secrets.

## Validation

- `mvn -pl PatchPilot -Dtest=GitHubIssueContextClientTests,IssueContextServiceTests,FixPlanGeneratorTests,WorkspaceFixTaskExecutorTests,TaskControllerTests test`
- `cd frontend && npm test -- TaskDetailPanel.test.tsx App.test.tsx api.test.ts`
- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
