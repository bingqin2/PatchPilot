import type { EvaluationCase, EvaluationCaseSummary } from '../../types';

interface EvaluationCaseCatalogPanelProps {
  cases: EvaluationCase[];
  summary: EvaluationCaseSummary | null;
  error: string | null;
  summaryError: string | null;
}

export function EvaluationCaseCatalogPanel({ cases, summary, error, summaryError }: EvaluationCaseCatalogPanelProps) {
  const supportedCases = cases.filter((evaluationCase) => evaluationCase.category === 'SUPPORTED_FIX');
  const rejectionCases = cases.filter((evaluationCase) => evaluationCase.category === 'SAFETY_REJECTION');
  const fallbackLanguages = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.language).filter((language): language is string => Boolean(language)))
  ).sort();
  const fallbackBuildSystems = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.buildSystem).filter((buildSystem): buildSystem is string => Boolean(buildSystem)))
  ).sort();
  const fallbackRejectionCategories = Array.from(
    new Set(rejectionCases.map((evaluationCase) => evaluationCase.expectedRejectionCategory).filter((category): category is string => Boolean(category)))
  ).sort();
  const status = summary?.status ?? (error || summaryError ? 'NEEDS_ATTENTION' : 'READY');
  const languages = summary?.coveredLanguages ?? fallbackLanguages;
  const buildSystems = summary?.coveredBuildSystems ?? fallbackBuildSystems;
  const rejectionCategories = summary?.rejectionCategories ?? fallbackRejectionCategories;
  const totalCaseCount = summary?.totalCaseCount ?? cases.length;
  const supportedFixCaseCount = summary?.supportedFixCaseCount ?? supportedCases.length;
  const safetyRejectionCaseCount = summary?.safetyRejectionCaseCount ?? rejectionCases.length;
  const nextAction = summary?.nextAction ?? (error || summaryError ? 'Refresh the catalog after fixing the evaluation API.' : 'Evaluation catalog is ready for demo evidence.');
  const healthContract = summary?.healthContract ?? 'This panel is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.';

  async function copyReport() {
    await navigator.clipboard?.writeText(buildEvaluationCaseCatalogReport(cases, summary, error, summaryError));
  }

  return (
    <section className="panel evaluation-case-panel" aria-label="Evaluation case catalog">
      <div className="panel-header">
        <div>
          <h2>Evaluation case catalog</h2>
          <p>{totalCaseCount} cases across {languages.length} {languages.length === 1 ? 'language' : 'languages'}</p>
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
      {summaryError ? (
        <div className="adapter-api-error">
          <strong>Evaluation summary incomplete</strong>
          <span>{summaryError}</span>
        </div>
      ) : null}
      <div className="adapter-readiness-summary">
        <div>
          <span>Readiness</span>
          <strong>{status}</strong>
          <p>{status === 'READY' ? 'Ready for demo evidence' : 'Needs catalog attention'}</p>
        </div>
        <div>
          <span>Supported fixes</span>
          <strong>{supportedFixCaseCount} supported fix {supportedFixCaseCount === 1 ? 'case' : 'cases'}</strong>
          <p>{languages.join(', ') || 'No supported language cases loaded'}</p>
        </div>
        <div>
          <span>Safety rejections</span>
          <strong>{safetyRejectionCaseCount} safety rejection {safetyRejectionCaseCount === 1 ? 'case' : 'cases'}</strong>
          <p>{rejectionCategories.join(', ') || 'No rejection cases loaded'}</p>
        </div>
        <div>
          <span>Build systems</span>
          <strong>{buildSystems.length} covered {buildSystems.length === 1 ? 'system' : 'systems'}</strong>
          <p>{buildSystems.join(', ') || 'No build systems loaded'}</p>
        </div>
        <div>
          <span>Contract</span>
          <strong>{nextAction}</strong>
          <p>{healthContract}</p>
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

function buildEvaluationCaseCatalogReport(
  cases: EvaluationCase[],
  summary: EvaluationCaseSummary | null,
  error: string | null,
  summaryError: string | null
) {
  const supportedCases = cases.filter((evaluationCase) => evaluationCase.category === 'SUPPORTED_FIX');
  const rejectionCases = cases.filter((evaluationCase) => evaluationCase.category === 'SAFETY_REJECTION');
  const fallbackLanguages = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.language).filter((language): language is string => Boolean(language)))
  ).sort();
  const fallbackBuildSystems = Array.from(
    new Set(supportedCases.map((evaluationCase) => evaluationCase.buildSystem).filter((buildSystem): buildSystem is string => Boolean(buildSystem)))
  ).sort();
  const status = summary?.status ?? (error || summaryError ? 'NEEDS_ATTENTION' : 'READY');
  const totalCaseCount = summary?.totalCaseCount ?? cases.length;
  const supportedFixCaseCount = summary?.supportedFixCaseCount ?? supportedCases.length;
  const safetyRejectionCaseCount = summary?.safetyRejectionCaseCount ?? rejectionCases.length;
  const languages = summary?.coveredLanguages ?? fallbackLanguages;
  const buildSystems = summary?.coveredBuildSystems ?? fallbackBuildSystems;
  const nextAction = summary?.nextAction ?? (error || summaryError ? 'Refresh the catalog after fixing the evaluation API.' : 'Evaluation catalog is ready for demo evidence.');
  return [
    '# PatchPilot Evaluation Case Catalog',
    '',
    `- Status: \`${status}\``,
    `- Cases: ${totalCaseCount}`,
    `- Supported fix cases: ${supportedFixCaseCount}`,
    `- Safety rejection cases: ${safetyRejectionCaseCount}`,
    `- Languages: ${languages.join(', ') || 'none'}`,
    `- Build systems: ${buildSystems.join(', ') || 'none'}`,
    `- Next action: ${nextAction}`,
    error ? `- Error: ${error}` : null,
    summaryError ? `- Summary error: ${summaryError}` : null,
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
