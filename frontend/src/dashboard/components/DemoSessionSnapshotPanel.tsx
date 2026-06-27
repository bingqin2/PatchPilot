import { Archive, Copy, Download } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoArchivedLaunchOutcome,
  DemoHandoffReadiness,
  DemoHandoffReadinessCheck,
  DemoHandoffPackageArchive,
  DemoPreparedLaunchCommand,
  DemoReadinessSnapshotTrendStatus,
  DemoReadinessStatus,
  DemoSessionArchive,
  DemoSessionReportInput,
  DemoSessionSnapshot
} from '../../types';
import { compactDateTime } from '../format';

interface DemoSessionSnapshotPanelProps {
  snapshot: DemoSessionSnapshot | null;
  preparedLaunchCommands: DemoPreparedLaunchCommand[];
  archivedLaunchOutcomes: DemoArchivedLaunchOutcome[];
  handoffReadiness: DemoHandoffReadiness | null;
  archives: DemoSessionArchive[];
  handoffPackageArchives: DemoHandoffPackageArchive[];
  error: string | null;
  handoffReadinessError?: string | null;
  archiveError: string | null;
  handoffPackageArchiveError: string | null;
  onCopyReport: (input: DemoSessionReportInput) => Promise<string>;
  onDownloadReport: (input: DemoSessionReportInput) => Promise<Blob>;
  onArchiveSession: (input: DemoSessionReportInput) => Promise<DemoSessionArchive>;
  onCopyHandoffPackage: (input: DemoSessionReportInput) => Promise<string>;
  onDownloadHandoffPackage: (input: DemoSessionReportInput) => Promise<Blob>;
  onArchiveHandoffPackage: (input: DemoSessionReportInput) => Promise<DemoHandoffPackageArchive>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadHandoffPackageArchiveReport: (archiveId: string) => Promise<Blob>;
}

