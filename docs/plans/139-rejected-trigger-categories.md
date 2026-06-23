# 139 Rejected Trigger Categories

## Goal

Make refused `/agent fix` attempts easier to diagnose by storing a stable rejection category alongside the existing human-readable reason.

This moves PatchPilot closer to a credible self-hosted issue-to-PR agent: vague comments, malicious prompts, unauthorized users, blocked repositories, rate limits, and model classifier refusals should be distinguishable without parsing reason text.

## Scope

- Add stable rejected-trigger category constants for deterministic safety, allowlist, rate-limit, and model-classification refusals.
- Return a category from `CommandSafetyGate`, `TriggerRateLimitDecision`, and `TriggerIntentDecision`.
- Persist rejected-trigger categories in in-memory and MySQL-backed audit storage.
- Add a Flyway migration for the MySQL `rejected_trigger_audit.category` column and category/created index.
- Include `category` in `GET /api/rejected-triggers` responses.
- Render the category as a dashboard badge in `RejectedTriggerPanel`.

## Non-Goals

- Do not change which requests are accepted or rejected.
- Do not replace human-readable rejection reasons.
- Do not add category filtering to the API or dashboard yet.
- Do not retry rejected triggers differently based on category.

## Validation

- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests test`
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
