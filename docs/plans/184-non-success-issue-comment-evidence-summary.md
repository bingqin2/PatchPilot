# 184 Non-Success Issue Comment Evidence Summary

## Goal

Make GitHub issue status comments useful when PatchPilot does not produce a Pull Request. A failed or pending-review task should explain what stopped, what the user should do next, and which repository adapter evidence PatchPilot had already selected.

## Scope

- Add adapter evidence to `FAILED` issue comments when language, build system, verification command, or detection reason is available.
- Add the same evidence to `PENDING_REVIEW` issue comments so authors can distinguish a risk-gate pause from a generic failure.
- State that selected verification commands come from repository adapter allowlists and not arbitrary issue comment text.
- Preserve existing unsupported-repository guidance, failure categories, next actions, and sanitized failure reasons.
- Add worker coverage to ensure non-success status comment updates receive task adapter metadata.

## Out of Scope

- New dashboard panels or API fields.
- Changing retry, approval, or generated-diff risk behavior.
- Showing full test-run output in issue comments.

## Validation

- Focused comment-tool tests must fail before implementation because non-success comments lack adapter evidence.
- Worker tests must confirm failed and pending-review comment updates receive adapter metadata.
- Full backend and frontend regression checks must pass before handoff.
