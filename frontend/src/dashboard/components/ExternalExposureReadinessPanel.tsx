import { Archive, Copy, Download, Play, RefreshCw, Square } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  DemoReadinessStatus,
  ExternalExposureCloseout,
  ExternalExposureHandoffPackage,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive,
  ExternalExposureReadinessCheck,
  ExternalExposureSession,
  ExternalExposureSessionCloseInput,
  ExternalExposureSessionInput
} from '../../types';
import { compactDateTime } from '../format';

interface ExternalExposureReadinessPanelProps {
  readiness: ExternalExposureReadiness | null;
  error: string | null;
  archives: ExternalExposureReadinessArchive[];
  archiveError: string | null;
  handoffPackage: ExternalExposureHandoffPackage | null;
  handoffPackageError: string | null;
  sessions: ExternalExposureSession[];
  sessionError: string | null;
  closeout: ExternalExposureCloseout | null;
  closeoutError: string | null;
  onArchiveReadiness: () => Promise<ExternalExposureReadinessArchive>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadHandoffPackageReport: () => Promise<Blob>;
  onStartSession: (input: ExternalExposureSessionInput) => Promise<ExternalExposureSession>;
  onCloseSession: (sessionId: string, input: ExternalExposureSessionCloseInput) => Promise<ExternalExposureSession>;
  onDownloadSessionReport: (sessionId: string) => Promise<Blob>;
  onDownloadCloseoutReport: () => Promise<Blob>;
  onRefresh: () => Promise<void> | void;
}

