import type { LanguageAdapterFixtureVerification, SupportedLanguageAdapter } from '../../types';

interface AdapterReadinessReportPanelProps {
  adapters: SupportedLanguageAdapter[];
  verifications: LanguageAdapterFixtureVerification[];
  error: string | null;
}

export function AdapterReadinessReportPanel({ adapters, verifications, error }: AdapterReadinessReportPanelProps) {
  const passingCount = verifications.filter((verification) => verification.status === 'PASS').length;
  const failingVerifications = verifications.filter((verification) => verification.status === 'FAIL');
  const languages = Array.from(new Set(adapters.map((adapter) => adapter.language))).sort();
  const readinessStatus = readinessLabel(verifications.length, passingCount, error);

  async function copyReport() {
    await navigator.clipboard?.writeText(buildAdapterReadinessReport(adapters, verifications, error));
  }

  return (
    <section className="panel adapter-readiness-panel" aria-label="Adapter readiness report">
      <div className="panel-header">
        <div>
          <h2>Adapter readiness report</h2>
          <p>{readinessStatus}</p>
        </div>
        <button type="button" className="secondary-button" onClick={copyReport}>
          Copy adapter readiness report
        </button>
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Adapter readiness incomplete</strong>
          <span>{error}</span>
        </div>
      ) : null}
      <div className="adapter-readiness-summary">
        <div>
          <span>Coverage</span>
          <strong>
            {adapters.length} adapters across {languages.length} {languages.length === 1 ? 'language' : 'languages'}
          </strong>
          <p>{languages.join(', ') || 'No languages loaded'}</p>
        </div>
        <div>
          <span>Fixture drift</span>
          <strong>
            {passingCount}/{verifications.length} fixtures passing
          </strong>
          <p>{failingVerifications.length === 0 ? 'No fixture drift detected.' : `${failingVerifications.length} fixture failure detected.`}</p>
        </div>
      </div>
      <div className="adapter-readiness-section">
        <h3>Allowlisted verification commands</h3>
        <div className="adapter-readiness-command-list">
          {adapters.map((adapter) => (
            <div className="adapter-readiness-command-row" key={`${adapter.language}-${adapter.buildSystem}`}>
              <strong>{adapter.language}/{adapter.buildSystem}</strong>
              <code>{commandLabel(adapter.verificationCommand)}</code>
              <span>{adapter.detectionSignals.join(', ')}</span>
            </div>
          ))}
          {adapters.length === 0 ? <p className="empty-state compact-empty-state">No adapters loaded.</p> : null}
        </div>
      </div>
      <div className="adapter-readiness-section">
        <h3>Fixture failures</h3>
        {failingVerifications.length > 0 ? (
          <div className="adapter-readiness-failure-list">
            {failingVerifications.map((verification) => (
              <div className="adapter-readiness-failure-row" key={verification.fixturePath}>
                <strong>{verification.fixtureName}</strong>
                <span>
                  expected {verification.expectedLanguage}/{verification.expectedBuildSystem}, actual{' '}
                  {verification.actualLanguage}/{verification.actualBuildSystem}
                </span>
                <p>{verification.reason}</p>
              </div>
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No adapter fixture failures.</p>
        )}
      </div>
    </section>
  );
}

function readinessLabel(totalCount: number, passingCount: number, error: string | null) {
  if (error) {
    return `Attention - ${passingCount}/${totalCount} fixtures passing`;
  }
  if (totalCount > 0 && passingCount === totalCount) {
    return `Ready - ${passingCount}/${totalCount} fixtures passing`;
  }
  return `Attention - ${passingCount}/${totalCount} fixtures passing`;
}

function buildAdapterReadinessReport(
  adapters: SupportedLanguageAdapter[],
  verifications: LanguageAdapterFixtureVerification[],
  error: string | null
) {
  const passingCount = verifications.filter((verification) => verification.status === 'PASS').length;
  const failingVerifications = verifications.filter((verification) => verification.status === 'FAIL');
  const languages = Array.from(new Set(adapters.map((adapter) => adapter.language))).sort();
  const status = !error && verifications.length > 0 && passingCount === verifications.length
    ? 'READY'
    : 'NEEDS_ATTENTION';
  return [
    '# PatchPilot Adapter Readiness Report',
    '',
    `- Status: \`${status}\``,
    `- Adapters: ${adapters.length}`,
    `- Languages: ${languages.join(', ') || 'none'}`,
    `- Fixtures: ${passingCount}/${verifications.length} passing`,
    error ? `- Error: ${error}` : null,
    '',
    '## Allowlisted Verification Commands',
    '',
    ...adapters.map((adapter) => (
      `- \`${adapter.language}/${adapter.buildSystem}\`: \`${commandLabel(adapter.verificationCommand)}\``
    )),
    '',
    '## Fixture Failures',
    '',
    ...(failingVerifications.length > 0
      ? failingVerifications.map((verification) => (
        `- \`${verification.fixtureName}\`: expected \`${verification.expectedLanguage}/${verification.expectedBuildSystem}\`, actual \`${verification.actualLanguage}/${verification.actualBuildSystem}\`; ${verification.reason}`
      ))
      : ['No adapter fixture failures.'])
  ].filter((line): line is string => line !== null).join('\n');
}

function commandLabel(command: string[]) {
  return command.length > 0 ? command.join(' ') : 'none';
}
