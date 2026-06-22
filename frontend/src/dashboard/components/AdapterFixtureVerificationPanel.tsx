import type { LanguageAdapterFixtureVerification } from '../../types';

interface AdapterFixtureVerificationPanelProps {
  verifications: LanguageAdapterFixtureVerification[];
  error: string | null;
}

export function AdapterFixtureVerificationPanel({ verifications, error }: AdapterFixtureVerificationPanelProps) {
  const passingCount = verifications.filter((verification) => verification.status === 'PASS').length;

  return (
    <section className="panel fixture-verification-panel" aria-label="Adapter fixture verification">
      <div className="panel-header">
        <div>
          <h2>Fixture verification</h2>
          <p>
            {verifications.length > 0
              ? `${passingCount}/${verifications.length} fixtures passing`
              : 'Loading fixture verification'}
          </p>
        </div>
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Fixture verification unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}
      <div className="fixture-verification-table" role="table" aria-label="Adapter fixture verification results">
        <div className="fixture-verification-head" role="row">
          <span role="columnheader">Fixture</span>
          <span role="columnheader">Expected</span>
          <span role="columnheader">Actual</span>
          <span role="columnheader">Expected command</span>
          <span role="columnheader">Actual command</span>
          <span role="columnheader">Reason</span>
          <span role="columnheader">Status</span>
        </div>
        {verifications.map((verification) => (
          <div
            className="fixture-verification-row"
            role="row"
            key={verification.fixturePath}
            aria-label={`${verification.fixtureName} ${verification.expectedLanguage} ${verification.expectedBuildSystem} ${verification.actualLanguage} ${verification.actualBuildSystem} ${verification.status}`}
          >
            <span role="cell">
              <strong>{verification.fixtureName}</strong>
              <small>{verification.fixturePath}</small>
            </span>
            <span role="cell">
              <strong>{verification.expectedLanguage}</strong>
              <small>{verification.expectedBuildSystem}</small>
            </span>
            <span role="cell">
              <strong>{verification.actualLanguage}</strong>
              <small>{verification.actualBuildSystem}</small>
            </span>
            <span role="cell" className="fixture-command-stack">
              <code>expected: {commandLabel(verification.expectedVerificationCommand)}</code>
            </span>
            <span role="cell" className="fixture-command-stack">
              <code>actual: {commandLabel(verification.actualVerificationCommand)}</code>
            </span>
            <span role="cell">{verification.reason}</span>
            <span role="cell" className={`adapter-status ${verification.status === 'FAIL' ? 'adapter-status-fail' : ''}`}>
              {verification.status}
            </span>
          </div>
        ))}
        {verifications.length === 0 ? <p className="empty-state">No fixture verification results loaded.</p> : null}
      </div>
    </section>
  );
}

function commandLabel(command: string[]) {
  return command.length > 0 ? command.join(' ') : 'none';
}
