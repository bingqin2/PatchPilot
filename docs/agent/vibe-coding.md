# Vibe Coding

PatchPilot uses AI-assisted development, but the project should not become a loose collection of generated code.

This document defines how to use AI help while keeping the backend resume-grade, testable, and safe.

## Principles

- Plans come before non-trivial implementation.
- Tests or validation commands must prove behavior.
- Agent-generated code is not trusted until reviewed and verified.
- External side effects must go through explicit tools or services.
- Project decisions are recorded in `docs/progress/decisions.md`.
- Execution progress is recorded in `docs/progress/execution-log.md`.

## Rules

### Use ExecPlans

Meaningful implementation should have an active plan under `docs/plans/`.

The plan should explain:

- Purpose.
- Context.
- Concrete steps.
- Validation.
- Recovery.
- Artifacts.

### Keep The Agent Bounded

The coding assistant may help generate implementation, but it must not:

- Skip repository inspection.
- Invent APIs without checking current code.
- Ignore project code standards.
- Remove user changes without permission.
- Claim success without running validation.

### Record Decisions

Record project-level decisions in:

```text
docs/progress/decisions.md
```

Record feature-level decisions in the active plan.

### Verify Before Completion

Before marking work complete, run the validation commands named by the plan. If validation cannot run, record why.

## PatchPilot-Specific Guardrails

- Never let model output execute as shell commands directly.
- Never auto-merge Pull Requests.
- Never write outside a task workspace.
- Never log secrets.
- Never report a fix as successful without test evidence.
