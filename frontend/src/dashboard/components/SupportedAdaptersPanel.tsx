import type { SupportedLanguageAdapter } from '../../types';

interface SupportedAdaptersPanelProps {
  adapters: SupportedLanguageAdapter[];
  error: string | null;
}

export function SupportedAdaptersPanel({ adapters, error }: SupportedAdaptersPanelProps) {
  return (
    <section className="panel supported-adapters-panel" aria-label="Supported adapters">
      <div className="panel-header">
        <div>
          <h2>Supported adapters</h2>
          <p>{adapters.length > 0 ? `${adapters.length} supported adapters` : 'Loading supported adapters'}</p>
        </div>
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Adapter API unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}
      <div className="supported-adapters-table" role="table" aria-label="Supported language adapters">
        <div className="supported-adapters-head" role="row">
          <span role="columnheader">Adapter</span>
          <span role="columnheader">Command</span>
          <span role="columnheader">Signals</span>
          <span role="columnheader">Fixture</span>
          <span role="columnheader">Status</span>
        </div>
        {adapters.map((adapter) => (
          <div
            className="supported-adapters-row"
            role="row"
            key={`${adapter.language}-${adapter.buildSystem}`}
            aria-label={`${adapter.language} ${adapter.buildSystem} ${commandLabel(adapter)}`}
          >
            <span role="cell">
              <strong>{adapter.language}</strong>
              <small>{adapter.buildSystem}</small>
            </span>
            <code role="cell">{commandLabel(adapter)}</code>
            <span role="cell">{adapter.detectionSignals.join(', ')}</span>
            <span role="cell">{adapter.demoFixturePath}</span>
            <span role="cell" className="adapter-status">
              {adapter.status}
            </span>
          </div>
        ))}
        {adapters.length === 0 ? <p className="empty-state">No supported adapters loaded.</p> : null}
      </div>
    </section>
  );
}

function commandLabel(adapter: SupportedLanguageAdapter) {
  return adapter.verificationCommand.join(' ');
}
