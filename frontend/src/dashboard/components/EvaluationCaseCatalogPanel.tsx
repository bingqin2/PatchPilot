import type {
  EvaluationCase,
  EvaluationCaseFixtureReadiness,
  EvaluationCaseFixtureReadinessSummary,
  EvaluationCaseSummary,
  EvaluationFixtureBaselineCase,
  EvaluationFixtureBaselineRunDigest,
  EvaluationFixtureBaselineRunArchive,
  EvaluationFixtureBaselineRunRegressionSummary,
  EvaluationFixtureBaselineSummary,
  EvaluationRunArchive,
  EvaluationRunArchiveDigest,
  EvaluationRunArchiveReadinessSummary,
  EvaluationRunPreview,
  EvaluationRunSnapshotArchive
} from '../../types';

interface EvaluationCaseCatalogPanelProps {
  cases: EvaluationCase[];
  summary: EvaluationCaseSummary | null;
  caseReadiness: EvaluationCaseFixtureReadinessSummary | null;
  fixtureBaseline: EvaluationFixtureBaselineSummary | null;
  fixtureBaselineLoading: boolean;
  fixtureBaselineRuns: EvaluationFixtureBaselineRunArchive[];
  fixtureBaselineRegressionSummary: EvaluationFixtureBaselineRunRegressionSummary | null;
  runPreview: EvaluationRunPreview | null;
  evaluationRuns: EvaluationRunArchive[];
  evaluationRunSummary: EvaluationRunArchiveReadinessSummary | null;
  evaluationRunLoading: boolean;
  archives: EvaluationRunSnapshotArchive[];
  error: string | null;
  summaryError: string | null;
  caseReadinessError: string | null;
  fixtureBaselineError: string | null;
  fixtureBaselineRegressionError: string | null;
  runPreviewError: string | null;
  evaluationRunError: string | null;
  evaluationRunSummaryError: string | null;
  archiveError: string | null;
  onRunFixtureBaseline: () => Promise<EvaluationFixtureBaselineSummary>;
  onRunAndArchiveFixtureBaseline: () => Promise<EvaluationFixtureBaselineRunArchive>;
  onDownloadFixtureBaselineRunReport: (runId: string) => Promise<Blob>;
  onArchiveRunSnapshot: () => Promise<EvaluationRunSnapshotArchive>;
  onRunAndArchiveEvaluation: () => Promise<EvaluationRunArchive>;
  onDownloadEvaluationRunReport: (runId: string) => Promise<Blob>;
  onDownloadArchiveReport: (snapshotId: string) => Promise<Blob>;
}

