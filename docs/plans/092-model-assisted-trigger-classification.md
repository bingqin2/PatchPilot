# 092 Model-Assisted Trigger Classification

## Goal

Add an optional model-assisted checkpoint before task creation so PatchPilot can reject safe-but-unclear `/agent fix` requests before spending queue, clone, model-planning, test, and GitHub resources.

## Design

- Deterministic safety remains first and authoritative.
- The model classifier only runs after signature verification, trigger detection, allowlists, dangerous-command rejection, duplicate delivery checks, and active-task checks.
- The classifier returns one of:
  - `SHOULD_EXECUTE`
  - `NEEDS_CLARIFICATION`
  - `REJECTED`
- Only `SHOULD_EXECUTE` creates a task.
- Rejected or clarification-needed results are recorded through `RejectedTriggerAuditService`.
- The feature is disabled by default and enabled through `PATCHPILOT_MODEL_TRIGGER_CLASSIFICATION_ENABLED=true`.

## Non-Goals

- No model override for deterministic safety rejections.
- No issue body fetching in the trigger classifier.
- No new database table for pre-task classification.

## Validation

- Unit tests for enabled, disabled, successful, clarification, invalid JSON, and provider failure classifier behavior.
- Webhook tests proving model-declined triggers do not create or dispatch tasks.
- Manual API tests proving model-declined triggers are rejected before task creation.
- Full backend Maven test suite.
