# Harness Engineering

Harness engineering is the reliability layer around AI-assisted development. It makes PatchPilot reproducible, testable, and debuggable.

## Goals

- Make local development repeatable.
- Make validation commands explicit.
- Make task progress auditable.
- Make failures easy to investigate.
- Keep AI-generated changes within project guardrails.

## Required Harnesses

### Documentation Harness

Project documents live under:

```text
docs/product
docs/plans
docs/progress
docs/agent
```

Long-running implementation work should update:

```text
docs/progress/execution-log.md
```

### Test Harness

Every meaningful backend phase should define its required validation commands.

Common commands:

```bash
mvn test
```

Later phases may add:

```bash
docker compose config
docker compose up -d
curl -i http://localhost:8080/actuator/health
```

### Safety Harness

PatchPilot must enforce:

- Webhook signature verification.
- Workspace path guards.
- Command allowlists.
- Tool-call audit records.
- Model-call audit records.
- Secret redaction in logs.

### Demo Harness

The final project should include:

- A small public demo Java repository.
- Seed issues.
- A repeatable `/agent fix` demo flow.
- Before/after PR examples.
- Success and failure examples.

## Validation Records

Each implementation phase should record:

- Date.
- Command run.
- Result.
- Important failures.
- Follow-up decisions.

Use:

```text
docs/progress/execution-log.md
```
