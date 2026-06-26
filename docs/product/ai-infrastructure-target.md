# AI Infrastructure Target

This document defines the AI infrastructure capabilities PatchPilot can grow into. It is intentionally broader than the first implementation slice. The basic product should still prove one reliable issue-to-PR workflow before these capabilities are expanded.

## Target Principle

PatchPilot should treat model calls as one controlled backend dependency, not as hidden magic inside the worker. Every model decision that can affect a repository should be structured, validated, auditable, cost-aware, and reversible by a human operator when risk is high.

The AI infrastructure target is to make PatchPilot a production-shaped agent backend with clear model boundaries, prompt governance, evaluation, observability, and safety controls.

## Implementation Order

AI infrastructure should be implemented in layers:

1. OpenAI-compatible model client, structured outputs, and model-call audit records.
2. Prompt templates, prompt version ids, and deterministic schema validation.
3. Tool registry, tool-call traces, and generated-diff safety review.
4. Cost, latency, retry, and budget visibility.
5. Evaluation fixtures and benchmark runs for issue-to-PR tasks.
6. Repository retrieval, lightweight RAG, and semantic code search when lexical search is not enough.
7. Multi-provider routing, fallback, and hosted-service controls after the local workflow is stable.

## Model Gateway

PatchPilot should expose one internal model gateway instead of scattering provider-specific calls through the worker.

Target capabilities:

- Provider-neutral request and response objects.
- OpenAI-compatible chat completions as the first provider path.
- Provider configuration through environment variables or protected admin configuration.
- Model capability metadata such as structured-output support, context window, tool-call support, and pricing inputs.
- Request timeouts, retry policy, and failure categories.
- Optional provider routing for different task stages such as trigger classification, fix planning, patch generation, patch review, and summarization.
- Optional fallback model when the primary model fails or exceeds budget.
- No direct model API calls from controllers, repositories, or tool implementations.

## Prompt Governance

Prompts should be treated as versioned product artifacts.

Target capabilities:

- Prompt templates for trigger classification, issue summarization, fix planning, file edit generation, patch review, PR body generation, and failure summarization.
- Prompt version id recorded on every model-call record.
- Stable JSON schemas for model outputs that influence task execution.
- Strict schema validation before any output is accepted.
- Safe fallback behavior when model output is malformed, incomplete, or low confidence.
- Prompt regression tests using checked-in issue and repository fixtures.
- Prompt change notes in the relevant plan or execution log when prompt behavior changes.

## Agent Orchestration

The agent workflow should remain deterministic around model calls.

Target capabilities:

- Explicit workflow states for issue loading, repository inspection, fix planning, patch generation, patch review, verification, PR creation, and failure reporting.
- Maximum model-call count per task.
- Maximum tool-call count per task.
- Maximum changed-file and changed-line thresholds before review.
- Structured intermediate artifacts: issue summary, repository context summary, fix plan, edit plan, patch review, verification summary, and PR summary.
- Idempotent task resume where practical.
- No execution of model-generated shell commands.
- Clear stop conditions for unsupported, unsafe, unactionable, low-confidence, or over-budget tasks.

## Tool Registry

Tools are the boundary between model reasoning and repository or GitHub side effects.

Target capabilities:

- Typed tool definitions with stable names, input schemas, output schemas, and risk levels.
- Allowlisted tools for repository tree inspection, code search, file reading, file writing, diff inspection, verification, git operations, pull request creation, and issue comments.
- Per-tool timeout and output-size limits.
- Workspace path validation for every filesystem tool.
- Separate read-only tools from mutation tools.
- Tool-call audit records for input summary, output summary, duration, success state, error category, and artifact references.
- Operator-visible tool traces in task detail and copied reports.

## Repository Retrieval And RAG

PatchPilot should start with deterministic lexical retrieval and add RAG only when the workflow needs it.

Target capabilities:

- Repository tree indexing for supported workspaces.
- Lexical code search over filenames, symbols, and text snippets.
- Dependency-aware context selection for common project types.
- Optional embedding index for large repositories after lexical search reaches limits.
- Pluggable vector store boundary so MySQL can remain the transactional database while vector search can be added later.
- Retrieval audit records showing which files and snippets were provided to the model.
- Index invalidation when the workspace branch changes.
- No embedding or indexing of secrets, generated artifacts, dependency folders, or files outside the task workspace.

