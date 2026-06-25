import { Clipboard, FilePlus2, RotateCcw, SendToBack, Trash2 } from 'lucide-react';
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

interface DemoLaunchCommandHistoryItem {
  id: string;
  savedAt: string;
  input: DemoLaunchCommandInput;
  result: DemoLaunchCommand;
}

const DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY = 'patchpilot.demoLaunchCommandHistory';
const DEMO_LAUNCH_COMMAND_HISTORY_LIMIT = 5;

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
  const [history, setHistory] = useState<DemoLaunchCommandHistoryItem[]>(loadDemoLaunchCommandHistory);

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
    const commandInput = input();
    const commandResult = await onComposeCommand(commandInput);
    if (commandResult) {
      addHistoryItem(commandInput, commandResult);
    }
  }

  async function copyCommand() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(result.triggerComment);
  }

  function addHistoryItem(commandInput: DemoLaunchCommandInput, commandResult: DemoLaunchCommand) {
    const item: DemoLaunchCommandHistoryItem = {
      id: `${Date.now()}-${commandResult.triggerComment}`,
      savedAt: new Date().toISOString(),
      input: commandInput,
      result: commandResult
    };

    setHistory((currentHistory) => {
      const updatedHistory = [
        item,
        ...currentHistory.filter((existing) => existing.result.triggerComment !== commandResult.triggerComment)
      ].slice(0, DEMO_LAUNCH_COMMAND_HISTORY_LIMIT);
      persistDemoLaunchCommandHistory(updatedHistory);
      return updatedHistory;
    });
  }

  function applyHistoryToComposer(item: DemoLaunchCommandHistoryItem) {
    setRepositoryOwner(item.input.repositoryOwner);
    setRepositoryName(item.input.repositoryName);
    setIssueNumber(String(item.input.issueNumber));
    setTriggerUser(item.input.triggerUser);
    setOperation(item.input.operation);
    setTargetPath(item.input.targetPath);
    setReplacementText(item.input.replacementText ?? '');
  }

  function clearHistory() {
    setHistory([]);
    persistDemoLaunchCommandHistory([]);
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

      <DemoLaunchCommandHistory
        history={history}
        onApplyToComposer={applyHistoryToComposer}
        onApplyToPreflight={onApplyToPreflight}
        onClearHistory={clearHistory}
      />
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

function DemoLaunchCommandHistory({
  history,
  onApplyToComposer,
  onApplyToPreflight,
  onClearHistory
}: {
  history: DemoLaunchCommandHistoryItem[];
  onApplyToComposer: (item: DemoLaunchCommandHistoryItem) => void;
  onApplyToPreflight: (input: DemoLaunchPreflightInput) => void;
  onClearHistory: () => void;
}) {
  async function copySavedCommand(item: DemoLaunchCommandHistoryItem) {
    await navigator.clipboard?.writeText(item.result.triggerComment);
  }

  return (
    <div className="demo-launch-command-history">
      <div className="demo-launch-command-history-header">
        <div>
          <h3>Recent demo launch commands</h3>
          <p>{history.length > 0 ? `${history.length} saved locally in this browser.` : 'Saved only in this browser.'}</p>
        </div>
        {history.length > 0 ? (
          <button className="secondary-button" type="button" onClick={onClearHistory}>
            <Trash2 size={16} />
            Clear command history
          </button>
        ) : null}
      </div>

      {history.length > 0 ? (
        <ul aria-label="Recent demo launch commands">
          {history.map((item) => (
            <li key={item.id}>
              <div className="demo-launch-command-history-copy">
                <code>{item.result.triggerComment}</code>
                <span>
                  {item.input.repositoryOwner}/{item.input.repositoryName} #{item.input.issueNumber} - {item.input.operation} -{' '}
                  {formatSavedAt(item.savedAt)}
                </span>
              </div>
              <div className="demo-launch-command-history-actions">
                <button className="secondary-button" type="button" onClick={() => void copySavedCommand(item)}>
                  <Clipboard size={16} />
                  Copy saved command
                </button>
                <button className="secondary-button" type="button" onClick={() => onApplyToComposer(item)}>
                  <RotateCcw size={16} />
                  Apply saved command to composer
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onApplyToPreflight(item.result.preflightInput)}
                >
                  <SendToBack size={16} />
                  Apply saved command to launch preflight
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No saved demo launch commands yet.</p>
      )}
    </div>
  );
}

function loadDemoLaunchCommandHistory(): DemoLaunchCommandHistoryItem[] {
  if (typeof globalThis.localStorage === 'undefined') {
    return [];
  }

  try {
    const rawHistory = globalThis.localStorage.getItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY);
    if (!rawHistory) {
      return [];
    }

    const parsedHistory: unknown = JSON.parse(rawHistory);
    if (!Array.isArray(parsedHistory)) {
      return [];
    }

    return parsedHistory.filter(isDemoLaunchCommandHistoryItem).slice(0, DEMO_LAUNCH_COMMAND_HISTORY_LIMIT);
  } catch {
    return [];
  }
}

function persistDemoLaunchCommandHistory(history: DemoLaunchCommandHistoryItem[]) {
  if (typeof globalThis.localStorage === 'undefined') {
    return;
  }

  try {
    if (history.length === 0) {
      globalThis.localStorage.removeItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY);
      return;
    }
    globalThis.localStorage.setItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY, JSON.stringify(history));
  } catch {
    // Browsers may reject localStorage writes in private contexts; history is optional.
  }
}

function isDemoLaunchCommandHistoryItem(value: unknown): value is DemoLaunchCommandHistoryItem {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'string' &&
    typeof value.savedAt === 'string' &&
    isDemoLaunchCommandInput(value.input) &&
    isDemoLaunchCommand(value.result)
  );
}

function isDemoLaunchCommandInput(value: unknown): value is DemoLaunchCommandInput {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.repositoryOwner === 'string' &&
    typeof value.repositoryName === 'string' &&
    typeof value.issueNumber === 'number' &&
    typeof value.triggerUser === 'string' &&
    (value.operation === 'replace' || value.operation === 'touch') &&
    typeof value.targetPath === 'string' &&
    (value.replacementText === null || typeof value.replacementText === 'string')
  );
}

function isDemoLaunchCommand(value: unknown): value is DemoLaunchCommand {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.triggerComment === 'string' &&
    typeof value.githubIssueUrl === 'string' &&
    typeof value.summary === 'string' &&
    isDemoLaunchPreflightInput(value.preflightInput) &&
    Array.isArray(value.nextActions) &&
    value.nextActions.every((action) => typeof action === 'string')
  );
}

function isDemoLaunchPreflightInput(value: unknown): value is DemoLaunchPreflightInput {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.repositoryOwner === 'string' &&
    typeof value.repositoryName === 'string' &&
    typeof value.issueNumber === 'number' &&
    typeof value.triggerUser === 'string' &&
    typeof value.triggerComment === 'string'
  );
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function formatSavedAt(savedAt: string) {
  const timestamp = new Date(savedAt);
  if (Number.isNaN(timestamp.getTime())) {
    return 'saved locally';
  }
  return timestamp.toLocaleString();
}
