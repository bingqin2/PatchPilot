# 187 GitHub Feedback Risk Review Evidence

## Goal

Make GitHub-facing feedback show when a task resumed after generated-diff risk review approval. Maintainers should be able to read the Pull Request or final issue status comment and see who approved the review, when it was approved, and why the task was allowed to continue.

## Scope

- Add risk-review approval evidence to generated Pull Request bodies when approval metadata exists.
- Add the same approval evidence to completed issue status comments when approval metadata exists.
- Keep GitHub feedback unchanged for tasks that did not pass through human risk-review approval.
- Reuse one formatter so PR and issue-comment wording stay consistent.

## Out of Scope

- No change to approval authorization, retry behavior, queue behavior, or generated-diff risk-gate decisions.
- No frontend changes. The dashboard already shows approval metadata in task detail.
- No new database fields. Existing `riskReviewApprovedAt`, `riskReviewApprovedBy`, and `riskReviewApprovalReason` fields are used.

## Verification

- Focused backend tests for Pull Request body and issue comment formatting.
- Full backend regression verification before handoff.
- Frontend tests are not required for this backend-only GitHub feedback slice.
