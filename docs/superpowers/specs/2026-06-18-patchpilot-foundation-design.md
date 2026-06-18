# PatchPilot Foundation Design

## Background

PatchPilot is being set up as a Java/Spring Boot GitHub issue-to-PR agent backend. The project needs a documentation and governance baseline before implementation starts.

## Design

Use the reference documentation layout:

```text
docs/product
docs/plans
docs/progress
docs/agent
docs/superpowers
```

Product docs describe the stable target. Plans describe executable implementation phases. Progress docs record decisions and validation history. Agent docs describe AI-assisted development rules.

## Accepted Initial Documents

Product:

- `docs/product/spec.md`
- `docs/product/architecture.md`
- `docs/product/backend-code-standard.md`
- `docs/product/target-state.md`
- `docs/product/roadmap.md`
- `docs/product/milestones.md`

Plans:

- `docs/plans/000-project-foundation.md`

Progress:

- `docs/progress/decisions.md`
- `docs/progress/execution-log.md`

Agent:

- `docs/agent/vibe-coding.md`
- `docs/agent/harness-engineering.md`
- `docs/agent/execplan/README.md`

## Open Items

- Confirm model integration choice: direct OpenAI-compatible Java client or Spring AI.
- Confirm persistence choice for the first durable task phase.
- Confirm whether Docker sandboxing is required before the first demo.