export function EvaluationCaseCatalogPanel({
  cases,
  summary,
  caseReadiness,
  fixtureBaseline,
  fixtureBaselineLoading,
  fixtureBaselineRuns,
  fixtureBaselineRegressionSummary,
  runPreview,
  evaluationRuns,
  evaluationRunSummary,
  evaluationRunLoading,
  archives,
  error,
  summaryError,
  caseReadinessError,
  fixtureBaselineError,
  fixtureBaselineRegressionError,
  runPreviewError,
  evaluationRunError,
  evaluationRunSummaryError,
  archiveError,
  onRunFixtureBaseline,
  onRunAndArchiveFixtureBaseline,
  onDownloadFixtureBaselineRunReport,
  onArchiveRunSnapshot,
  onRunAndArchiveEvaluation,
  onDownloadEvaluationRunReport,
  onDownloadArchiveReport
}: EvaluationCaseCatalogPanelProps) {
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

  async function copyRunPreview() {
    if (runPreview) {
      await navigator.clipboard?.writeText(runPreview.markdownReport);
    }
  }

  async function copyFixtureReadinessReport() {
    if (caseReadiness) {
      await navigator.clipboard?.writeText(caseReadiness.markdownReport);
    }
  }

  async function runFixtureBaseline() {
    await onRunFixtureBaseline();
  }

  async function runAndArchiveFixtureBaseline() {
    await onRunAndArchiveFixtureBaseline();
  }

  async function copyFixtureBaselineReport() {
    if (fixtureBaseline) {
      await navigator.clipboard?.writeText(fixtureBaseline.markdownReport);
    }
  }

  async function copyFixtureBaselineRunReport(archive: EvaluationFixtureBaselineRunArchive) {
    await navigator.clipboard?.writeText(archive.report);
  }

  async function copyFixtureBaselineRegressionReport() {
    if (fixtureBaselineRegressionSummary) {
      await navigator.clipboard?.writeText(fixtureBaselineRegressionSummary.markdownReport);
    }
  }

  async function downloadFixtureBaselineRunReport(archive: EvaluationFixtureBaselineRunArchive) {
    const blob = await onDownloadFixtureBaselineRunReport(archive.id);
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `patchpilot-evaluation-fixture-baseline-run-${archive.id}.md`;
    anchor.click();
    URL.revokeObjectURL(url);
  }

  async function archiveRunSnapshot() {
    await onArchiveRunSnapshot();
  }

  async function runAndArchiveEvaluation() {
    await onRunAndArchiveEvaluation();
  }

  async function copyEvaluationRunReport(archive: EvaluationRunArchive) {
    await navigator.clipboard?.writeText(archive.report);
  }

  async function copyEvaluationRunSummaryReport() {
    if (evaluationRunSummary) {
      await navigator.clipboard?.writeText(evaluationRunSummary.markdownReport);
    }
  }

  async function downloadEvaluationRunReport(archive: EvaluationRunArchive) {
    const blob = await onDownloadEvaluationRunReport(archive.id);
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `patchpilot-evaluation-run-${archive.id}.md`;
    anchor.click();
    URL.revokeObjectURL(url);
  }

  async function copyArchivedReport(archive: EvaluationRunSnapshotArchive) {
    await navigator.clipboard?.writeText(archive.report);
  }

  async function downloadArchivedReport(archive: EvaluationRunSnapshotArchive) {
    const blob = await onDownloadArchiveReport(archive.id);
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `patchpilot-evaluation-run-snapshot-${archive.id}.md`;
    anchor.click();
    URL.revokeObjectURL(url);
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
      {caseReadinessError ? (
        <div className="adapter-api-error">
          <strong>Evaluation case fixture readiness incomplete</strong>
          <span>{caseReadinessError}</span>
        </div>
      ) : null}
      {fixtureBaselineError ? (
        <div className="adapter-api-error">
          <strong>Evaluation fixture baseline incomplete</strong>
          <span>{fixtureBaselineError}</span>
        </div>
      ) : null}
      {fixtureBaselineRegressionError ? (
        <div className="adapter-api-error">
          <strong>Evaluation fixture baseline regression incomplete</strong>
          <span>{fixtureBaselineRegressionError}</span>
        </div>
      ) : null}
      {runPreviewError ? (
        <div className="adapter-api-error">
          <strong>Evaluation run preview incomplete</strong>
          <span>{runPreviewError}</span>
        </div>
      ) : null}
      {evaluationRunError ? (
        <div className="adapter-api-error">
          <strong>Evaluation run archive incomplete</strong>
          <span>{evaluationRunError}</span>
        </div>
      ) : null}
      {evaluationRunSummaryError ? (
        <div className="adapter-api-error">
          <strong>Evaluation run readiness summary incomplete</strong>
          <span>{evaluationRunSummaryError}</span>
        </div>
      ) : null}
      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Evaluation run snapshot archive incomplete</strong>
          <span>{archiveError}</span>
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
      {caseReadiness ? (
        <div className="adapter-readiness-section evaluation-case-readiness-section">
          <div className="panel-subheader">
            <div>
              <h3>Evaluation case fixture readiness</h3>
              <p>{caseReadiness.totalCaseCount} checked cases</p>
            </div>
            <button type="button" className="secondary-button" onClick={copyFixtureReadinessReport}>
              Copy fixture readiness report
            </button>
          </div>
          <div className="adapter-readiness-summary">
            <div>
              <span>Fixture status</span>
              <strong>{caseReadiness.status}</strong>
              <p>{caseReadiness.nextAction}</p>
            </div>
            <div>
              <span>Passing</span>
              <strong>{caseReadiness.passingCaseCount} passing {caseReadiness.passingCaseCount === 1 ? 'case' : 'cases'}</strong>
              <p>Adapter and expected-file checks match.</p>
            </div>
            <div>
              <span>No fixture required</span>
              <strong>{caseReadiness.noFixtureRequiredCaseCount} no-fixture-required {caseReadiness.noFixtureRequiredCaseCount === 1 ? 'case' : 'cases'}</strong>
              <p>Safety rejection coverage does not need repository files.</p>
            </div>
            <div>
              <span>Failing</span>
              <strong>{caseReadiness.failingCaseCount} failing {caseReadiness.failingCaseCount === 1 ? 'case' : 'cases'}</strong>
              <p>{caseReadiness.sideEffectContract}</p>
            </div>
          </div>
          <div className="evaluation-case-list">
            {caseReadiness.cases.map((readiness) => (
              <EvaluationCaseReadinessRow readiness={readiness} key={readiness.caseId} />
            ))}
          </div>
        </div>
      ) : null}
      <div className="adapter-readiness-section evaluation-fixture-baseline-section">
        <div className="panel-subheader">
          <div>
            <h3>Evaluation fixture baseline</h3>
            <p>{fixtureBaseline ? `${fixtureBaseline.executedCaseCount} executed cases` : 'Run supported fixture verification on demand'}</p>
          </div>
          <div className="panel-actions">
            <button type="button" className="secondary-button" onClick={runFixtureBaseline} disabled={fixtureBaselineLoading}>
              {fixtureBaselineLoading ? 'Running fixture baseline' : 'Run fixture baseline'}
            </button>
            <button type="button" className="secondary-button" onClick={runAndArchiveFixtureBaseline} disabled={fixtureBaselineLoading}>
              Run and archive fixture baseline
            </button>
            {fixtureBaseline ? (
              <button type="button" className="secondary-button" onClick={copyFixtureBaselineReport}>
                Copy fixture baseline report
              </button>
            ) : null}
          </div>
        </div>
        {fixtureBaseline ? (
          <>
            <div className="adapter-readiness-summary">
              <div>
                <span>Baseline status</span>
                <strong>{fixtureBaseline.status}</strong>
                <p>{fixtureBaseline.nextAction}</p>
              </div>
              <div>
                <span>Passed</span>
                <strong>{fixtureBaseline.passedCaseCount} passed {fixtureBaseline.passedCaseCount === 1 ? 'case' : 'cases'}</strong>
                <p>{fixtureBaseline.executedCaseCount} executed cases</p>
              </div>
              <div>
                <span>Failed</span>
                <strong>{fixtureBaseline.failedCaseCount} failed {fixtureBaseline.failedCaseCount === 1 ? 'case' : 'cases'}</strong>
                <p>Failing commands need fixture or adapter attention.</p>
              </div>
              <div>
                <span>Skipped</span>
                <strong>{fixtureBaseline.skippedCaseCount} skipped {fixtureBaseline.skippedCaseCount === 1 ? 'case' : 'cases'}</strong>
                <p>{fixtureBaseline.sideEffectContract}</p>
              </div>
            </div>
            <div className="evaluation-case-list">
              {fixtureBaseline.cases.map((baselineCase) => (
                <EvaluationFixtureBaselineRow baselineCase={baselineCase} key={baselineCase.caseId} />
              ))}
            </div>
          </>
        ) : (
          <p className="empty-state compact-empty-state">No fixture baseline run has been executed in this dashboard session.</p>
        )}
      </div>
      <div className="adapter-readiness-section evaluation-fixture-baseline-archive-section">
        <div className="panel-subheader">
          <div>
            <h3>Evaluation fixture baseline regression</h3>
            <p>{fixtureBaselineRegressionSummary ? fixtureBaselineRegressionSummary.nextAction : 'Archive baseline runs to compare regression movement'}</p>
          </div>
          {fixtureBaselineRegressionSummary ? (
            <button type="button" className="secondary-button" onClick={copyFixtureBaselineRegressionReport}>
              Copy fixture baseline regression report
            </button>
          ) : null}
        </div>
        {fixtureBaselineRegressionSummary ? (
          <div className="adapter-readiness-summary">
            <div>
              <span>Regression status</span>
              <strong>{fixtureBaselineRegressionSummary.status}</strong>
              <p>{fixtureBaselineRegressionSummary.sideEffectContract}</p>
            </div>
            <div>
              <span>Latest</span>
              <strong>{fixtureBaselineRegressionSummary.latestRun?.id ?? 'none'}</strong>
              <p>{baselineRunDigestLabel(fixtureBaselineRegressionSummary.latestRun)}</p>
            </div>
            <div>
              <span>Previous</span>
              <strong>{fixtureBaselineRegressionSummary.previousRun?.id ?? 'none'}</strong>
              <p>{baselineRunDigestLabel(fixtureBaselineRegressionSummary.previousRun)}</p>
            </div>
            <div>
              <span>Deltas</span>
              <strong>{`Passed ${signedCount(fixtureBaselineRegressionSummary.passedDelta)}`}</strong>
              <p>{`Failed ${signedCount(fixtureBaselineRegressionSummary.failedDelta)}`}</p>
              <p>{`Skipped ${signedCount(fixtureBaselineRegressionSummary.skippedDelta)}`}</p>
            </div>
            <div>
              <span>Case movement</span>
              <strong>{`Newly failed: ${listLabel(fixtureBaselineRegressionSummary.newlyFailedCaseIds)}`}</strong>
              <p>{`Recovered: ${listLabel(fixtureBaselineRegressionSummary.recoveredCaseIds)}`}</p>
            </div>
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No fixture baseline regression summary loaded yet.</p>
        )}
      </div>
      <div className="adapter-readiness-section evaluation-fixture-baseline-archive-section">
        <h3>Archived evaluation fixture baseline runs</h3>
        {fixtureBaselineRuns.length > 0 ? (
          <div className="evaluation-case-list">
            {fixtureBaselineRuns.map((archive) => (
              <article className="evaluation-case-row" key={archive.id}>
                <div>
                  <strong>{archive.status}</strong>
                  <span>{archive.id}</span>
                </div>
                <p>{archive.createdAt}</p>
                <div className="evaluation-case-facts">
                  <span>{archive.totalCaseCount} cases</span>
                  <span>{archive.executedCaseCount} executed</span>
                  <span>{archive.passedCaseCount} passed</span>
                  <span>{archive.failedCaseCount} failed</span>
                  <span>{archive.skippedCaseCount} skipped</span>
                </div>
                <div className="evaluation-case-detail">
                  <span>Archive contract</span>
                  <p>{archive.sideEffectContract}</p>
                </div>
                <div className="evaluation-case-detail">
                  <span>Next action</span>
                  <p>{archive.nextAction}</p>
                </div>
                <div className="panel-actions">
                  <button type="button" className="secondary-button" onClick={() => copyFixtureBaselineRunReport(archive)}>
                    Copy {archive.id} baseline report
                  </button>
                  <button type="button" className="secondary-button" onClick={() => downloadFixtureBaselineRunReport(archive)}>
                    Download {archive.id} baseline report
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No evaluation fixture baseline runs archived yet.</p>
        )}
      </div>
      {runPreview ? (
        <div className="adapter-readiness-section evaluation-run-preview-section">
          <div className="panel-subheader">
            <div>
              <h3>{runPreview.title}</h3>
              <p>{runPreview.previewRunId}</p>
            </div>
            <div className="panel-actions">
              <button type="button" className="secondary-button" onClick={copyRunPreview}>
                Copy evaluation run preview
              </button>
              <button type="button" className="secondary-button" onClick={archiveRunSnapshot}>
                Archive evaluation run snapshot
              </button>
            </div>
          </div>
          <div className="adapter-readiness-summary">
            <div>
              <span>Preview status</span>
              <strong>{runPreview.status}</strong>
              <p>{runPreview.caseCount} expected cases</p>
            </div>
            <div>
              <span>Expected commands</span>
              <strong>{runPreview.expectedVerificationCommands.length} commands</strong>
              <p>{runPreview.expectedVerificationCommands.join(', ') || 'No commands loaded'}</p>
            </div>
            <div>
              <span>Safety categories</span>
              <strong>{runPreview.safetyRejectionCategories.length} categories</strong>
              <p>{runPreview.safetyRejectionCategories.join(', ') || 'No safety categories loaded'}</p>
            </div>
            <div>
              <span>Run contract</span>
              <strong>{runPreview.nextAction}</strong>
              <p>{runPreview.sideEffectContract}</p>
            </div>
          </div>
          {runPreview.gaps.length > 0 ? (
            <div className="evaluation-case-detail">
              <span>Known gaps before automated runs</span>
              <ul>
                {runPreview.gaps.map((gap) => (
                  <li key={gap}>{gap}</li>
                ))}
              </ul>
            </div>
          ) : null}
        </div>
      ) : null}
      <div className="adapter-readiness-section evaluation-run-archive-section">
        <div className="panel-subheader">
          <div>
            <h3>Full evaluation run readiness</h3>
            <p>{evaluationRunSummary ? evaluationRunSummary.nextAction : 'Archive a full evaluation run to produce readiness evidence'}</p>
          </div>
          {evaluationRunSummary ? (
            <button type="button" className="secondary-button" onClick={copyEvaluationRunSummaryReport}>
              Copy full evaluation readiness report
            </button>
          ) : null}
        </div>
        {evaluationRunSummary ? (
          <div className="adapter-readiness-summary">
            <div>
              <span>Readiness status</span>
              <strong>{evaluationRunSummary.status}</strong>
              <p>{evaluationRunSummary.sideEffectContract}</p>
            </div>
            <div>
              <span>Latest run</span>
              <strong>{evaluationRunSummary.latestRun?.id ?? 'none'}</strong>
              <p>{evaluationRunDigestLabel(evaluationRunSummary.latestRun)}</p>
            </div>
            <div>
              <span>Previous run</span>
              <strong>{evaluationRunSummary.previousRun?.id ?? 'none'}</strong>
              <p>{evaluationRunDigestLabel(evaluationRunSummary.previousRun)}</p>
            </div>
            <div>
              <span>Deltas</span>
              <strong>{`Passed ${signedCount(evaluationRunSummary.passedDelta)}`}</strong>
              <p>{`Failed ${signedCount(evaluationRunSummary.failedDelta)}`}</p>
              <p>{`Skipped ${signedCount(evaluationRunSummary.skippedDelta)}`}</p>
            </div>
            <div>
              <span>Coverage</span>
              <strong>{listLabel(evaluationRunSummary.coveredLanguages)}</strong>
              <p>{listLabel(evaluationRunSummary.coveredBuildSystems)}</p>
            </div>
            <div>
              <span>Safety categories</span>
              <strong>{listLabel(evaluationRunSummary.safetyRejectionCategories)}</strong>
              <p>{evaluationRunSummary.nextAction}</p>
            </div>
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No full evaluation run readiness summary loaded yet.</p>
        )}
      </div>
      <div className="adapter-readiness-section evaluation-run-archive-section">
        <div className="panel-subheader">
          <div>
            <h3>Archived evaluation runs</h3>
            <p>{evaluationRuns.length > 0 ? `${evaluationRuns.length} archived local evaluation ${evaluationRuns.length === 1 ? 'run' : 'runs'}` : 'Run local fixture and safety coverage evidence on demand'}</p>
          </div>
          <button type="button" className="secondary-button" onClick={runAndArchiveEvaluation} disabled={evaluationRunLoading}>
            {evaluationRunLoading ? 'Running evaluation' : 'Run evaluation'}
          </button>
        </div>
        {evaluationRuns.length > 0 ? (
          <div className="evaluation-case-list">
            {evaluationRuns.map((archive) => (
              <article className="evaluation-case-row" key={archive.id}>
                <div>
                  <strong>{archive.status}</strong>
                  <span>{archive.id}</span>
                </div>
                <p>{archive.createdAt}</p>
                <div className="evaluation-case-facts">
                  <span>{archive.totalCaseCount} cases</span>
                  <span>{archive.supportedFixCaseCount} supported</span>
                  <span>{archive.safetyRejectionCaseCount} safety</span>
                  <span>{archive.executedFixCaseCount} executed</span>
                  <span>{archive.passedFixCaseCount} passed</span>
                  <span>{archive.failedFixCaseCount} failed</span>
                  <span>{archive.skippedCaseCount} skipped</span>
                </div>
                <div className="evaluation-case-detail">
                  <span>Coverage</span>
                  <p>{`${archive.coveredLanguages.join(', ') || 'No languages'} / ${archive.coveredBuildSystems.join(', ') || 'No build systems'}`}</p>
                </div>
                <div className="evaluation-case-detail">
                  <span>Safety categories</span>
                  <p>{archive.safetyRejectionCategories.join(', ') || 'No safety categories'}</p>
                </div>
                <div className="evaluation-case-detail">
                  <span>Archive contract</span>
                  <p>{archive.sideEffectContract}</p>
                </div>
                <div className="evaluation-case-detail">
                  <span>Next action</span>
                  <p>{archive.nextAction}</p>
                </div>
                <div className="panel-actions">
                  <button type="button" className="secondary-button" onClick={() => copyEvaluationRunReport(archive)}>
                    Copy {archive.id} evaluation report
                  </button>
                  <button type="button" className="secondary-button" onClick={() => downloadEvaluationRunReport(archive)}>
                    Download {archive.id} evaluation report
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No evaluation runs archived yet.</p>
        )}
      </div>
      <div className="adapter-readiness-section evaluation-run-archive-section">
        <h3>Archived evaluation run snapshots</h3>
        {archives.length > 0 ? (
          <div className="evaluation-case-list">
            {archives.map((archive) => (
              <article className="evaluation-case-row" key={archive.id}>
                <div>
                  <strong>{archive.title}</strong>
                  <span>{archive.id}</span>
                </div>
                <p>{archive.createdAt}</p>
                <div className="evaluation-case-facts">
                  <span>{archive.status}</span>
                  <span>{archive.previewRunId}</span>
                  <span>{archive.caseCount} cases</span>
                  <span>{archive.coveredLanguages.join(', ') || 'No languages'}</span>
                </div>
                <div className="evaluation-case-detail">
                  <span>Archive contract</span>
                  <p>{archive.sideEffectContract}</p>
                </div>
                <div className="panel-actions">
                  <button type="button" className="secondary-button" onClick={() => copyArchivedReport(archive)}>
                    Copy {archive.id} report
                  </button>
                  <button type="button" className="secondary-button" onClick={() => downloadArchivedReport(archive)}>
                    Download {archive.id} report
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="empty-state compact-empty-state">No evaluation run snapshots archived yet.</p>
        )}
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

function EvaluationCaseReadinessRow({ readiness }: { readiness: EvaluationCaseFixtureReadiness }) {
  return (
    <article className="evaluation-case-row">
      <div>
        <strong>{readiness.title}</strong>
        <span>{readiness.caseId}</span>
      </div>
      <div className="evaluation-case-facts">
        <span>{readiness.status}</span>
        <span>{readiness.fixtureRequired ? 'fixture required' : 'fixture not required'}</span>
        <span>{readiness.fixtureExists ? 'fixture exists' : 'no fixture'}</span>
        <span>{readiness.adapterMatches ? 'adapter matches' : 'adapter not matched'}</span>
        <span>{readiness.expectedFilesExist ? 'expected files present' : 'expected files missing'}</span>
      </div>
      <div className="evaluation-case-detail">
        <span>Fixture</span>
        <p>{readiness.fixturePath}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Adapter</span>
        <p>{`${readiness.expectedLanguage}/${readiness.expectedBuildSystem} -> ${readiness.actualLanguage}/${readiness.actualBuildSystem}`}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Commands</span>
        <p>{`${commandLabel(readiness.expectedVerificationCommand)} -> ${commandLabel(readiness.actualVerificationCommand)}`}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Expected files</span>
        <p>{readiness.expectedChangedFiles.join(', ') || 'none'}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Missing files</span>
        <p>{readiness.missingExpectedFiles.join(', ') || 'none'}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Evidence</span>
        <p>{readiness.reason}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Next action</span>
        <p>{readiness.nextAction}</p>
      </div>
    </article>
  );
}

function EvaluationFixtureBaselineRow({ baselineCase }: { baselineCase: EvaluationFixtureBaselineCase }) {
  return (
    <article className="evaluation-case-row">
      <div>
        <strong>{baselineCase.title}</strong>
        <span>{baselineCase.caseId}</span>
      </div>
      <div className="evaluation-case-facts">
        <span>{baselineCase.status}</span>
        <span>{baselineCase.executed ? 'executed' : 'not executed'}</span>
        <span>{baselineCase.language}/{baselineCase.buildSystem}</span>
        <code>{commandLabel(baselineCase.verificationCommand)}</code>
        <span>exit {baselineCase.exitCode ?? 'none'}</span>
      </div>
      <div className="evaluation-case-detail">
        <span>Fixture</span>
        <p>{baselineCase.fixturePath}</p>
      </div>
      <div className="evaluation-case-detail">
        <span>Evidence</span>
        <p>{baselineCase.reason}</p>
      </div>
      {baselineCase.outputSnippet ? (
        <div className="evaluation-case-detail">
          <span>Output</span>
          <p>{baselineCase.outputSnippet}</p>
        </div>
      ) : null}
      <div className="evaluation-case-detail">
        <span>Next action</span>
        <p>{baselineCase.nextAction}</p>
      </div>
    </article>
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

function signedCount(value: number) {
  return value > 0 ? `+${value}` : String(value);
}

function listLabel(values: string[]) {
  return values.length > 0 ? values.join(', ') : 'none';
}

function baselineRunDigestLabel(run: EvaluationFixtureBaselineRunDigest | null) {
  if (!run) {
    return 'No archived run available';
  }
  return `${run.status} · ${run.passedCaseCount} passed · ${run.failedCaseCount} failed · ${run.skippedCaseCount} skipped`;
}

function evaluationRunDigestLabel(run: EvaluationRunArchiveDigest | null) {
  if (!run) {
    return 'No archived run available';
  }
  return `${run.status} · ${run.passedFixCaseCount} passed · ${run.failedFixCaseCount} failed · ${run.skippedCaseCount} skipped`;
}
