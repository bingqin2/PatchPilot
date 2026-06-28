import { Download, ExternalLink } from 'lucide-react';
import { useState } from 'react';
import type { FixTaskEvidencePackageArchive, FixTaskEvidencePackageArchiveSummary } from '../../types';
import { compactTime } from '../format';

interface TaskEvidenceArchiveReviewPanelProps {
  summary: FixTaskEvidencePackageArchiveSummary | null;
  archives: FixTaskEvidencePackageArchive[];
  error: string | null;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onSelectTask: (taskId: string) => void;
}

export function TaskEvidenceArchiveReviewPanel({
  summary,
  archives,
  error,
  onDownloadArchiveReport,
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