export function ExternalExposureReadinessPanel({
  readiness,
  error,
  archives,
  archiveError,
  handoffPackage,
  handoffPackageError,
  sessions,
  sessionError,
  closeout,
  closeoutError,
  onArchiveReadiness,
  onDownloadArchiveReport,
  onDownloadHandoffPackageReport,
  onStartSession,
  onCloseSession,
  onDownloadSessionReport,
  onDownloadCloseoutReport,
  onRefresh
}: ExternalExposureReadinessPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [sessionStatus, setSessionStatus] = useState<string | null>(null);
  const [publicUrl, setPublicUrl] = useState('');
  const [webhookUrl, setWebhookUrl] = useState('');
  const [purpose, setPurpose] = useState('');
  const [operator, setOperator] = useState('');

  async function copyReport() {
    if (!readiness) {
      return;
    }
    try {
      await navigator.clipboard?.writeText(readiness.markdownReport);
      setCopyStatus('Exposure report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function archiveReadiness() {
    try {
      await onArchiveReadiness();
      setArchiveStatus('Exposure readiness archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function downloadArchiveReport(archiveId: string) {
    try {
      const report = await onDownloadArchiveReport(archiveId);
      downloadMarkdown(report, `patchpilot-external-exposure-readiness-${archiveId}.md`);
      setDownloadStatus('Exposure readiness archive downloaded');
    } catch {
      setDownloadStatus('Archive download failed');
    }
  }

  async function downloadHandoffPackageReport() {
    try {
      const report = await onDownloadHandoffPackageReport();
      downloadMarkdown(report, 'patchpilot-external-exposure-handoff-package.md');
      setDownloadStatus('Exposure handoff package downloaded');
    } catch {
      setDownloadStatus('Handoff package download failed');
    }
  }

  async function startSession(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      await onStartSession({
        publicUrl,
        webhookUrl,
        purpose,
        operator,
        expectedShutdownAt: undefined,
        notes: ''
      });
      setSessionStatus('Exposure session started');
    } catch {
      setSessionStatus('Exposure session start failed');
    }
  }

  async function closeSession(sessionId: string) {
    try {
      await onCloseSession(sessionId, {
        closedBy: operator || 'local-operator',
        closedAt: undefined,
        closeNotes: 'Closed from dashboard.'
      });
      setSessionStatus('Exposure session closed');
    } catch {
      setSessionStatus('Exposure session close failed');
    }
  }

  async function downloadSessionReport(sessionId: string) {
    try {
      const report = await onDownloadSessionReport(sessionId);
      downloadMarkdown(report, `patchpilot-external-exposure-session-${sessionId}.md`);
      setSessionStatus('Exposure session report downloaded');
    } catch {
      setSessionStatus('Exposure session download failed');
    }
  }

  async function downloadCloseoutReport() {
    try {
      const report = await onDownloadCloseoutReport();
      downloadMarkdown(report, 'patchpilot-external-exposure-closeout.md');
      setDownloadStatus('Exposure closeout report downloaded');
    } catch {
      setDownloadStatus('Exposure closeout download failed');
    }
  }

  return (
    <section className="panel external-exposure-readiness-panel" aria-label="External exposure readiness">
      <div className="panel-header">
        <div>
          <h2>External exposure readiness</h2>
          <p>{readiness?.summary ?? 'Loading temporary public URL safety gate'}</p>
        </div>
        <div className="demo-readiness-header-meta">
          {readiness ? (
            <button className="secondary-button" type="button" onClick={() => void copyReport()}>
              <Copy size={16} />
              Copy exposure report
            </button>
          ) : null}
          <button
            className="secondary-button"
            type="button"
            onClick={() => void archiveReadiness()}
            aria-label="Archive exposure readiness"
            disabled={!readiness}
          >
            <Archive size={16} />
            Archive
          </button>
          <button className="secondary-button" type="button" onClick={() => void onRefresh()}>
            <RefreshCw size={16} />
            Refresh exposure gate
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          {sessionStatus ? <span className="copy-status">{sessionStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure readiness unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {readiness ? (
        <ExternalExposureReadinessResult readiness={readiness} />
      ) : (
        <div className="empty-state">No external exposure readiness loaded.</div>
      )}

      <ExternalExposureHandoffPackageResult
        handoffPackage={handoffPackage}
        error={handoffPackageError}
        onDownloadReport={downloadHandoffPackageReport}
      />

      <ExternalExposureSessionResult
        sessions={sessions}
        error={sessionError}
        publicUrl={publicUrl}
        webhookUrl={webhookUrl}
        purpose={purpose}
        operator={operator}
        onPublicUrlChange={setPublicUrl}
        onWebhookUrlChange={setWebhookUrl}
        onPurposeChange={setPurpose}
        onOperatorChange={setOperator}
        onStartSession={startSession}
        onCloseSession={closeSession}
        onDownloadSessionReport={downloadSessionReport}
      />

      <ExternalExposureCloseoutResult
        closeout={closeout}
        error={closeoutError}
        onDownloadReport={downloadCloseoutReport}
      />

      <div className="demo-launch-preflight-actions">
        <h3>Recent exposure readiness archives</h3>
        {archiveError ? (
          <div className="adapter-api-error">
            <strong>Exposure readiness archive unavailable</strong>
            <span>{archiveError}</span>
          </div>
        ) : null}
        {archives.length === 0 ? (
          <p className="empty-state">No exposure readiness archives recorded.</p>
        ) : (
          <div className="demo-evidence-records">
            {archives.map((archive) => (
              <div key={archive.id}>
                <span>{archive.id}</span>
                <strong>{archive.status}</strong>
                <small>{archive.safeToExpose ? 'Safe to expose' : 'Not safe to expose'}</small>
                <small>{archive.summary}</small>
                <small>
                  {archive.readyCount} ready, {archive.needsAttentionCount} attention, {archive.blockedCount} blocked
                </small>
                <small>{compactDateTime(archive.createdAt)}</small>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void downloadArchiveReport(archive.id)}
                  aria-label={`Download exposure readiness archive ${archive.id}`}
                >
                  <Download size={14} />
                  Download archive
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

function ExternalExposureSessionResult({
  sessions,
  error,
  publicUrl,
  webhookUrl,
  purpose,
  operator,
  onPublicUrlChange,
  onWebhookUrlChange,
  onPurposeChange,
  onOperatorChange,
  onStartSession,
  onCloseSession,
  onDownloadSessionReport
}: {
  sessions: ExternalExposureSession[];
  error: string | null;
  publicUrl: string;
  webhookUrl: string;
  purpose: string;
  operator: string;
  onPublicUrlChange: (value: string) => void;
  onWebhookUrlChange: (value: string) => void;
  onPurposeChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onStartSession: (event: FormEvent<HTMLFormElement>) => void;
  onCloseSession: (sessionId: string) => Promise<void>;
  onDownloadSessionReport: (sessionId: string) => Promise<void>;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <div className="panel-subheader">
        <div>
          <h3>External exposure sessions</h3>
          <p>Record temporary public URL sharing and shutdown evidence.</p>
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure sessions unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      <form className="manual-task-form compact-form" onSubmit={onStartSession}>
        <label>
          <span>Temporary public URL</span>
          <input
            aria-label="Temporary public URL"
            value={publicUrl}
            onChange={(event) => onPublicUrlChange(event.target.value)}
            placeholder="https://example.trycloudflare.com"
          />
        </label>
        <label>
          <span>GitHub webhook URL</span>
          <input
            aria-label="GitHub webhook URL"
            value={webhookUrl}
            onChange={(event) => onWebhookUrlChange(event.target.value)}
            placeholder="https://example.trycloudflare.com/api/github/webhook"
          />
        </label>
        <label>
          <span>Exposure purpose</span>
          <input
            aria-label="Exposure purpose"
            value={purpose}
            onChange={(event) => onPurposeChange(event.target.value)}
            placeholder="Live GitHub webhook smoke test"
          />
        </label>
        <label>
          <span>Exposure operator</span>
          <input
            aria-label="Exposure operator"
            value={operator}
            onChange={(event) => onOperatorChange(event.target.value)}
            placeholder="bingqin2"
          />
        </label>
        <button className="secondary-button" type="submit">
          <Play size={14} />
          Start exposure session
        </button>
      </form>

      {sessions.length === 0 ? (
        <p className="empty-state">No external exposure sessions recorded.</p>
      ) : (
        <div className="demo-evidence-records">
          {sessions.map((session) => (
            <div key={session.id}>
              <span>{session.id}</span>
              <strong>{session.status}</strong>
              <small>{session.publicUrl}</small>
              <small>{session.webhookUrl}</small>
              <small>{session.purpose}</small>
              <small>
                Started {compactDateTime(session.startedAt)}
                {session.closedAt ? ` · Closed ${compactDateTime(session.closedAt)}` : ''}
              </small>
              <small>Linked archive {session.linkedReadinessArchiveId ?? 'missing'}</small>
              <button
                className="secondary-button"
                type="button"
                onClick={() => void onDownloadSessionReport(session.id)}
                aria-label={`Download exposure session ${session.id}`}
              >
                <Download size={14} />
                Download session
              </button>
              {session.status === 'ACTIVE' ? (
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void onCloseSession(session.id)}
                  aria-label={`Close exposure session ${session.id}`}
                >
                  <Square size={14} />
                  Close session
                </button>
              ) : null}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function ExternalExposureCloseoutResult({
  closeout,
  error,
  onDownloadReport
}: {
  closeout: ExternalExposureCloseout | null;
  error: string | null;
  onDownloadReport: () => Promise<void>;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <div className="panel-subheader">
        <div>
          <h3>External exposure closeout</h3>
          <p>{closeout?.summary ?? 'Loading exposure shutdown evidence'}</p>
        </div>
        <button
          className="secondary-button"
          type="button"
          onClick={() => void onDownloadReport()}
          aria-label="Download exposure closeout report"
          disabled={!closeout}
        >
          <Download size={14} />
          Download closeout
        </button>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure closeout unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {closeout ? (
        <div className={`demo-launch-preflight-result demo-launch-preflight-result-${statusClass(closeout.status)}`}>
          <div className="demo-launch-preflight-summary">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(closeout.status)}`}>
              {closeout.status}
            </span>
            <strong>{closeout.closeoutReady ? 'Closeout complete' : 'Closeout incomplete'}</strong>
            <p>{closeout.nextAction}</p>
            <small>
              Generated {compactDateTime(closeout.generatedAt)} · {closeout.totalCount} checks
            </small>
          </div>

          <div className="demo-launch-preflight-grid">
            <div>
              <span>Latest session</span>
              <strong>{closeout.latestSessionId ?? 'Missing'}</strong>
            </div>
            <div>
              <span>Session status</span>
              <strong>{closeout.latestSessionStatus ?? 'Missing'}</strong>
            </div>
            <div>
              <span>Linked archive</span>
              <strong>{closeout.linkedReadinessArchiveId ?? 'Missing'}</strong>
            </div>
            <div>
              <span>Handoff</span>
              <strong>{closeout.handoffStatus ?? 'Missing'}</strong>
            </div>
          </div>

          <div className="demo-launch-preflight-grid">
            <div>
              <span>Ready</span>
              <strong>{closeout.readyCount} ready</strong>
            </div>
            <div>
              <span>Needs attention</span>
              <strong>{closeout.needsAttentionCount} warning</strong>
            </div>
            <div>
              <span>Blocked</span>
              <strong>{closeout.blockedCount} blocked</strong>
            </div>
            <div>
              <span>Archive freshness</span>
              <strong>{closeout.archiveFreshness ?? 'Missing'}</strong>
            </div>
          </div>

          <p className="demo-launch-preflight-blocked">{closeout.sideEffectContract}</p>

          <div className="demo-evidence-records">
            {closeout.evidenceNotes.map((note) => (
              <div key={note}>
                <span>Evidence</span>
                <small>{note}</small>
              </div>
            ))}
          </div>

          <div className="demo-launch-preflight-actions">
            <h4>Next actions</h4>
            <ul>
              {closeout.nextActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </div>
      ) : (
        <p className="empty-state">No external exposure closeout loaded.</p>
      )}
    </div>
  );
}

function ExternalExposureReadinessResult({ readiness }: { readiness: ExternalExposureReadiness }) {
  const tone = statusClass(readiness.status);

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {readiness.status}
        </span>
        <strong>{readiness.safeToExpose ? 'Safe to expose' : 'Not safe to expose'}</strong>
        <p>{readiness.summary}</p>
        <small>
          Generated {compactDateTime(readiness.generatedAt)} · {readiness.totalCount} checks
        </small>
      </div>

      <div className="demo-launch-preflight-grid">
        <div>
          <span>Ready</span>
          <strong>{readiness.readyCount} ready</strong>
        </div>
        <div>
          <span>Needs attention</span>
          <strong>{readiness.needsAttentionCount} warning</strong>
        </div>
        <div>
          <span>Blocked</span>
          <strong>{readiness.blockedCount} blocked</strong>
        </div>
        <div>
          <span>Total</span>
          <strong>{readiness.totalCount} checks</strong>
        </div>
      </div>

      <p className="demo-launch-preflight-blocked">{readiness.sideEffectContract}</p>

      <div className="operator-setup-grid">
        {readiness.checks.map((check) => (
          <ExternalExposureCheckCard check={check} key={check.name} />
        ))}
      </div>

      <div className="demo-launch-preflight-actions">
        <h3>Next actions</h3>
        <ul>
          {readiness.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function ExternalExposureHandoffPackageResult({
  handoffPackage,
  error,
  onDownloadReport
}: {
  handoffPackage: ExternalExposureHandoffPackage | null;
  error: string | null;
  onDownloadReport: () => Promise<void>;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <div className="panel-subheader">
        <div>
          <h3>External exposure handoff package</h3>
          <p>{handoffPackage?.summary ?? 'Loading shareable exposure handoff evidence'}</p>
        </div>
        <button
          className="secondary-button"
          type="button"
          onClick={() => void onDownloadReport()}
          aria-label="Download exposure handoff package"
          disabled={!handoffPackage}
        >
          <Download size={14} />
          Download handoff
        </button>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure handoff package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {handoffPackage ? (
        <div className={`demo-launch-preflight-result demo-launch-preflight-result-${statusClass(handoffPackage.status)}`}>
          <div className="demo-launch-preflight-summary">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(handoffPackage.status)}`}>
              {handoffPackage.status}
            </span>
            <strong>{handoffPackage.handoffReady ? 'Ready to share' : 'Not ready to share'}</strong>
            <p>{handoffPackage.nextAction}</p>
            <small>
              Generated {compactDateTime(handoffPackage.generatedAt)} · Archive freshness: {handoffPackage.archiveFreshness}
            </small>
          </div>

          <div className="demo-launch-preflight-grid">
            <div>
              <span>Current readiness</span>
              <strong>{handoffPackage.readinessStatus}</strong>
            </div>
            <div>
              <span>Current safety</span>
              <strong>{handoffPackage.readinessSafeToExpose ? 'Safe to expose' : 'Not safe to expose'}</strong>
            </div>
            <div>
              <span>Latest archive</span>
              <strong>{handoffPackage.latestArchiveId ?? 'Missing'}</strong>
            </div>
            <div>
              <span>Archived safety</span>
              <strong>
                {handoffPackage.latestArchiveSafeToExpose === null
                  ? 'No archive'
                  : handoffPackage.latestArchiveSafeToExpose
                    ? 'Safe to expose'
                    : 'Not safe to expose'}
              </strong>
            </div>
          </div>

          <p className="demo-launch-preflight-blocked">{handoffPackage.sideEffectContract}</p>

          <div className="demo-evidence-records">
            {handoffPackage.evidenceNotes.map((note) => (
              <div key={note}>
                <span>Evidence</span>
                <small>{note}</small>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p className="empty-state">No external exposure handoff package loaded.</p>
      )}
    </div>
  );
}

function ExternalExposureCheckCard({ check }: { check: ExternalExposureReadinessCheck }) {
  const tone = statusClass(check.status);

  return (
    <div className={`operator-setup-check operator-setup-check-${tone}`}>
      <span>{check.status}</span>
      <strong>{check.name}</strong>
      <p>{check.summary}</p>
      <small>{check.nextAction}</small>
    </div>
  );
}

function statusClass(status: DemoReadinessStatus) {
  if (status === 'READY') {
    return 'ready';
  }
  if (status === 'BLOCKED') {
    return 'blocked';
  }
  return 'attention';
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
