import { Download, ExternalLink, FileText } from 'lucide-react';
import { useState } from 'react';
import type {
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary
} from '../../types';
import { compactTime } from '../format';

interface TaskEvidenceArchiveReviewPanelProps {
  summary: FixTaskEvidencePackageArchiveSummary | null;
  shareCenter: FixTaskEvidencePackageArchiveShareCenter | null;
  archives: FixTaskEvidencePackageArchive[];
  error: string | null;
  shareCenterError: string | null;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadShareCenterReport: () => Promise<Blob>;
  onSelectTask: (taskId: string) => void;
}

export function TaskEvidenceArchiveReviewPanel({
  summary,
  shareCenter,
  archives,
  error,
  shareCenterError,
  onDownloadArchiveReport,
  onDownloadShareCenterReport,
  onSelectTask
}: TaskEvidenceArchiveReviewPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

  async function downloadArchive(archive: FixTaskEvidencePackageArchive) {
    try {
      const report = await onDownloadArchiveReport(archive.id);
      downloadMarkdown(
        report,
        `patchpilot-task-${safeFilenamePart(archive.taskId)}-evidence-${safeFilenamePart(archive.id)}.md`
      );
      setDownloadStatus(`Archived evidence ${archive.id} downloaded`);
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadShareCenterReport() {
    try {
      const report = await onDownloadShareCenterReport();
      downloadMarkdown(report, 'patchpilot-task-evidence-share-center.md');
      setDownloadStatus('Task evidence share center report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel task-evidence-archive-review-panel" aria-label="Task evidence archive review">
      <div className="panel-header">
        <div>
          <h2>Task evidence archive review</h2>
          <p>{summary?.totalArchiveCount ?? archives.length} archived reports</p>
        </div>
        {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
      </div>

      {error ? <p className="panel-error">{error}</p> : null}
      {shareCenterError ? <p className="panel-error">{shareCenterError}</p> : null}

      {shareCenter ? (
        <section
          className={`task-evidence-share-center task-evidence-share-center-${shareCenter.status.toLowerCase().replace('_', '-')}`}
          aria-label="Task evidence share center"
        >
          <div className="task-evidence-share-center-heading">
            <div>
              <span>Task evidence share center</span>
              <strong>{formatShareCenterStatus(shareCenter.status)}</strong>
              <p>{shareCenter.summary}</p>
            </div>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadShareCenterReport()}
              aria-label="Download task evidence share center report"
            >
              <FileText size={14} />
              Download share center report
            </button>
          </div>
          <div className="task-evidence-share-center-grid">
            <div>
              <span>Shareable archive</span>
              <strong>{shareCenter.shareableArchiveId ?? 'None'}</strong>
              <p>{shareCenter.shareableTaskId ? `Task ${shareCenter.shareableTaskId}` : 'Archive a completed PR task'}</p>
            </div>
            <div>
              <span>Pull Request</span>
              <strong>{shareCenter.shareablePullRequestUrl ?? 'None'}</strong>
              <p>{shareCenter.shareReady ? 'Ready to share' : 'Missing completed PR evidence'}</p>
            </div>
            <div>
              <span>Latest archive</span>
              <strong>{shareCenter.latestArchiveId ?? 'None'}</strong>
              <p>{shareCenter.latestArchivedAt ? `archived ${compactTime(shareCenter.latestArchivedAt)}` : 'No archive yet'}</p>
            </div>
          </div>
          <div className="task-evidence-share-center-notes">
            <div>
              <span>Download actions</span>
              <ul>
                {shareCenter.downloadActions.map((action) => (
                  <li key={action}>{action}</li>
                ))}
              </ul>
            </div>
            <div>
              <span>Evidence notes</span>
              <ul>
                {shareCenter.evidenceNotes.map((note) => (
                  <li key={note}>{note}</li>
                ))}
              </ul>
            </div>
          </div>
          <p className="task-evidence-share-center-contract">{shareCenter.sideEffectContract}</p>
          <strong>{shareCenter.nextAction}</strong>
        </section>
      ) : null}

      <div className="task-evidence-archive-summary">
        <div>
          <span>Total</span>
          <strong>{summary?.totalArchiveCount ?? archives.length}</strong>
          <p>archived reports</p>
        </div>
        <div>
          <span>Completed</span>
          <strong>{summary?.completedArchiveCount ?? countStatus(archives, 'COMPLETED')}</strong>
          <p>completed</p>
        </div>
        <div>
          <span>Failed</span>
          <strong>{summary?.failedArchiveCount ?? countStatus(archives, 'FAILED')}</strong>
          <p>failed</p>
        </div>
        <div>
          <span>Latest task</span>
          <strong>{latestTaskLabel(summary)}</strong>
          <p>{summary?.latestArchivedAt ? `archived ${compactTime(summary.latestArchivedAt)}` : 'No archive yet'}</p>
        </div>
      </div>

      {summary ? (
        <div className="task-evidence-archive-contract">
          <p>{summary.sideEffectContract}</p>
          <strong>{summary.nextAction}</strong>
        </div>
      ) : null}

      {archives.length > 0 ? (
        <div className="task-evidence-archive-list">
          {archives.map((archive) => (
            <article className="task-evidence-archive-row" key={archive.id}>
              <div>
                <strong>{archive.id}</strong>
                <p>{archive.summary}</p>
                <span>
                  {archive.repositoryOwner}/{archive.repositoryName} #{archive.issueNumber} · {archive.status} · archived{' '}
                  {compactTime(archive.archivedAt)}
                </span>
              </div>
              <div className="task-evidence-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onSelectTask(archive.taskId)}
                  aria-label={`Open task ${archive.taskId}`}
                >
                  <ExternalLink size={14} />
                  Open task
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void downloadArchive(archive)}
                  aria-label={`Download archived evidence ${archive.id}`}
                >
                  <Download size={14} />
                  Download
                </button>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <p className="empty-state">No archived task evidence packages.</p>
      )}
    </section>
  );
}

function latestTaskLabel(summary: FixTaskEvidencePackageArchiveSummary | null) {
  if (!summary?.latestRepositoryOwner || !summary.latestRepositoryName || summary.latestIssueNumber == null) {
    return 'None';
  }
  return `${summary.latestRepositoryOwner}/${summary.latestRepositoryName} #${summary.latestIssueNumber}`;
}

function countStatus(archives: FixTaskEvidencePackageArchive[], status: string) {
  return archives.filter((archive) => archive.status === status).length;
}

function formatShareCenterStatus(status: FixTaskEvidencePackageArchiveShareCenter['status']) {
  return status
    .split('_')
    .map((part) => part.charAt(0) + part.slice(1).toLowerCase())
    .join(' ');
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}

function safeFilenamePart(value: string) {
  return value.replace(/[^A-Za-z0-9._-]/g, '-');
}
