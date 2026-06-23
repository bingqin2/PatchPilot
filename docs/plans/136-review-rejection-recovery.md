# 136 Review Rejection Recovery

## Goal

Make model patch review rejections recoverable and explainable across GitHub comments, reports, metrics, and the dashboard.

This moves PatchPilot toward a credible self-hosted issue-to-PR agent: when the model produces an unsafe, irrelevant, or off-scope patch, operators should see that the review gate blocked the patch intentionally and that retry will request a fresh edit instead of reusing rejected content.

## Scope

- Keep model patch review rejections as `FAILED` tasks so they can use the existing retry flow.
- Classify rejected patch reviews as `PATCH_REVIEW_REJECTION` in failure metrics instead of generic model, auth, or unknown failures.
- Add review gate and recovery guidance to GitHub status comments for failed patch review tasks.
- Add review gate and recovery guidance to markdown task reports.
- Show retry regeneration guidance in the dashboard patch review detail panel.

## Non-Goals

- Do not automatically retry rejected patches.
- Do not reuse rejected generated edits.
- Do not change generated diff human review approval semantics for `PENDING_REVIEW` tasks.
- Do not add a separate patch review queue page.

## Validation

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
