import { Copy, Rocket } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type { DemoLiveLaunchGate, GitHubTriggerDryRunInput } from '../../types';

interface LiveLaunchGatePanelProps {
  result: DemoLiveLaunchGate | null;
  error: string | null;
  pending: boolean;
  onRunGate: (input: GitHubTriggerDryRunInput) => Promise<DemoLiveLaunchGate> | Promise<void> | void;
}

export function LiveLaunchGatePanel({
  result,
  error,
  pending,
  onRunGate
}: LiveLaunchGatePanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [triggerComment, setTriggerComment] = useState('/agent fix touch docs/live-gate.md');

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
    await onRunGate(input());
  }

  async function copyReport() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(result.markdownReport);
  }

  return (
    <section className="panel live-launch-gate-panel" aria-label="Live launch gate">
      <div className="panel-header">
        <div>
          <h2>Live launch gate</h2>
          <p>Run the final read-only gate before posting a real GitHub issue comment.</p>
        </div>
        {result ? (
          <button className="secondary-button" type="button" onClick={() => void copyReport()}>
            <Copy size={16} />
            Copy launch gate report
          </button>
        ) : null}
      </div>

      <form className="demo-launch-preflight-form" aria-label="Live launch gate form" onSubmit={(event) => void submit(event)}>
        <label htmlFor="live-launch-gate-repository-owner">
          Repository owner
          <input
            id="live-launch-gate-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-repository-name">
          Repository name
          <input
            id="live-launch-gate-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-issue-number">
          Issue number
          <input
            id="live-launch-gate-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-trigger-user">
          Trigger user
          <input
            id="live-launch-gate-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label className="demo-launch-comment" htmlFor="live-launch-gate-comment">
          GitHub issue comment
          <input
            id="live-launch-gate-comment"
            value={triggerComment}
            onChange={(event) => setTriggerComment(event.target.value)}
            placeholder="/agent fix touch docs/live-gate.md"
            required
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={pending || triggerComment.trim().length === 0}
        >
          <Rocket size={16} />
          {pending ? 'Running launch gate' : 'Run live launch gate'}
        </button>
      </form>

      {error ? (
        <div className="adapter-api-error">
          <strong>Live launch gate failed</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {result ? (
        <LiveLaunchGateResult result={result} />
      ) : (
        <div className="empty-state">No live launch gate run yet.</div>
      )}
    </section>
  );
}

function LiveLaunchGateResult({ result }: { result: DemoLiveLaunchGate }) {
  const tone = result.status === 'READY' ? 'ready' : result.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {result.status}
        </span>
        <strong>{result.readyToPost ? 'Ready to post' : result.status === 'BLOCKED' ? 'Blocked' : 'Needs attention'}</strong>
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
          <span>Webhook payload</span>
          <strong>{result.webhookSetup.payloadUrl}</strong>
        </div>
        <div>
          <span>Default branch</span>
          <strong>{result.livePublishPreflight.defaultBranch ?? 'unknown'}</strong>
        </div>
        <div>
          <span>Trigger dry run</span>
          <strong>{result.triggerDryRun.status}</strong>
        </div>
      </div>

      <p className="demo-launch-preflight-blocked">{result.sideEffectContract}</p>

      <div className="operator-setup-grid">
        {result.checks.map((check) => (
          <div
            className={`operator-setup-check operator-setup-check-${check.status === 'READY' ? 'ready' : check.status === 'BLOCKED' ? 'blocked' : 'attention'}`}
            key={check.name}
          >
            <span>{check.name}</span>
            <strong>{check.status}</strong>
            <p>{check.message}</p>
            <small>{check.action}</small>
          </div>
        ))}
      </div>

      <div className="demo-launch-preflight-actions">
        <h3>Next actions</h3>
        <ul>
          {result.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}
