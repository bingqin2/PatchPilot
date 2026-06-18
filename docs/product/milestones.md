# Milestones

## Milestone 0: Foundation

Outcome:

- The project is documented, runnable, and ready for implementation.

Evidence:

- Product docs exist.
- Backend code standard exists.
- `mvn test` passes.
- Local backend starts.
- Health endpoint works.

## Milestone 1: Webhook To Task

Outcome:

- GitHub can trigger PatchPilot through `/agent fix`.

Evidence:

- Webhook signatures are verified.
- `issue_comment.created` is parsed.
- `/agent fix` creates a task.
- Duplicate delivery handling exists or is planned with a clear persistence hook.

## Milestone 2: Task To Workspace

Outcome:

- A task can prepare a repository workspace safely.

Evidence:

- Repository clone works.
- Branch creation works.
- Workspace path guard tests pass.
- GitHub issue comment callback works.

## Milestone 3: Agent Patch

Outcome:

- The agent can inspect code and apply a focused patch through controlled tools.

Evidence:

- Tool registry exists.
- Code search, file read, file write, and diff tools work.
- Tool calls are audited.
- Unsafe file paths are rejected.

## Milestone 4: Verified Pull Request

Outcome:

- PatchPilot can open a tested Pull Request for a simple Java/Maven issue.

Evidence:

- Maven tests run.
- Test results are recorded.
- PR is created.
- Issue is updated with the PR link.

## Milestone 5: Production-Shaped Hardening

Outcome:

- PatchPilot has the minimum safety and observability expected from a serious backend project.

Evidence:

- Durable MySQL persistence.
- Docker sandbox or documented isolation boundary.
- Model-call and tool-call audit records.
- Failure reasons are visible.
- Metrics exist for success rate, latency, and cost.
