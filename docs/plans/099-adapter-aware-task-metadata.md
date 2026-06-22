# 099 Adapter-Aware Task Metadata

## Goal

Make every task expose the language adapter selected during execution, including language, build system, and verification command. Operators should be able to answer which adapter ran without reading raw tool-call logs.

## Scope

- Persist adapter metadata on `fix_task`.
- Record metadata immediately after successful language-adapter detection.
- Expose metadata through task list/detail API responses.
- Show metadata in the dashboard task list and selected task detail.
- Keep unsupported repositories unchanged: they fail before adapter metadata is recorded.

## Out Of Scope

- Filtering by adapter fields.
- Adding new adapters.
- Reworking verification execution.

## Acceptance Checks

- Existing tasks can have null adapter metadata.
- Supported tasks record `language`, `buildSystem`, and `verificationCommand`.
- Dashboard shows adapter metadata for tasks that have it.
- Verification output labels are language-neutral.
