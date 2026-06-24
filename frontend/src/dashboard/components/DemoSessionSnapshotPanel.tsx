import { Archive, Copy } from 'lucide-react';
import { useState } from 'react';
import type { DemoReadinessStatus, DemoSessionArchive, DemoSessionSnapshot } from '../../types';
import { compactDateTime } from '../format';

interface DemoSessionSnapshotPanelProps {
  snapshot: DemoSessionSnapshot | null;
  archives: DemoSessionArchive[];
  error: string | null;
  archiveError: string | null;
  onCopyReport: () => Promise<string>;
  onArchiveSession: () => Promise<DemoSessionArchive>;
}

export function DemoSessionSnapshotPanel({
  snapshot,
  archives,
  error,
  archiveError,
  onCopyReport,
  onArchiveSession
}: DemoSessionSnapshotPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const scriptStepCount = snapshot?.script.steps.length ?? 0;

  async function copySessionReport() {
    try {
      const report = await onCopyReport();
      await navigator.clipboard.writeText(report);
      setCopyStatus('Demo session report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function archiveSession() {
    try {
      await onArchiveSession();
      setArchiveStatus('Demo session archived');
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
            <button className="secondary-button" type="button" onClick={() => void archiveSession()}>
              <Archive size={14} />
              Archive session
            </button>
            {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
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
          </div>

          <div className="demo-session-lists">
            <SnapshotList title="Operator checklist" items={snapshot.operatorChecklist} />
            <SnapshotList title="Health contract" items={snapshot.healthContract} />
            <SnapshotList title="Next actions" items={snapshot.nextActions} emptyText="No next actions recorded." />
          </div>

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
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo session archives recorded.</p>
            )}
          </div>
        </>
      ) : (
        <div className="empty-state">Demo session snapshot has not loaded yet.</div>
      )}
    </section>
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
