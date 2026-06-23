# 140 Rejected Trigger Category Filtering

## Goal

Let operators filter rejected `/agent fix` audit records by the stable rejection category introduced in plan 139.

This moves PatchPilot closer to a credible self-hosted issue-to-PR agent because malicious, vague, unauthorized, rate-limited, and model-rejected attempts can be isolated quickly during demo and production troubleshooting.

## Scope

- Add optional `category` filtering to `GET /api/rejected-triggers`.
- Support the filter in both in-memory and MyBatis-backed rejected-trigger audit services.
- Keep existing `limit` behavior and retry behavior unchanged.
- Add a dashboard category select for rejected triggers.
- Persist rejected-trigger category filter state in the URL as `rejectedCategory`.
- Update frontend API helpers, tests, and product docs.

## Non-Goals

- Do not add category count summaries.
- Do not add multi-select category filtering.
- Do not change rejection decisions or retry behavior.
- Do not add dashboard charts for rejected triggers.

## Validation

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
