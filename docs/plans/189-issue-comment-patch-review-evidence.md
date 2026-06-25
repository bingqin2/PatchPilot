# 189 Issue Comment Patch Review Evidence

## Goal

Expose the latest model patch-review evidence in GitHub issue status comments, not only in generated Pull Request bodies.

## Why

Maintainers and issue authors often read the issue thread before opening a Pull Request. Successful and failed task comments should show why PatchPilot trusted or rejected the generated patch without exposing raw prompts, model responses, or diffs.

## Scope

- Add issue-comment rendering for latest patch-review decision, reason, confidence, required follow-up, edited files, and review time.
- Include approval evidence on completed task comments when a patch review exists.
- Include rejection evidence on failed task comments when the model patch-review gate rejects generated edits.
- Reuse the same GitHub-facing formatter for Pull Request bodies and issue comments.
- Wire `FixTaskWorker` to load the latest patch-review record before completed and failed status comment updates.

## Out of Scope

- No new model calls.
- No raw diff, prompt, or completion content in GitHub comments.
- No change to the patch-review decision policy.
- No frontend changes.

## Verification

- Add failing tests for completed and failed issue comments.
- Add worker tests proving latest patch-review records are passed to issue comments.
- Run focused backend tests and full backend regression tests.
