import { Copy, SearchCheck } from 'lucide-react';
import { useEffect, useRef, useState, type FormEvent } from 'react';
import type { DemoLaunchPreflight, DemoLaunchPreflightInput, DemoReadinessStatus } from '../../types';

interface DemoLaunchPreflightPanelProps {
  result: DemoLaunchPreflight | null;
  error: string | null;
  pending: boolean;
  composedPreflightInput?: DemoLaunchPreflightInput | null;
  onRunPreflight: (input: DemoLaunchPreflightInput) => Promise<DemoLaunchPreflight> | Promise<void> | void;
}

export function DemoLaunchPreflightPanel({
  result,
  error,
  pending,
  composedPreflightInput,
  onRunPreflight
}: DemoLaunchPreflightPanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [triggerComment, setTriggerComment] = useState('/agent fix replace docs/demo.md PatchPilot smoke test');
  const lastAppliedPreflightInputKey = useRef<string | null>(null);

  useEffect(() => {
    if (!composedPreflightInput) {
      return;
    }
    const inputKey = JSON.stringify(composedPreflightInput);
    if (inputKey === lastAppliedPreflightInputKey.current) {
      return;
    }
    setRepositoryOwner(composedPreflightInput.repositoryOwner);
    setRepositoryName(composedPreflightInput.repositoryName);
    setIssueNumber(String(composedPreflightInput.issueNumber));
    setTriggerUser(composedPreflightInput.triggerUser);
    setTriggerComment(composedPreflightInput.triggerComment);
    lastAppliedPreflightInputKey.current = inputKey;
  }, [composedPreflightInput]);

  function input(): DemoLaunchPreflightInput {
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
    await onRunPreflight(input());
  }

  async function copyReport() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(buildDemoLaunchPreflightReport(input(), result, error));
  }

  return (
    <section className="panel demo-launch-preflight-panel" aria-label="Demo launch preflight">
      <div className="panel-header">
        <div>
          <h2>Demo launch preflight</h2>
          <p>Dry-run the exact GitHub issue comment before posting it.</p>
        </div>
        {result ? (
          <button className="secondary-button" type="button" onClick={() => void copyReport()}>
            <Copy size={16} />
            Copy launch preflight report
          </button>
        ) : null}
      </div>

      <form
        className="demo-launch-preflight-form"
        aria-label="Demo launch preflight form"
        onSubmit={(event) => void submit(event)}
      >
        <label htmlFor="demo-launch-repository-owner">
          Repository owner
          <input
            id="demo-launch-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-launch-repository-name">
          Repository name
          <input
            id="demo-launch-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-launch-issue-number">
          Issue number
          <input
            id="demo-launch-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-launch-trigger-user">
          Trigger user
          <input
            id="demo-launch-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label className="demo-launch-comment" htmlFor="demo-launch-trigger-comment">
          GitHub issue comment
          <input
            id="demo-launch-trigger-comment"
            value={triggerComment}
            onChange={(event) => setTriggerComment(event.target.value)}
            placeholder="/agent fix replace docs/demo.md PatchPilot smoke test"
            required
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={pending || triggerComment.trim().length === 0}
        >
          <SearchCheck size={16} />
          {pending ? 'Running preflight' : 'Run launch preflight'}
        </button>
      </form>

      {error ? (
        <div className="adapter-api-error">
          <strong>Launch preflight failed</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {result ? (
        <DemoLaunchPreflightResult result={result} />
      ) : (
        <div className="empty-state">No launch preflight run yet.</div>
      )}
    </section>
  );
}

function DemoLaunchPreflightResult({ result }: { result: DemoLaunchPreflight }) {
  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${statusClass(result.status)}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${statusClass(result.status)}`}>
          {statusLabel(result.status)}
        </span>
        <strong>{result.readyToPost ? 'Ready to post' : 'Not ready'}</strong>
        <p>{result.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Readiness</span>
          <strong>{result.readiness.status}</strong>
        </div>
        <div>
          <span>Trigger evaluation</span>
          <strong>{result.triggerEvaluation.status}</strong>
        </div>
        <div>
          <span>Issue context</span>
          <strong>{result.triggerEvaluation.issueContextLoaded ? 'Loaded' : 'Not loaded'}</strong>
        </div>
        <div>
          <span>Blocked category</span>
          <strong>{result.triggerEvaluation.blockedCategory ?? 'none'}</strong>
        </div>
      </div>
      {result.triggerEvaluation.blockedReason ? (
        <p className="demo-launch-preflight-blocked">{result.triggerEvaluation.blockedReason}</p>
      ) : null}
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

function buildDemoLaunchPreflightReport(
  input: DemoLaunchPreflightInput,
  result: DemoLaunchPreflight,
  error: string | null
) {
  return [
    '# PatchPilot Demo Launch Preflight Report',
    '',
    `- Status: \`${result.status}\``,
    `- Ready to post: \`${result.readyToPost ? 'YES' : 'NO'}\``,
    `- Repository: \`${inlineCode(`${input.repositoryOwner}/${input.repositoryName}`)}\``,
    `- Issue: \`#${input.issueNumber}\``,
    `- Trigger user: \`${inlineCode(input.triggerUser)}\``,
    `- Comment: \`${inlineCode(input.triggerComment)}\``,
    `- Readiness: \`${result.readiness.status}\``,
    `- Trigger evaluation: \`${result.triggerEvaluation.status}\``,
    `- Source: \`${result.triggerEvaluation.source}\``,
    `- Issue context: \`${result.triggerEvaluation.issueContextLoaded ? 'LOADED' : 'NOT_LOADED'}\``,
    result.triggerEvaluation.blockedCategory
      ? `- Blocked category: \`${inlineCode(result.triggerEvaluation.blockedCategory)}\``
      : null,
    result.triggerEvaluation.blockedReason ? `- Blocked reason: ${result.triggerEvaluation.blockedReason}` : null,
    error ? `- Error: ${error}` : null,
    '',
    '## Next Actions',
    '',
    result.nextActions.length === 1 ? `- Next action: ${result.nextActions[0]}` : null,
    ...result.nextActions.map((action) => `- ${action}`)
  ].filter((line): line is string => line !== null).join('\n');
}

function statusLabel(status: DemoReadinessStatus) {
  switch (status) {
    case 'READY':
      return 'Ready';
    case 'NEEDS_ATTENTION':
      return 'Needs attention';
    case 'BLOCKED':
      return 'Blocked';
  }
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}

function inlineCode(value: string) {
  return value.replace(/`/g, "'").replace(/\s+/g, ' ').trim();
}
