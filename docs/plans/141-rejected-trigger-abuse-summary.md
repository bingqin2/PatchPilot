# 141 Rejected Trigger Abuse Summary

## Goal

Show operators the recent rejection pattern behind refused `/agent fix` attempts, not just the raw audit rows.

This moves PatchPilot closer to the final self-hosted issue-to-PR target because operators can quickly tell whether rejected activity is mostly vague requests, dangerous instructions, unauthorized users, blocked repositories, rate limits, or model-classifier refusals.

## Scope

- Add `GET /api/rejected-triggers/summary?limit=...`.
- Summarize recent rejected-trigger audits by stable category, source, trigger user, and repository.
- Keep the summary read-only and based on existing rejected-trigger audit records.
- Add typed frontend API support for the summary endpoint.
- Render a compact dashboard summary above rejected-trigger rows.
- Let category summary buttons apply the existing URL-backed rejected-category filter.
- Update README, architecture, frontend design, and execution logs.

## Non-Goals

- Do not add a new database table.
- Do not add server-side time-window filters.
- Do not change rejection decisions, retry behavior, or rate-limit behavior.
- Do not expose raw secrets, payload bodies, or webhook signatures.

## Validation

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