## Evaluation Harness

PatchPilot should be measured through repeatable issue-to-PR fixtures, not only manual demos.

Target capabilities:

- Checked-in evaluation cases with repository fixture, issue text, expected changed files, expected verification command, and success criteria.
- The first implementation surface is a read-only evaluation case catalog and readiness summary API/dashboard panel, so operators can inspect supported language and safety-rejection scenarios before automated benchmark execution exists.
- Smoke benchmarks for Java/Maven, Java/Gradle, Go, Node, and Python adapters.
- Metrics for trigger acceptance accuracy, unsupported-repository rejection accuracy, patch application success, verification pass rate, PR creation success, false rejection rate, false acceptance rate, model cost, and latency.
- Stored evaluation runs that can compare model, prompt version, adapter, and code revision.
- A Markdown report for each benchmark run that can be used in demos and interviews.
- A small public benchmark set for resume evidence and a private set for regression testing.

## Observability And Cost Control

AI behavior should be inspectable from both APIs and the React dashboard.

Target capabilities:

- Model-call timeline with provider, model, prompt version, token counts, duration, estimated cost, success state, and error category.
- Tool-call timeline with duration, success state, and compact input/output summaries.
- Per-task cost and latency summary.
- Aggregate cost, latency, success-rate, failure-rate, test-pass-rate, and retry-rate dashboards.
- Budget limits per task, repository, trigger user, and deployment instance.
- Alerts or readiness warnings when model credentials, pricing inputs, queue worker state, or provider health are missing.
- Redacted prompt and output summaries that are useful for debugging without exposing secrets.

## Safety And Policy Layer

AI infrastructure must make unsafe work harder to start and easier to diagnose.

Target capabilities:

- Deterministic safety checks before model calls.
- Optional model-assisted classification after deterministic checks.
- Prompt-injection detection for issue text, comments, and repository files.
- Secret-like content detection before model input, file writes, diff review, logs, and GitHub comments.
- Generated-diff risk review before verification or GitHub writes.
- Human approval for high-risk generated diffs.
- Refusal categories that are stable enough for API filtering, dashboard summaries, and GitHub comments.
- Durable audit records for manual operator overrides, approvals, cancellations, retries, quarantines, and releases.

## Data Targets

Long-term AI infrastructure may require these durable records:

- `model_provider_config`: non-secret provider metadata and active routing policy.
- `prompt_version`: prompt name, version, schema name, checksum, and activation status.
- `model_call_record`: provider, model, prompt version, token usage, duration, cost, summaries, status, and error category.
- `tool_call_record`: tool name, input summary, output summary, artifact references, duration, status, and error category.
- `agent_artifact`: structured issue summary, fix plan, edit plan, patch review, verification summary, and PR summary.
- `repository_index`: repository, branch, index type, freshness, excluded paths, and index status.
- `retrieval_record`: selected files, snippets, scores, and reason for inclusion.
- `evaluation_case`: fixture metadata, issue text, expected behavior, and success criteria.
- `evaluation_run`: model, prompt version, repository revision, metrics, artifacts, and report path.
- `safety_decision_record`: deterministic and model-assisted safety decisions with stable categories.

The first implementation does not need all of these tables. New tables should be added only when a feature needs durable state for product behavior, debugging, or demo evidence.

## AI Infra Resume Signals

When implemented incrementally, this target can support stronger resume bullets than a simple API wrapper:

- Built a provider-neutral model gateway with audited OpenAI-compatible calls, structured JSON validation, token/cost tracking, and retry-aware failure categories.
- Designed an agent workflow where model outputs are constrained by typed tools, workspace guards, command allowlists, generated-diff risk review, and human approval gates.
- Implemented an evaluation harness that measures issue-to-PR success rate, verification pass rate, cost, and latency across Java, Go, Node.js, and Python repository fixtures.
- Added repository retrieval and optional RAG boundaries while keeping transactional task state in MySQL and model context selection auditable.
