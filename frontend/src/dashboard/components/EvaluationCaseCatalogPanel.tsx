import type { EvaluationCase } from '../../types';

interface EvaluationCaseCatalogPanelProps {
  cases: EvaluationCase[];
  error: string | null;
}

export function EvaluationCaseCatalogPanel({ cases, error }: EvaluationCaseCatalogPanelProps) {
  const supportedCases = cases.filter((evaluationCase) => evaluationCase.category === 'SUPPORTED_FIX');
  const rejectionCases = cases.filter((evaluationCase) => evaluationCase.category === 'SAFETY_REJECTION');
  const languages = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.language).filter((language): language is string => Boolean(language)))
  ).sort();

  async function copyReport() {
    await navigator.clipboard?.writeText(buildEvaluationCaseCatalogReport(cases, error));
  }

  return (
    <section className="panel evaluation-case-panel" aria-label="Evaluation case catalog">
      <div className="panel-header">
        <div>
          <h2>Evaluation case catalog</h2>
          <p>{cases.length} cases across {languages.length} {languages.length === 1 ? 'language' : 'languages'}</p>
        </div>
        <button type="button" className="secondary-button" onClick={copyReport}>
          Copy evaluation catalog report
        </button>
      </div>
      {error ? (
        <div className="adapter-api-error">
          <strong>Evaluation catalog incomplete</strong>
          <span>{error}</span>
        </div>
      ) : null}
      <div className="adapter-readiness-summary">
        <div>
          <span>Supported fixes</span>
          <strong>{supportedCases.length} supported fix {supportedCases.length === 1 ? 'case' : 'cases'}</strong>
          <p>{languages.join(', ') || 'No supported language cases loaded'}</p>
        </div>
        <div>
          <span>Safety rejections</span>
          <strong>{rejectionCases.length} safety rejection {rejectionCases.length === 1 ? 'case' : 'cases'}</strong>
          <p>{rejectionCases.map((evaluationCase) => evaluationCase.expectedRejectionCategory).filter(Boolean).join(', ') || 'No rejection cases loaded'}</p>
        </div>
        <div>
          <span>Contract</span>
          <strong>Read-only benchmark map</strong>
          <p>No tasks, model calls, tests, Git commands, or GitHub writes are run from this panel.</p>
        </div>
      </div>
      <div className="adapter-readiness-section">
        <h3>Supported issue-to-PR cases</h3>
        {supportedCases.length > 0 ? (
          <div className="evaluation-case-list">
            {supportedCases.map((evaluationCase) => (
              <EvaluationCaseRow evaluationCase={evaluationCase} key={evaluationCase.id} />
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No supported fix evaluation cases loaded.</p>
        )}
      </div>
      <div className="adapter-readiness-section">
        <h3>Safety rejection cases</h3>
        {rejectionCases.length > 0 ? (
          <div className="evaluation-case-list">
            {rejectionCases.map((evaluationCase) => (
              <EvaluationCaseRow evaluationCase={evaluationCase} key={evaluationCase.id} />
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No safety rejection evaluation cases loaded.</p>
        )}
      </div>
    </section>
  );
}

function EvaluationCaseRow({ evaluationCase }: { evaluationCase: EvaluationCase }) {
  return (
    <article className="evaluation-case-row">
      <div>
        <strong>{evaluationCase.title}</strong>
        <span>{evaluationCase.id}</span>
      </div>
      <p>{evaluationCase.issueText}</p>
      <div className="evaluation-case-facts">
        <span>{evaluationCase.language && evaluationCase.buildSystem ? `${evaluationCase.language}/${evaluationCase.buildSystem}` : evaluationCase.category}</span>
        <code>{commandLabel(evaluationCase.expectedVerificationCommand)}</code>
        {evaluationCase.repositoryFixturePath ? <span>{evaluationCase.repositoryFixturePath}</span> : null}
        {evaluationCase.expectedRejectionCategory ? <span>{evaluationCase.expectedRejectionCategory}</span> : null}
      </div>
      {evaluationCase.expectedChangedFiles.length > 0 ? (
        <div className="evaluation-case-detail">
          <span>Expected files</span>
          <p>{evaluationCase.expectedChangedFiles.join(', ')}</p>
        </div>
      ) : null}
      <div className="evaluation-case-detail">
        <span>Success criteria</span>
        <ul>
          {evaluationCase.successCriteria.map((criterion) => (
            <li key={criterion}>{criterion}</li>
          ))}
        </ul>
      </div>
      <div className="evaluation-case-detail">
        <span>Safety expectation</span>
        <p>{evaluationCase.safetyExpectation}</p>
      </div>
    </article>
  );
}

function buildEvaluationCaseCatalogReport(cases: EvaluationCase[], error: string | null) {
  const supportedCases = cases.filter((evaluationCase) => evaluationCase.category === 'SUPPORTED_FIX');
  const rejectionCases = cases.filter((evaluationCase) => evaluationCase.category === 'SAFETY_REJECTION');
  const languages = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.language).filter((language): language is string => Boolean(language)))
  ).sort();
  return [
    '# PatchPilot Evaluation Case Catalog',
    '',
    `- Status: \`${error ? 'NEEDS_ATTENTION' : 'READY'}\``,
    `- Cases: ${cases.length}`,
    `- Supported fix cases: ${supportedCases.length}`,
    `- Safety rejection cases: ${rejectionCases.length}`,
    `- Languages: ${languages.join(', ') || 'none'}`,
    error ? `- Error: ${error}` : null,
    '',
    '## Supported Issue-to-PR Cases',
    '',
    ...(supportedCases.length > 0 ? supportedCases.map(formatCaseReport) : ['No supported fix evaluation cases loaded.']),
    '',
    '## Safety Rejection Cases',
    '',
    ...(rejectionCases.length > 0 ? rejectionCases.map(formatCaseReport) : ['No safety rejection evaluation cases loaded.'])
  ].filter((line): line is string => line !== null).join('\n');
}

function formatCaseReport(evaluationCase: EvaluationCase) {
  return [
    `- \`${evaluationCase.id}\`: ${evaluationCase.title}`,
    evaluationCase.language && evaluationCase.buildSystem
      ? `  - Adapter: \`${evaluationCase.language}/${evaluationCase.buildSystem}\``
      : `  - Category: \`${evaluationCase.category}\``,
    evaluationCase.repositoryFixturePath ? `  - Fixture: \`${evaluationCase.repositoryFixturePath}\`` : null,
    `  - Command: \`${commandLabel(evaluationCase.expectedVerificationCommand)}\``,
    evaluationCase.expectedRejectionCategory ? `  - Rejection: \`${evaluationCase.expectedRejectionCategory}\`` : null,
    evaluationCase.expectedChangedFiles.length > 0
      ? `  - Expected files: ${evaluationCase.expectedChangedFiles.map((file) => `\`${file}\``).join(', ')}`
      : null,
    `  - Decision: \`${evaluationCase.expectedDecision}\``,
    `  - Safety: ${evaluationCase.safetyExpectation}`
  ].filter((line): line is string => line !== null).join('\n');
}

function commandLabel(command: string[]) {
  return command.length > 0 ? command.join(' ') : 'none';
}
