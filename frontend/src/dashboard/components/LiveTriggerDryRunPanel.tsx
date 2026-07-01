import { Copy, ShieldCheck } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type { GitHubTriggerDryRun, GitHubTriggerDryRunInput, TriggerEvaluationResult } from '../../types';

interface LiveTriggerDryRunPanelProps {
  result: GitHubTriggerDryRun | null;
  error: string | null;
  pending: boolean;
  onDryRun: (input: GitHubTriggerDryRunInput) => Promise<GitHubTriggerDryRun> | Promise<void> | void;
}

export function LiveTriggerDryRunPanel({
  result,
  error,
  pending,
  onDryRun
}: LiveTriggerDryRunPanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [triggerComment, setTriggerComment] = useState('/agent fix touch docs/live-trigger-preview.md');

  function input(): GitHubTriggerDryRunInput {
    return {
      repositoryOwner: repositoryOwner.trim(),
      repositoryName: repositoryName.trim(),
      issueNumber: Number(issueNumber),
      triggerUser: triggerUser.trim(),
      triggerComment: triggerComment.trim()
    };
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onDryRun(input());
  }

  async function copyReport() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(buildLiveTriggerDryRunReport(result));
  }

  return (
    <section className="panel live-trigger-dry-run-panel" aria-label="Live trigger dry run">
      <div className="panel-header">
        <div>
          <h2>Live trigger dry run</h2>
          <p>Preview the exact GitHub issue comment without creating a task.</p>
        </div>
        {result ? (
          <button className="secondary-button" type="button" onClick={() => void copyReport()}>
            <Copy size={16} />
            Copy dry-run report
          </button>
        ) : null}
      </div>

      <form className="demo-launch-preflight-form" aria-label="Live trigger dry run form" onSubmit={(event) => void submit(event)}>
        <label htmlFor="live-trigger-repository-owner">
          Repository owner
          <input
            id="live-trigger-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-trigger-repository-name">
          Repository name
          <input
            id="live-trigger-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-trigger-issue-number">
          Issue number
          <input
            id="live-trigger-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-trigger-user">
          Trigger user
          <input
            id="live-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label className="demo-launch-comment" htmlFor="live-trigger-comment">
          GitHub issue comment
          <input
            id="live-trigger-comment"
            value={triggerComment}
            onChange={(event) => setTriggerComment(event.target.value)}
            placeholder="/agent fix touch docs/live-trigger-preview.md"
            required
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={pending || triggerComment.trim().length === 0}
        >
          <ShieldCheck size={16} />
          {pending ? 'Running dry run' : 'Run live trigger dry run'}
        </button>
      </form>

      {error ? (
        <div className="adapter-api-error">
          <strong>Live trigger dry run failed</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {result ? (
        <LiveTriggerDryRunResult result={result} />
      ) : (
        <div className="empty-state">No live trigger dry run yet.</div>
      )}
    </section>
  );
}

function LiveTriggerDryRunResult({ result }: { result: GitHubTriggerDryRun }) {
  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${result.wouldCreateTask ? 'ready' : 'blocked'}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${result.wouldCreateTask ? 'ready' : 'blocked'}`}>
          {result.status}
        </span>
        <strong>{result.wouldCreateTask ? 'Would create task' : 'Blocked'}</strong>
        <p>{result.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Repository</span>
          <strong>{result.repository}</strong>
        </div>
        <div>
          <span>Issue</span>
          <strong>#{result.issueNumber}</strong>
        </div>
        <div>
          <span>Issue URL</span>
          <strong>{result.issueUrl}</strong>
        </div>
        <div>
          <span>Trigger user</span>
          <strong>{result.triggerUser}</strong>
        </div>
        <div>
          <span>Issue context</span>
          <strong>{result.evaluation.issueContextLoaded ? 'Issue context loaded' : 'Issue context not loaded'}</strong>
        </div>
        <div>
          <span>Blocked category</span>
          <strong>{result.evaluation.blockedCategory ?? 'none'}</strong>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{result.sideEffectContract}</p>
      {result.evaluation.blockedReason ? (
        <p className="demo-launch-preflight-blocked">{result.evaluation.blockedReason}</p>
      ) : null}
      <div className="trigger-evaluation-grid">
        <DecisionItem label="Safety" decision={result.evaluation.safetyDecision} />
        <DecisionItem label="Active task" decision={result.evaluation.activeTaskDecision} />
        <DecisionItem label="Quarantine" decision={result.evaluation.quarantineDecision} />
        <DecisionItem label="Rate limit" decision={result.evaluation.rateLimitDecision} />
        <DecisionItem label="Model" decision={result.evaluation.triggerIntentDecision} />
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Next action</h3>
        <p>{result.nextAction}</p>
      </div>
    </div>
  );
}

function DecisionItem({
  label,
  decision
}: {
  label: string;
  decision: TriggerEvaluationResult['safetyDecision'];
}) {
  return (
    <div>
      <span>{label}</span>
      <p>{decision ? decision.reason : 'Not evaluated'}</p>
    </div>
  );
}

function buildLiveTriggerDryRunReport(result: GitHubTriggerDryRun) {
  return [
    '# PatchPilot Live Trigger Dry Run Report',
    '',
    `- Status: \`${result.status}\``,
    `- Would create task: \`${result.wouldCreateTask ? 'YES' : 'NO'}\``,
    `- Repository: \`${inlineCode(result.repository)}\``,
    `- Issue: \`#${result.issueNumber}\``,
    `- Issue URL: ${result.issueUrl}`,
    `- Trigger user: \`${inlineCode(result.triggerUser)}\``,
    `- Comment: \`${inlineCode(result.triggerComment)}\``,
    `- Source: \`${result.evaluation.source}\``,
    `- Issue context: \`${result.evaluation.issueContextLoaded ? 'LOADED' : 'NOT_LOADED'}\``,
    result.evaluation.blockedCategory
      ? `- Blocked category: \`${inlineCode(result.evaluation.blockedCategory)}\``
      : null,
    result.evaluation.blockedReason ? `- Blocked reason: ${result.evaluation.blockedReason}` : null,
    `- Side effects: ${result.sideEffectContract}`,
    `- Next action: ${result.nextAction}`,
    '',
    '## Gate Decisions',
    '',
    decisionLine('Safety', result.evaluation.safetyDecision),
    decisionLine('Active task', result.evaluation.activeTaskDecision),
    decisionLine('Quarantine', result.evaluation.quarantineDecision),
    decisionLine('Rate limit', result.evaluation.rateLimitDecision),
    decisionLine('Model', result.evaluation.triggerIntentDecision)
  ].filter((line): line is string => line !== null).join('\n');
}

function decisionLine(label: string, decision: TriggerEvaluationResult['safetyDecision']) {
  if (!decision) {
    return `- ${label}: \`NOT_EVALUATED\``;
  }
  return `- ${label}: \`${decision.allowed ? 'ALLOW' : 'BLOCK'}\` - ${decision.reason}`;
}

function inlineCode(value: string) {
  return value.replace(/`/g, "'").replace(/\s+/g, ' ').trim();
}
