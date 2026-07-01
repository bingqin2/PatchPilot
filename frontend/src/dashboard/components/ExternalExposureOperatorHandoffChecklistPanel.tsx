import { Archive, Download, RefreshCw } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoReadinessStatus,
  ExternalExposureOperatorHandoffChecklist,
  ExternalExposureOperatorHandoffChecklistArchive
} from '../../types';
import { compactDateTime } from '../format';

interface ExternalExposureOperatorHandoffChecklistPanelProps {
  checklist: ExternalExposureOperatorHandoffChecklist | null;
  archives: ExternalExposureOperatorHandoffChecklistArchive[];
  error: string | null;
  archiveError: string | null;
  onDownloadReport: () => Promise<Blob>;
  onArchiveChecklist: () => Promise<ExternalExposureOperatorHandoffChecklistArchive>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onRefresh: () => Promise<void> | void;
}

export function ExternalExposureOperatorHandoffChecklistPanel({
  checklist,
  archives,
  error,
  archiveError,
  onDownloadReport,
  onArchiveChecklist,
  onDownloadArchiveReport,
  onRefresh
}: ExternalExposureOperatorHandoffChecklistPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-external-exposure-operator-handoff-checklist.md');
      setDownloadStatus('Exposure handoff checklist downloaded');
    } catch {
      setDownloadStatus('Exposure handoff checklist download failed');
    }
  }

  async function archiveChecklist() {
    try {
      await onArchiveChecklist();
      setDownloadStatus('Exposure handoff checklist archived');
    } catch {
      setDownloadStatus('Exposure handoff checklist archive failed');
    }
  }

  async function downloadArchiveReport(archiveId: string) {
    try {
      const report = await onDownloadArchiveReport(archiveId);
      downloadMarkdown(report, `patchpilot-external-exposure-operator-handoff-checklist-${archiveId}.md`);
      setDownloadStatus('Exposure handoff checklist archive downloaded');
    } catch {
      setDownloadStatus('Exposure handoff checklist archive download failed');
    }
  }

  return (
    <section
      className="panel external-exposure-operator-handoff-checklist-panel"
      aria-label="External exposure operator handoff checklist"
    >
      <div className="panel-header">
        <div>
          <h2>External exposure handoff checklist</h2>
          <p>{checklist?.summary ?? 'Loading external exposure handoff checklist'}</p>
        </div>
        <div className="demo-readiness-header-meta">
          <button
            className="secondary-button"
            type="button"
            onClick={() => void downloadReport()}
            disabled={!checklist}
            aria-label="Download exposure handoff checklist"
          >
            <Download size={16} />
            Download
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void archiveChecklist()}
            disabled={!checklist}
            aria-label="Archive exposure handoff checklist"
          >
            <Archive size={16} />
            Archive
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void onRefresh()}
            aria-label="Refresh exposure handoff checklist"
          >
            <RefreshCw size={16} />
            Refresh
          </button>
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure handoff checklist unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Exposure handoff checklist archives unavailable</strong>
          <span>{archiveError}</span>
        </div>
      ) : null}

      {checklist ? (
        <>
          <div className="demo-readiness-summary">
            <div>
              <span>Status</span>
              <strong>{checklist.readyForNextLiveStep ? 'Ready for next live step' : blockedLabel(checklist.status)}</strong>
              <small>{checklist.nextAction}</small>
            </div>
            <div>
              <span>Repository</span>
              <strong>{checklist.repository || 'Not configured'}</strong>
              <small>Live publish: {checklist.livePublishStatus ?? 'missing'}</small>
            </div>
            <div>
              <span>Closeout archive</span>
              <strong>{checklist.latestCloseoutArchiveId ?? 'No archive'}</strong>
              <small>Freshness: {checklist.archiveFreshness ?? 'missing'}</small>
            </div>
            <div>
              <span>Session</span>
              <strong>{checklist.latestSessionId ?? 'No session'}</strong>
              <small>{checklist.latestSessionStatus ?? 'missing'}</small>
            </div>
            <div>
              <span>Active sessions</span>
              <strong>{checklist.activeSessionCount} active sessions</strong>
              <small>{checklist.blockedCount} blocked</small>
            </div>
          </div>

          <div className="demo-evidence-grid">
            <div>
              <span>Public URL</span>
              <strong>{checklist.publicUrl ?? 'No public URL'}</strong>
              <small>{checklist.webhookUrl ?? 'No webhook URL'}</small>
            </div>
            <div>
              <span>Handoff</span>
              <strong>{checklist.handoffStatus ?? 'missing'}</strong>
              <small>{checklist.livePublishReady ? 'Publish preflight ready' : 'Publish preflight not ready'}</small>
            </div>
            <div>
              <span>Generated</span>
              <strong>{compactDateTime(checklist.generatedAt)}</strong>
              <small>
                {checklist.readyCount} ready, {checklist.needsAttentionCount} attention, {checklist.blockedCount} blocked
              </small>
            </div>
          </div>

          <div className="demo-launch-preflight-actions">
            <h3>Checklist checks</h3>
            <div className="demo-evidence-records">
              {checklist.checks.map((check) => (
                <div key={check.name}>
                  <span>{check.name}</span>
                  <strong>{statusLabel(check.status)}</strong>
                  <small>{check.summary}</small>
                  <small>{check.nextAction}</small>
                </div>
              ))}
            </div>
          </div>

          <ListBlock title="Evidence notes" items={checklist.evidenceNotes} />
          <ListBlock title="Next actions" items={checklist.nextActions} />
          <ListBlock title="Download actions" items={checklist.downloadActions} />
          <div className="demo-launch-preflight-actions">
            <h3>Recent checklist archives</h3>
            {archives.length > 0 ? (
              <div className="demo-evidence-records">
                {archives.map((archive) => (
                  <div key={archive.id}>
                    <span>{archive.readyForNextLiveStep ? 'Ready archive' : blockedLabel(archive.status)}</span>
                    <strong>{archive.id}</strong>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <small>
                      Closeout {archive.latestCloseoutArchiveId ?? 'missing'} · publish{' '}
                      {archive.livePublishStatus ?? 'missing'}
                    </small>
                    <button
                      className="text-button"
                      type="button"
                      onClick={() => void downloadArchiveReport(archive.id)}
                      aria-label={`Download exposure handoff checklist archive ${archive.id}`}
                    >
                      Download archive
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">No external exposure handoff checklist archives recorded.</div>
            )}
          </div>
          <small>{checklist.sideEffectContract}</small>
        </>
      ) : (
        <div className="empty-state">No external exposure handoff checklist loaded.</div>
      )}
    </section>
  );
}

function ListBlock({ title, items }: { title: string; items: string[] }) {
  if (items.length === 0) {
    return null;
  }
  return (
    <div className="demo-launch-preflight-actions">
      <h3>{title}</h3>
      <ul className="demo-action-list">
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

function statusLabel(status: DemoReadinessStatus) {
  if (status === 'READY') {
    return 'Ready';
  }
  if (status === 'BLOCKED') {
    return 'Blocked';
  }
  return 'Needs attention';
}

function blockedLabel(status: DemoReadinessStatus) {
  return status === 'BLOCKED' ? 'Blocked before next live step' : statusLabel(status);
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
}
