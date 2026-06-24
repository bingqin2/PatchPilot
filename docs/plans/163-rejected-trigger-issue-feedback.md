# 163 Rejected Trigger Issue Feedback

## Goal

Make rejected GitHub `/agent fix` comments understandable to the issue author. When PatchPilot refuses a trigger before task creation, the GitHub issue should receive a safe refusal comment with the rejection category, reason, and next action.

## Scope

- Extend rejected issue comments with the stable rejection category.
- Add category-specific next-action guidance for vague, dangerous, unauthorized, rate-limited, quarantined, and model-classifier failures.
- Keep the original trigger body out of refusal comments so dangerous or secret-seeking text is not echoed back.
- Store the refusal comment id and URL in rejected-trigger audit records when comment creation succeeds.
- Preserve rejection behavior when GitHub comment creation fails: do not create a task, and still record the rejected-trigger audit without a comment link.

## Out of Scope

- No new dashboard panel or API endpoint.
- No change to safety-gate ordering, rate limits, quarantine behavior, or model classification decisions.
- No retry or edit lifecycle for refusal comments.

## Verification

- Add a failing `IssueCommentToolTests` case for category and next-action refusal copy.
- Add a failing null-category fallback test to prevent refusal-comment crashes.
- Add webhook service coverage that proves the rejection category is passed to the issue-comment tool and stored in audit records.
- Run focused backend tests, then the full backend suite before handoff.
