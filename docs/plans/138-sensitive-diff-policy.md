# 138 Sensitive Diff Policy

## Goal

Keep generated patch safety rules consistent across planning, diff review, configuration visibility, and dashboard readiness.

This moves PatchPilot closer to a credible self-hosted issue-to-PR agent: operators should know which generated changes are blocked before tests, commits, pushes, or Pull Request creation, and protected paths should not depend on duplicated hardcoded lists.

## Scope

- Add a shared generated-diff safety policy for sensitive paths, secret-like added lines, binary diffs, and broad patch thresholds.
- Use the shared policy in `GeneratedDiffRiskGate`.
- Use the same policy in planned patch workflow validation before model edits or direct replacement instructions can write sensitive files.
- Extend protected path coverage for Git metadata and package-manager credential files such as `.npmrc`, `.pypirc`, `.netrc`, and Maven `settings.xml`.
- Expose non-sensitive policy state through `GET /api/configuration/summary`.
- Render generated-diff policy state in the dashboard configuration panel with setup advisories when the policy is disabled or empty.

## Non-Goals

- Do not add runtime editing for the generated-diff policy.
- Do not replace pending-review approval semantics.
- Do not allow operators to approve direct writes to protected paths before diff review.
- Do not expose secret values or raw policy internals through the configuration API.

## Validation

- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests test`
- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx src/api.test.ts src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
