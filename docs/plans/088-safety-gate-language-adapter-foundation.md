# Safety Gate And Language Adapter Foundation

## Goal

Move PatchPilot closer to the safe multi-language target by adding a pre-execution safety gate and a first language adapter boundary.

## Why This Matters

PatchPilot should not create expensive or risky work merely because a comment starts with `/agent fix`. The next maturity step is to reject unsafe, vague, or unsupported requests before task creation and to make Java/Maven the first explicit adapter rather than a hard-coded special case.

## Scope

- Parse supported `/agent fix` commands into an explicit decision.
- Reject dangerous or non-actionable commands before task creation.
- Apply the same gate to GitHub webhooks and dashboard-created manual tasks.
- Add a `LanguageAdapter` interface and a Java/Maven implementation.
- Keep the existing Java/Maven runtime behavior working.

## Out Of Scope

- Real GitHub collaborator permission checks.
- Node.js, Python, or Gradle execution.
- Hosted multi-tenant abuse controls.
- Automatic triage of comments without an explicit `/agent fix` command.

## Implementation Tasks

1. Add safety gate domain types and service.
2. Add failing tests for rejected webhook and manual commands.
3. Wire the safety gate into webhook and manual task creation.
4. Add language adapter domain types and Java/Maven adapter.
5. Route Maven test execution through the adapter while preserving current behavior.
6. Update docs and execution log with validation evidence.

## Validation

- Focused webhook and manual task tests should fail before implementation and pass after wiring.
- Focused Java/Maven adapter tests should fail before implementation and pass after the adapter exists.
- `mvn -pl PatchPilot test` should pass.
- `git diff --check` should pass.