export function DemoSessionSnapshotPanel({
  snapshot,
  preparedLaunchCommands,
  archivedLaunchOutcomes,
  handoffReadiness,
  archives,
  handoffPackageArchives,
  error,
  handoffReadinessError = null,
  archiveError,
  handoffPackageArchiveError,
  onCopyReport,
  onDownloadReport,
  onArchiveSession,
  onCopyHandoffPackage,
  onDownloadHandoffPackage,
  onArchiveHandoffPackage,
  onDownloadArchiveReport,
  onDownloadHandoffPackageArchiveReport
}: DemoSessionSnapshotPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const scriptStepCount = snapshot?.script.steps.length ?? 0;
  const reportInput = { preparedLaunchCommands, archivedLaunchOutcomes };

  async function copySessionReport() {
    try {
      const report = await onCopyReport(reportInput);
      await navigator.clipboard.writeText(report);
      setCopyStatus('Demo session report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadSessionReport() {
    if (!snapshot) {
      return;
    }
    try {
      const report = await onDownloadReport(reportInput);
      downloadMarkdown(report, `patchpilot-demo-session-${snapshot.sessionId}.md`);
      setDownloadStatus('Demo session report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyHandoffPackage() {
    try {
      const report = await onCopyHandoffPackage(reportInput);
      await navigator.clipboard.writeText(report);
      setCopyStatus('Demo handoff package copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadHandoffPackage() {
    if (!snapshot) {
      return;
    }
    try {
      const report = await onDownloadHandoffPackage(reportInput);
      downloadMarkdown(report, `patchpilot-demo-handoff-${snapshot.sessionId}.md`);
      setDownloadStatus('Demo handoff package downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archiveSession() {
    try {
      await onArchiveSession(reportInput);
      setArchiveStatus('Demo session archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function archiveHandoffPackage() {
    try {
      await onArchiveHandoffPackage(reportInput);
      setArchiveStatus('Demo handoff package archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function copyArchivedReport(archive: DemoSessionArchive) {
    try {
      await navigator.clipboard.writeText(archive.report);
      setCopyStatus('Archived session report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadArchivedReport(archive: DemoSessionArchive) {
    try {
      const report = await onDownloadArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-demo-session-${archive.id}.md`);
      setDownloadStatus('Archived session report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyArchivedHandoffPackage(archive: DemoHandoffPackageArchive) {
    try {
      await navigator.clipboard.writeText(archive.report);
      setCopyStatus('Archived handoff package copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadArchivedHandoffPackage(archive: DemoHandoffPackageArchive) {
    try {
      const report = await onDownloadHandoffPackageArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-demo-handoff-package-${archive.id}.md`);
      setDownloadStatus('Archived handoff package downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel demo-session-panel" aria-label="Demo session snapshot">
      <div className="panel-header">
        <div>
          <h2>Demo session snapshot</h2>
          <p>{snapshot?.summary ?? 'Loading demo session snapshot'}</p>
        </div>
        {snapshot ? (
          <div className="demo-session-header-meta">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(snapshot.status)}`}>
              {statusLabel(snapshot.status)}
            </span>
            <time dateTime={snapshot.generatedAt}>{compactDateTime(snapshot.generatedAt)}</time>
            <button className="secondary-button" type="button" onClick={() => void copySessionReport()}>
              <Copy size={14} />
              Copy session report
            </button>
            <button className="secondary-button" type="button" onClick={() => void downloadSessionReport()}>
              <Download size={14} />
              Download session report
            </button>
            <button className="secondary-button" type="button" onClick={() => void copyHandoffPackage()}>
              <Copy size={14} />
              Copy handoff package
            </button>
            <button className="secondary-button" type="button" onClick={() => void downloadHandoffPackage()}>
              <Download size={14} />
              Download handoff package
            </button>
            <button className="secondary-button" type="button" onClick={() => void archiveSession()}>
              <Archive size={14} />
              Archive session
            </button>
            <button className="secondary-button" type="button" onClick={() => void archiveHandoffPackage()}>
              <Archive size={14} />
              Archive handoff package
            </button>
            {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
            {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
            {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          </div>
        ) : null}
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo session snapshot unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Demo session archive unavailable</strong>
          <span>{archiveError}</span>
        </div>
      ) : null}

      {handoffReadinessError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff readiness unavailable</strong>
          <span>{handoffReadinessError}</span>
        </div>
      ) : null}

      {handoffPackageArchiveError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff package archive unavailable</strong>
          <span>{handoffPackageArchiveError}</span>
        </div>
      ) : null}

      {snapshot ? (
        <>
          <div className="demo-session-summary">
            <div>
              <span>Session</span>
              <strong>{snapshot.sessionId}</strong>
            </div>
            <div>
              <span>Share summary</span>
              <strong>{snapshot.shareSummary}</strong>
            </div>
          </div>

          <div className="demo-session-grid">
            <SnapshotFact
              label="Evidence status"
              value={statusLabel(snapshot.evidenceBundle.status)}
              detail={snapshot.evidenceBundle.summary}
            />
            <SnapshotFact
              label="Recent task"
              value={snapshot.evidenceBundle.recentTask?.id ?? 'None'}
              detail={snapshot.evidenceBundle.recentTask?.status ?? 'No task evidence'}
            />
            <SnapshotFact
              label="Recent Pull Request"
              value={snapshot.evidenceBundle.recentPullRequestUrl ?? 'Missing'}
              detail={snapshot.evidenceBundle.recentPullRequestUrl ? 'PR evidence available' : 'Run smoke task'}
            />
            <SnapshotFact
              label="Script"
              value={`${scriptStepCount} ${scriptStepCount === 1 ? 'step' : 'steps'}`}
              detail={snapshot.script.summary}
            />
            <SnapshotFact
              label="Readiness trend"
              value={trendStatusLabel(snapshot.readinessSnapshotTrend.status)}
              detail={snapshot.readinessSnapshotTrend.summary}
            />
            <SnapshotFact
              label="Trend delta"
              value={trendDelta(snapshot.readinessSnapshotTrend)}
              detail={snapshot.readinessSnapshotTrend.nextAction}
            />
            {handoffReadiness ? (
              <>
                <SnapshotFact
                  label="Handoff readiness"
                  value={statusLabel(handoffReadiness.status)}
                  detail={handoffReadiness.summary}
                />
                <SnapshotFact
                  label="Handoff evidence"
                  value={handoffEvidenceLabel(preparedLaunchCommands.length, archivedLaunchOutcomes.length)}
                  detail={handoffReadiness.nextAction}
                />
              </>
            ) : null}
          </div>

          {handoffReadiness ? <HandoffReadinessCheckList checks={handoffReadiness.checks} /> : null}

          <div className="demo-session-lists">
            <SnapshotList title="Operator checklist" items={snapshot.operatorChecklist} />
            <SnapshotList title="Health contract" items={snapshot.healthContract} />
            <SnapshotList title="Next actions" items={snapshot.nextActions} emptyText="No next actions recorded." />
          </div>

          <PreparedLaunchCommandList commands={preparedLaunchCommands} />
          <ArchivedLaunchOutcomeList outcomes={archivedLaunchOutcomes} />

          <div className="demo-session-archives">
            <h3>Recent session archives</h3>
            {archives.length ? (
              <ul>
                {archives.map((archive) => (
                  <li key={archive.id}>
                    <div>
                      <strong>{archive.id}</strong>
                      <span>{archive.sessionId}</span>
                      <small>{archive.shareSummary}</small>
                    </div>
                    <div className="demo-session-archive-actions">
                      <time dateTime={archive.createdAt}>{compactDateTime(archive.createdAt)}</time>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void copyArchivedReport(archive)}
                        aria-label={`Copy archived session report ${archive.id}`}
                      >
                        <Copy size={14} />
                        Copy report
                      </button>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void downloadArchivedReport(archive)}
                        aria-label={`Download archived session report ${archive.id}`}
                      >
                        <Download size={14} />
                        Download report
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo session archives recorded.</p>
            )}
          </div>

          <div className="demo-session-archives">
            <h3>Recent handoff package archives</h3>
            {handoffPackageArchives.length ? (
              <ul>
                {handoffPackageArchives.map((archive) => (
                  <li key={archive.id}>
                    <div>
                      <strong>{archive.id}</strong>
                      <span>{archive.sessionId}</span>
                      <small>{archive.shareSummary}</small>
                    </div>
                    <div className="demo-session-archive-actions">
                      <time dateTime={archive.createdAt}>{compactDateTime(archive.createdAt)}</time>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void copyArchivedHandoffPackage(archive)}
                        aria-label={`Copy archived handoff package ${archive.id}`}
                      >
                        <Copy size={14} />
                        Copy package
                      </button>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void downloadArchivedHandoffPackage(archive)}
                        aria-label={`Download archived handoff package ${archive.id}`}
                      >
                        <Download size={14} />
                        Download package
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo handoff package archives recorded.</p>
            )}
          </div>
        </>
      ) : (
        <div className="empty-state">Demo session snapshot has not loaded yet.</div>
      )}
    </section>
  );
}

function HandoffReadinessCheckList({ checks }: { checks: DemoHandoffReadinessCheck[] }) {
  return (
    <div className="demo-session-handoff-checks">
      <h3>Handoff readiness checks</h3>
      {checks.length ? (
        <ul>
          {checks.map((check) => (
            <li key={check.name}>
              <div>
                <strong>{check.name}</strong>
                <small>{check.summary}</small>
                <small>Next action: {check.nextAction}</small>
              </div>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                {statusLabel(check.status)}
              </span>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No handoff readiness checks recorded.</p>
      )}
    </div>
  );
}

function ArchivedLaunchOutcomeList({ outcomes }: { outcomes: DemoArchivedLaunchOutcome[] }) {
  return (
    <div className="demo-session-prepared-commands">
      <h3>Archived launch outcomes</h3>
      {outcomes.length ? (
        <ul>
          {outcomes.map((outcome) => (
            <li key={`${outcome.triggerComment}-${outcome.archivedAt}`}>
              <code>{outcome.triggerComment}</code>
              <span>
                {outcome.repositoryOwner}/{outcome.repositoryName} #{outcome.issueNumber} - {outcome.taskStatus}
              </span>
              {outcome.pullRequestUrl ? <small>{outcome.pullRequestUrl}</small> : null}
              <time dateTime={outcome.archivedAt}>{compactDateTime(outcome.archivedAt)}</time>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No archived launch outcomes recorded in this browser.</p>
      )}
    </div>
  );
}

interface SnapshotFactProps {
  label: string;
  value: string;
  detail: string;
}

function SnapshotFact({ label, value, detail }: SnapshotFactProps) {
  return (
    <div className="demo-session-fact">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </div>
  );
}

interface SnapshotListProps {
  title: string;
  items: string[];
  emptyText?: string;
}

function SnapshotList({ title, items, emptyText = 'No entries recorded.' }: SnapshotListProps) {
  return (
    <div>
      <h3>{title}</h3>
      {items.length ? (
        <ul>
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p className="empty-state">{emptyText}</p>
      )}
    </div>
  );
}

function PreparedLaunchCommandList({ commands }: { commands: DemoPreparedLaunchCommand[] }) {
  return (
    <div className="demo-session-prepared-commands">
      <h3>Prepared launch commands</h3>
      {commands.length ? (
        <ul>
          {commands.map((command) => (
            <li key={`${command.triggerComment}-${command.savedAt}`}>
              <code>{command.triggerComment}</code>
              <span>
                {command.repositoryOwner}/{command.repositoryName} #{command.issueNumber} - {command.operation} -{' '}
                {command.targetPath}
              </span>
              {command.replacementText ? <small>{command.replacementText}</small> : null}
              <time dateTime={command.savedAt}>{compactDateTime(command.savedAt)}</time>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No prepared launch commands recorded in this browser.</p>
      )}
    </div>
  );
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

function trendStatusLabel(status: DemoReadinessSnapshotTrendStatus) {
  switch (status) {
    case 'NO_BASELINE':
      return 'No baseline';
    case 'IMPROVING':
      return 'Improving';
    case 'STABLE':
      return 'Stable';
    case 'REGRESSING':
      return 'Regressing';
  }
}

function trendDelta(snapshotTrend: DemoSessionSnapshot['readinessSnapshotTrend']) {
  return `${signed(snapshotTrend.readyCheckDelta)} ready / ${signed(snapshotTrend.needsAttentionCheckDelta)} warning / ${signed(
    snapshotTrend.blockedCheckDelta
  )} blocked`;
}

function handoffEvidenceLabel(commandCount: number, outcomeCount: number) {
  return `${commandCount} ${plural(commandCount, 'command')} / ${outcomeCount} ${plural(outcomeCount, 'outcome')}`;
}

function plural(count: number, noun: string) {
  return count === 1 ? noun : `${noun}s`;
}

function signed(value: number) {
  return value > 0 ? `+${value}` : `${value}`;
}

function downloadMarkdown(blob: Blob, filename: string) {
  const objectUrl = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = objectUrl;
  anchor.download = safeFilename(filename);
  document.body.append(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(objectUrl);
}

function safeFilename(filename: string) {
  return filename.replace(/[^a-z0-9._-]+/gi, '-');
}
