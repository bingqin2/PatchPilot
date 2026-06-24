import { useState, type FormEvent } from 'react';
import type { RepositoryPreflightInput, RepositoryPreflightResult } from '../../types';

interface RepositoryPreflightPanelProps {
  result: RepositoryPreflightResult | null;
  error: string | null;
  loading: boolean;
  allowedRootDirs: string[];
  onRunPreflight: (input: RepositoryPreflightInput) => void | Promise<void>;
}

const DEFAULT_REPOSITORY_PATH = 'docs/demo-repositories/java-maven';

export function RepositoryPreflightPanel({
  result,
  error,
  loading,
  allowedRootDirs,
  onRunPreflight
}: RepositoryPreflightPanelProps) {
  const [repositoryPath, setRepositoryPath] = useState(DEFAULT_REPOSITORY_PATH);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onRunPreflight({ repositoryPath: repositoryPath.trim() });
  }

  return (
    <section className="panel repository-preflight-panel" aria-label="Repository preflight">
      <div className="panel-header">
        <div>
          <h2>Repository preflight</h2>
          <p>Validate local adapter support before creating a fix task</p>
        </div>
      </div>
      <form className="repository-preflight-form" onSubmit={(event) => void submit(event)}>
        <label htmlFor="repository-preflight-path">
          Repository path
          <input
            id="repository-preflight-path"
            value={repositoryPath}
            onChange={(event) => setRepositoryPath(event.target.value)}
            placeholder="docs/demo-repositories/java-maven"
          />
        </label>
        <button className="secondary-button" type="submit" disabled={loading || repositoryPath.trim().length === 0}>
          {loading ? 'Running' : 'Run preflight'}
        </button>
      </form>
      <div className="repository-preflight-roots">
        <strong>Allowed roots</strong>
        {allowedRootDirs.length > 0 ? (
          <p>{allowedRootDirs.join(', ')}</p>
        ) : (
          <p>No repository preflight allowed roots configured</p>
        )}
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Preflight failed</strong>
          <span>{error}</span>
        </div>
      ) : null}
      {result ? <RepositoryPreflightResultView result={result} /> : <p className="empty-state">No repository preflight run yet.</p>}
    </section>
  );
}

function RepositoryPreflightResultView({ result }: { result: RepositoryPreflightResult }) {
  return (
    <div className="repository-preflight-result">
      <div className="repository-preflight-summary">
        <span className={`adapter-status ${result.supported ? '' : 'adapter-status-fail'}`}>
          {result.supported ? 'SUPPORTED' : 'UNSUPPORTED'}
        </span>
        <strong>{result.repositoryPath}</strong>
      </div>
      <div className="repository-preflight-grid">
        <div>
          <span>Language</span>
          <strong>{result.language}</strong>
        </div>
        <div>
          <span>Build system</span>
          <strong>{result.buildSystem}</strong>
        </div>
        <div>
          <span>Verification command</span>
          <code>{commandLabel(result.verificationCommand)}</code>
        </div>
        <div>
          <span>Detection reason</span>
          <p>{result.reason}</p>
        </div>
      </div>
      <div className="repository-preflight-action">{result.operatorAction}</div>
      {!result.supported && result.supportedAdapters.length > 0 ? (
        <div className="repository-preflight-adapters" aria-label="Supported adapter options">
          {result.supportedAdapters.map((adapter) => (
            <div key={`${adapter.language}-${adapter.buildSystem}`}>
              <strong>
                {adapter.language} / {adapter.buildSystem}
              </strong>
              <span>{adapter.detectionSignals.join(', ')}</span>
              <code>{adapter.verificationCommand.join(' ')}</code>
            </div>
          ))}
        </div>
      ) : null}
    </div>
  );
}

function commandLabel(command: string[]) {
  return command.length > 0 ? command.join(' ') : 'none';
}
