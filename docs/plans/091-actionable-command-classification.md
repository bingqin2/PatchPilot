# 091 Actionable Command Classification

## Goal

Prevent PatchPilot from creating work for vague or nonsensical `/agent fix` comments. This moves the safety boundary closer to the product goal: a self-hosted issue-to-PR agent should only spend queue, clone, model, and GitHub resources on requests that contain a concrete maintenance task.

## Scope

- Classify `/agent fix` instructions before task creation.
- Accept deterministic actionable signals:
  - supported patch operations such as `touch <path>` and `replace <path> <content>`
  - likely file paths or file names
  - concrete failure descriptions containing build, test, error, exception, timeout, or crash signals
- Reject vague inputs such as `/agent fix`, `/agent fix help`, and `/agent fix make it better`.
- Reuse the rejected trigger audit log for both webhook and manual API rejections.
- Document operator-facing examples.

## Non-Goals

- No model-based classification before task creation.
- No language-specific issue analysis in the webhook request path.
- No change to execution sandboxing or language adapter support.

## Validation

- Safety gate unit coverage for actionable and non-actionable commands.
- Webhook service coverage proving vague comments are rejected without task creation or dispatch.
- Manual task API coverage proving rejected commands are audited.
- Full backend Maven test suite.
