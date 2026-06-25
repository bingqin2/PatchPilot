import { Clipboard, FilePlus2, SendToBack } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  DemoLaunchCommand,
  DemoLaunchCommandInput,
  DemoLaunchCommandOperation,
  DemoLaunchPreflightInput
} from '../../types';

interface DemoLaunchCommandPanelProps {
  result: DemoLaunchCommand | null;
  error: string | null;
  pending: boolean;
  onComposeCommand: (input: DemoLaunchCommandInput) => Promise<DemoLaunchCommand> | Promise<void> | void;
  onApplyToPreflight: (input: DemoLaunchPreflightInput) => void;
}

export function DemoLaunchCommandPanel({
  result,
  error,
  pending,
  onComposeCommand,
  onApplyToPreflight
}: DemoLaunchCommandPanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [operation, setOperation] = useState<DemoLaunchCommandOperation>('replace');
  const [targetPath, setTargetPath] = useState('docs/demo.md');
  const [replacementText, setReplacementText] = useState('PatchPilot smoke test');

  function input(): DemoLaunchCommandInput {
    return {
      repositoryOwner: repositoryOwner.trim(),
      repositoryName: repositoryName.trim(),
      issueNumber: Number(issueNumber),
      triggerUser: triggerUser.trim(),
      operation,
      targetPath: targetPath.trim(),
      replacementText: operation === 'replace' ? replacementText.trim() : null
    };
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onComposeCommand(input());
  }

  async function copyCommand() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(result.triggerComment);
  }

  return (
    <section className="panel demo-launch-command-panel" aria-label="Demo launch command composer">
      <div className="panel-header">
        <div>
          <h2>Demo launch command composer</h2>
          <p>Generate a controlled /agent fix comment before launch preflight.</p>
        </div>
        {result ? (
          <button className="secondary-button" type="button" onClick={() => void copyCommand()}>
            <Clipboard size={16} />
            Copy command
          </button>
        ) : null}
      </div>

      <form
        className="demo-launch-command-form"
        aria-label="Demo launch command form"
        onSubmit={(event) => void submit(event)}
      >
        <label htmlFor="demo-command-repository-owner">
          Repository owner
          <input
            id="demo-command-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-command-repository-name">
          Repository name
          <input
            id="demo-command-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-command-issue-number">
          Issue number
          <input
            id="demo-command-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-command-trigger-user">
          Trigger user
          <input
            id="demo-command-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label htmlFor="demo-command-operation">
          Operation
          <select
            id="demo-command-operation"
            value={operation}
            onChange={(event) => setOperation(event.target.value as DemoLaunchCommandOperation)}
          >
            <option value="replace">Replace file text</option>
            <option value="touch">Touch file</option>
          </select>
        </label>
        <label htmlFor="demo-command-target-path">
          Target path
          <input
            id="demo-command-target-path"
            value={targetPath}
            onChange={(event) => setTargetPath(event.target.value)}
            placeholder="docs/demo.md"
            required
          />
        </label>
        {operation === 'replace' ? (
          <label className="demo-launch-command-replacement" htmlFor="demo-command-replacement-text">
            Replacement text
            <input
              id="demo-command-replacement-text"
              value={replacementText}
              onChange={(event) => setReplacementText(event.target.value)}
              placeholder="PatchPilot smoke test"
              required
            />
          </label>
        ) : null}
        <button
          className="secondary-button"
          type="submit"
          disabled={pending || targetPath.trim().length === 0 || (operation === 'replace' && replacementText.trim().length === 0)}
        >
          <FilePlus2 size={16} />
          {pending ? 'Generating command' : 'Generate command'}
        </button>
      </form>

      {error ? (
        <div className="adapter-api-error">
          <strong>Command generation failed</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {result ? (
        <DemoLaunchCommandResult result={result} onApplyToPreflight={onApplyToPreflight} />
      ) : (
        <div className="empty-state">No demo launch command generated yet.</div>
      )}
    </section>
  );
}

function DemoLaunchCommandResult({
  result,
  onApplyToPreflight
}: {
  result: DemoLaunchCommand;
  onApplyToPreflight: (input: DemoLaunchPreflightInput) => void;
}) {
  return (
    <div className="demo-launch-command-result">
      <div className="demo-launch-command-output">
        <span>Generated issue comment</span>
        <code>{result.triggerComment}</code>
      </div>
      <p>{result.summary}</p>
      <div className="demo-launch-command-actions">
        <a href={result.githubIssueUrl} target="_blank" rel="noreferrer">
          Open GitHub issue
        </a>
        <button className="secondary-button" type="button" onClick={() => onApplyToPreflight(result.preflightInput)}>
          <SendToBack size={16} />
          Apply to launch preflight
        </button>
      </div>
      <ul>
        {result.nextActions.map((action) => (
          <li key={action}>{action}</li>
        ))}
      </ul>
    </div>
  );
}
