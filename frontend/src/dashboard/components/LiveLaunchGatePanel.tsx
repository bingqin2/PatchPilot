import { Archive, ClipboardCheck, Copy, Download, FileCheck2, PackageCheck, RefreshCw, Rocket } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  DemoLiveLaunchGate,
  DemoLiveDemoEvidenceBundle,
  DemoLiveDemoEvidenceBundleArchive,
  DemoLiveDemoHandoffPackage,
  DemoLiveTriggerLaunchPackage,
  DemoLiveTriggerLaunchPackageArchive,
  DemoLiveTriggerOutcomeCloseout,
  DemoLiveTriggerOutcomeCloseoutArchive,
  DemoLiveTriggerOutcomeCloseoutInput,
  GitHubTriggerDryRunInput
} from '../../types';

interface LiveLaunchGatePanelProps {
  result: DemoLiveLaunchGate | null;
  error: string | null;
  pending: boolean;
  onRunGate: (input: GitHubTriggerDryRunInput) => Promise<DemoLiveLaunchGate> | Promise<void> | void;
  launchPackage: DemoLiveTriggerLaunchPackage | null;
  launchPackageError: string | null;
  launchPackagePending: boolean;
  onCreateLaunchPackage: (
    input: GitHubTriggerDryRunInput
  ) => Promise<DemoLiveTriggerLaunchPackage> | Promise<void> | void;
  launchPackageArchives: DemoLiveTriggerLaunchPackageArchive[];
  launchPackageArchiveError: string | null;
  onArchiveLaunchPackage: (
    input: GitHubTriggerDryRunInput
  ) => Promise<DemoLiveTriggerLaunchPackageArchive> | Promise<void> | void;
  onDownloadLaunchPackageArchiveReport: (archiveId: string) => Promise<Blob>;
  outcomeCloseout: DemoLiveTriggerOutcomeCloseout | null;
  outcomeCloseoutError: string | null;
  outcomeCloseoutPending: boolean;
  onCreateOutcomeCloseout: (
    input: DemoLiveTriggerOutcomeCloseoutInput
  ) => Promise<DemoLiveTriggerOutcomeCloseout> | Promise<void> | void;
  onDownloadOutcomeCloseoutReport: (input: DemoLiveTriggerOutcomeCloseoutInput) => Promise<Blob>;
  outcomeCloseoutArchives: DemoLiveTriggerOutcomeCloseoutArchive[];
  outcomeCloseoutArchiveError: string | null;
  onArchiveOutcomeCloseout: (
    input: DemoLiveTriggerOutcomeCloseoutInput
  ) => Promise<DemoLiveTriggerOutcomeCloseoutArchive> | Promise<void> | void;
  onDownloadOutcomeCloseoutArchiveReport: (archiveId: string) => Promise<Blob>;
  liveDemoEvidenceBundle: DemoLiveDemoEvidenceBundle | null;
  liveDemoEvidenceBundleError: string | null;
  onRefreshLiveDemoEvidenceBundle: () => Promise<DemoLiveDemoEvidenceBundle> | Promise<void> | void;
  onDownloadLiveDemoEvidenceBundleReport: () => Promise<Blob>;
  liveDemoEvidenceBundleArchives: DemoLiveDemoEvidenceBundleArchive[];
  liveDemoEvidenceBundleArchiveError: string | null;
  onArchiveLiveDemoEvidenceBundle: () => Promise<DemoLiveDemoEvidenceBundleArchive> | Promise<void> | void;
  onDownloadLiveDemoEvidenceBundleArchiveReport: (archiveId: string) => Promise<Blob>;
  liveDemoHandoffPackage: DemoLiveDemoHandoffPackage | null;
  liveDemoHandoffPackageError: string | null;
  onRefreshLiveDemoHandoffPackage: () => Promise<DemoLiveDemoHandoffPackage> | Promise<void> | void;
  onDownloadLiveDemoHandoffPackageReport: () => Promise<Blob>;
}

export function LiveLaunchGatePanel({
  result,
  error,
  pending,
  onRunGate,
  launchPackage,
  launchPackageError,
  launchPackagePending,
  onCreateLaunchPackage,
  launchPackageArchives,
  launchPackageArchiveError,
  onArchiveLaunchPackage,
  onDownloadLaunchPackageArchiveReport,
  outcomeCloseout,
  outcomeCloseoutError,
  outcomeCloseoutPending,
  onCreateOutcomeCloseout,
  onDownloadOutcomeCloseoutReport,
  outcomeCloseoutArchives,
  outcomeCloseoutArchiveError,
  onArchiveOutcomeCloseout,
  onDownloadOutcomeCloseoutArchiveReport,
  liveDemoEvidenceBundle,
  liveDemoEvidenceBundleError,
  onRefreshLiveDemoEvidenceBundle,
  onDownloadLiveDemoEvidenceBundleReport,
  liveDemoEvidenceBundleArchives,
  liveDemoEvidenceBundleArchiveError,
  onArchiveLiveDemoEvidenceBundle,
  onDownloadLiveDemoEvidenceBundleArchiveReport,
  liveDemoHandoffPackage,
  liveDemoHandoffPackageError,
  onRefreshLiveDemoHandoffPackage,
  onDownloadLiveDemoHandoffPackageReport
}: LiveLaunchGatePanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [triggerComment, setTriggerComment] = useState('/agent fix touch docs/live-gate.md');

  function input(): GitHubTriggerDryRunInput {
    return {
      repositoryOwner: repositoryOwner.trim(),
      repositoryName: repositoryName.trim(),
      issueNumber: Number(issueNumber),
      triggerUser: triggerUser.trim(),
      triggerComment: triggerComment.trim()
    };
  }

  function closeoutInput(): DemoLiveTriggerOutcomeCloseoutInput {
    return {
      ...input(),
      launchPackageArchiveId: launchPackageArchives[0]?.id ?? null
    };
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onRunGate(input());
  }

  async function copyReport() {
    if (!result) {
      return;
    }
    await navigator.clipboard?.writeText(result.markdownReport);
  }

  async function createLaunchPackage() {
    await onCreateLaunchPackage(input());
  }

  async function archiveLaunchPackage() {
    await onArchiveLaunchPackage(input());
  }

  function downloadLaunchPackage() {
    if (!launchPackage) {
      return;
    }
    const blob = new Blob([launchPackage.markdownReport], { type: 'text/markdown;charset=UTF-8' });
    downloadMarkdown(blob, 'patchpilot-live-trigger-launch-package.md');
  }

  async function downloadLaunchPackageArchive(archiveId: string) {
    const blob = await onDownloadLaunchPackageArchiveReport(archiveId);
    downloadMarkdown(blob, `patchpilot-live-trigger-launch-package-archive-${archiveId}.md`);
  }

  async function createOutcomeCloseout() {
    await onCreateOutcomeCloseout(closeoutInput());
  }

  async function downloadOutcomeCloseout() {
    const blob = await onDownloadOutcomeCloseoutReport(closeoutInput());
    downloadMarkdown(blob, 'patchpilot-live-trigger-outcome-closeout.md');
  }

  async function archiveOutcomeCloseout() {
    await onArchiveOutcomeCloseout(closeoutInput());
  }

  async function downloadOutcomeCloseoutArchive(archiveId: string) {
    const blob = await onDownloadOutcomeCloseoutArchiveReport(archiveId);
    downloadMarkdown(blob, `patchpilot-live-trigger-outcome-closeout-archive-${archiveId}.md`);
  }

  async function refreshLiveDemoEvidenceBundle() {
    await onRefreshLiveDemoEvidenceBundle();
  }

  async function downloadLiveDemoEvidenceBundle() {
    const blob = await onDownloadLiveDemoEvidenceBundleReport();
    downloadMarkdown(blob, 'patchpilot-live-demo-evidence-bundle.md');
  }

  async function archiveLiveDemoEvidenceBundle() {
    await onArchiveLiveDemoEvidenceBundle();
  }

  async function downloadLiveDemoEvidenceBundleArchive(archiveId: string) {
    const blob = await onDownloadLiveDemoEvidenceBundleArchiveReport(archiveId);
    downloadMarkdown(blob, `patchpilot-live-demo-evidence-bundle-archive-${archiveId}.md`);
  }

  async function refreshLiveDemoHandoffPackage() {
    await onRefreshLiveDemoHandoffPackage();
  }

  async function downloadLiveDemoHandoffPackage() {
    const blob = await onDownloadLiveDemoHandoffPackageReport();
    downloadMarkdown(blob, 'patchpilot-live-demo-handoff-package.md');
  }

  return (
    <section className="panel live-launch-gate-panel" aria-label="Live launch gate">
      <div className="panel-header">
        <div>
          <h2>Live launch gate</h2>
          <p>Run the final read-only gate before posting a real GitHub issue comment.</p>
        </div>
        {result ? (
          <div className="demo-readiness-header-meta">
            <button className="secondary-button" type="button" onClick={() => void copyReport()}>
              <Copy size={16} />
              Copy launch gate report
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void createLaunchPackage()}
              disabled={launchPackagePending}
              aria-label="Create live trigger launch package"
            >
              <PackageCheck size={16} />
              {launchPackagePending ? 'Creating launch package' : 'Create launch package'}
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={downloadLaunchPackage}
              disabled={!launchPackage}
              aria-label="Download live trigger launch package"
            >
              <Download size={16} />
              Download package
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveLaunchPackage()}
              disabled={launchPackagePending}
              aria-label="Archive live trigger launch package"
            >
              <Archive size={16} />
              Archive package
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void createOutcomeCloseout()}
              disabled={outcomeCloseoutPending}
              aria-label="Generate live trigger outcome closeout"
            >
              <ClipboardCheck size={16} />
              {outcomeCloseoutPending ? 'Generating closeout' : 'Generate outcome closeout'}
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadOutcomeCloseout()}
              disabled={!outcomeCloseout}
              aria-label="Download live trigger outcome closeout"
            >
              <Download size={16} />
              Download closeout
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveOutcomeCloseout()}
              disabled={!outcomeCloseout || outcomeCloseoutPending}
              aria-label="Archive live trigger outcome closeout"
            >
              <Archive size={16} />
              Archive closeout
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoEvidenceBundle()}
              aria-label="Refresh live demo evidence bundle"
            >
              <RefreshCw size={16} />
              Refresh evidence bundle
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoEvidenceBundle()}
              disabled={!liveDemoEvidenceBundle}
              aria-label="Download live demo evidence bundle"
            >
              <FileCheck2 size={16} />
              Download evidence bundle
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveLiveDemoEvidenceBundle()}
              disabled={!liveDemoEvidenceBundle}
              aria-label="Archive live demo evidence bundle"
            >
              <Archive size={16} />
              Archive evidence bundle
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoHandoffPackage()}
              aria-label="Refresh live demo handoff package"
            >
              <RefreshCw size={16} />
              Refresh handoff package
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoHandoffPackage()}
              disabled={!liveDemoHandoffPackage}
              aria-label="Download live demo handoff package"
            >
              <FileCheck2 size={16} />
              Download handoff package
            </button>
          </div>
        ) : null}
      </div>

      <form className="demo-launch-preflight-form" aria-label="Live launch gate form" onSubmit={(event) => void submit(event)}>
        <label htmlFor="live-launch-gate-repository-owner">
          Repository owner
          <input
            id="live-launch-gate-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-repository-name">
          Repository name
          <input
            id="live-launch-gate-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-issue-number">
          Issue number
          <input
            id="live-launch-gate-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-launch-gate-trigger-user">
          Trigger user
          <input
            id="live-launch-gate-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label className="demo-launch-comment" htmlFor="live-launch-gate-comment">
          GitHub issue comment
          <input
            id="live-launch-gate-comment"
            value={triggerComment}
            onChange={(event) => setTriggerComment(event.target.value)}
            placeholder="/agent fix touch docs/live-gate.md"
            required
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={pending || triggerComment.trim().length === 0}
        >
          <Rocket size={16} />
          {pending ? 'Running launch gate' : 'Run live launch gate'}
        </button>
      </form>

      {error ? (
        <div className="adapter-api-error">
          <strong>Live launch gate failed</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {launchPackageError ? (
        <div className="adapter-api-error">
          <strong>Live trigger launch package failed</strong>
          <span>{launchPackageError}</span>
        </div>
      ) : null}

      {launchPackageArchiveError ? (
        <div className="adapter-api-error">
          <strong>Live trigger launch package archive failed</strong>
          <span>{launchPackageArchiveError}</span>
        </div>
      ) : null}

      {outcomeCloseoutError ? (
        <div className="adapter-api-error">
          <strong>Live trigger outcome closeout failed</strong>
          <span>{outcomeCloseoutError}</span>
        </div>
      ) : null}

      {outcomeCloseoutArchiveError ? (
        <div className="adapter-api-error">
          <strong>Live trigger outcome closeout archive failed</strong>
          <span>{outcomeCloseoutArchiveError}</span>
        </div>
      ) : null}

      {liveDemoEvidenceBundleError ? (
        <div className="adapter-api-error">
          <strong>Live demo evidence bundle failed</strong>
          <span>{liveDemoEvidenceBundleError}</span>
        </div>
      ) : null}

      {liveDemoEvidenceBundleArchiveError ? (
        <div className="adapter-api-error">
          <strong>Live demo evidence bundle archive failed</strong>
          <span>{liveDemoEvidenceBundleArchiveError}</span>
        </div>
      ) : null}

      {liveDemoHandoffPackageError ? (
        <div className="adapter-api-error">
          <strong>Live demo handoff package failed</strong>
          <span>{liveDemoHandoffPackageError}</span>
        </div>
      ) : null}

      {result ? (
        <>
          <LiveLaunchGateResult result={result} />
          {launchPackage ? <LiveTriggerLaunchPackageResult launchPackage={launchPackage} /> : null}
          <LiveTriggerLaunchPackageArchiveList
            archives={launchPackageArchives}
            onDownloadArchive={(archiveId) => void downloadLaunchPackageArchive(archiveId)}
          />
          {outcomeCloseout ? <LiveTriggerOutcomeCloseoutResult closeout={outcomeCloseout} /> : null}
          <LiveTriggerOutcomeCloseoutArchiveList
            archives={outcomeCloseoutArchives}
            onDownloadArchive={(archiveId) => void downloadOutcomeCloseoutArchive(archiveId)}
          />
          {liveDemoEvidenceBundle ? <LiveDemoEvidenceBundleResult bundle={liveDemoEvidenceBundle} /> : null}
          <LiveDemoEvidenceBundleArchiveList
            archives={liveDemoEvidenceBundleArchives}
            onDownloadArchive={(archiveId) => void downloadLiveDemoEvidenceBundleArchive(archiveId)}
          />
          {liveDemoHandoffPackage ? (
            <LiveDemoHandoffPackageResult handoffPackage={liveDemoHandoffPackage} />
          ) : null}
        </>
      ) : (
        <div className="empty-state">No live launch gate run yet.</div>
      )}
    </section>
  );
}

function LiveDemoHandoffPackageResult({ handoffPackage }: { handoffPackage: DemoLiveDemoHandoffPackage }) {
  const tone =
    handoffPackage.status === 'READY' ? 'ready' : handoffPackage.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {handoffPackage.status}
        </span>
        <strong>Live demo handoff package</strong>
        <p>{handoffPackage.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Evidence archive</span>
          <strong>
            {handoffPackage.evidenceBundleArchiveId
              ? `Archive ${handoffPackage.evidenceBundleArchiveId}`
              : 'missing'}
          </strong>
          <small>{handoffPackage.repository ?? 'repository missing'}</small>
        </div>
        <div>
          <span>Task</span>
          <strong>{handoffPackage.taskId ? `Task ${handoffPackage.taskId}` : 'missing'}</strong>
          <small>{handoffPackage.taskStatus ?? 'unknown'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{handoffPackage.pullRequestUrl ? `PR ${handoffPackage.pullRequestUrl}` : 'not created'}</strong>
        </div>
        <div>
          <span>Webhook</span>
          <strong>{handoffPackage.webhookDeliveryId ?? 'missing'}</strong>
          <small>Generated {handoffPackage.generatedAt}</small>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{handoffPackage.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Review checklist</h3>
        {handoffPackage.reviewChecklist.length === 0 ? (
          <p>No review checklist available.</p>
        ) : (
          <ul>
            {handoffPackage.reviewChecklist.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        )}
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Delivery instructions</h3>
        <ul>
          {handoffPackage.deliveryInstructions.map((instruction) => (
            <li key={instruction}>{instruction}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Evidence notes</h3>
        {handoffPackage.evidenceNotes.length === 0 ? (
          <p>No evidence notes recorded.</p>
        ) : (
          <ul>
            {handoffPackage.evidenceNotes.map((note) => (
              <li key={note}>{note}</li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

function LiveDemoEvidenceBundleArchiveList({
  archives,
  onDownloadArchive
}: {
  archives: DemoLiveDemoEvidenceBundleArchive[];
  onDownloadArchive: (archiveId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Recent live demo evidence bundle archives</h3>
      {archives.length === 0 ? (
        <p>No live demo evidence bundle archives recorded.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <strong>{archive.id}</strong>
              <span> {archive.status} </span>
              <span>{archive.taskId ? `Task ${archive.taskId}` : 'No matching task'}</span>
              {archive.pullRequestUrl ? <span> {archive.pullRequestUrl}</span> : null}
              <span>Archived at {archive.archivedAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download live demo evidence bundle archive ${archive.id}`}
                onClick={() => onDownloadArchive(archive.id)}
              >
                <Download size={14} />
                Download
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

function LiveDemoEvidenceBundleResult({ bundle }: { bundle: DemoLiveDemoEvidenceBundle }) {
  const tone = bundle.status === 'READY' ? 'ready' : bundle.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {bundle.status}
        </span>
        <strong>Live demo evidence bundle</strong>
        <p>{bundle.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Launch archive</span>
          <strong>{bundle.launchPackageArchiveId ? `Archive ${bundle.launchPackageArchiveId}` : 'missing'}</strong>
          <small>{bundle.launchPackageArchivedAt ?? 'not archived'}</small>
        </div>
        <div>
          <span>Outcome closeout</span>
          <strong>{bundle.outcomeCloseoutArchiveId ? `Archive ${bundle.outcomeCloseoutArchiveId}` : 'missing'}</strong>
          <small>{bundle.outcomeCloseoutArchivedAt ?? 'not archived'}</small>
        </div>
        <div>
          <span>Task</span>
          <strong>{bundle.taskId ? `Task ${bundle.taskId}` : 'missing'}</strong>
          <small>{bundle.taskStatus ?? 'unknown'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{bundle.pullRequestUrl ? `PR ${bundle.pullRequestUrl}` : 'not created'}</strong>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{bundle.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Bundle evidence</h3>
        <ul>
          {bundle.evidenceNotes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Bundle next actions</h3>
        <ul>
          {bundle.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function LiveTriggerLaunchPackageArchiveList({
  archives,
  onDownloadArchive
}: {
  archives: DemoLiveTriggerLaunchPackageArchive[];
  onDownloadArchive: (archiveId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Recent launch package archives</h3>
      {archives.length === 0 ? (
        <p>No launch package archives recorded.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <strong>{archive.id}</strong>
              <span> {archive.status} </span>
              <span>Archived at {archive.archivedAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download launch package archive ${archive.id}`}
                onClick={() => onDownloadArchive(archive.id)}
              >
                <Download size={14} />
                Download
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

function LiveTriggerOutcomeCloseoutArchiveList({
  archives,
  onDownloadArchive
}: {
  archives: DemoLiveTriggerOutcomeCloseoutArchive[];
  onDownloadArchive: (archiveId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Recent outcome closeout archives</h3>
      {archives.length === 0 ? (
        <p>No outcome closeout archives recorded.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <strong>{archive.id}</strong>
              <span> {archive.status} </span>
              <span>{archive.taskId ? `Task ${archive.taskId}` : 'No matching task'}</span>
              {archive.pullRequestUrl ? <span> {archive.pullRequestUrl}</span> : null}
              <span>Archived at {archive.archivedAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download outcome closeout archive ${archive.id}`}
                onClick={() => onDownloadArchive(archive.id)}
              >
                <Download size={14} />
                Download
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

function LiveTriggerOutcomeCloseoutResult({ closeout }: { closeout: DemoLiveTriggerOutcomeCloseout }) {
  const tone = closeout.status === 'READY' ? 'ready' : closeout.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {closeout.status}
        </span>
        <strong>Live trigger outcome closeout</strong>
        <p>{closeout.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Launch package archive</span>
          <strong>{closeout.launchPackageArchiveId ?? 'missing'}</strong>
          <small>{closeout.launchPackageStatus ?? 'unknown'}</small>
        </div>
        <div>
          <span>Task</span>
          <strong>{closeout.taskId ? `Task ${closeout.taskId}` : 'No matching task'}</strong>
          <small>{closeout.taskStatus ?? 'unknown'}</small>
        </div>
        <div>
          <span>Webhook delivery</span>
          <strong>{closeout.webhookDeliveryId ?? 'missing'}</strong>
          <small>{closeout.webhookDeliveryStatus ?? 'unknown'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{closeout.pullRequestUrl ?? 'not created'}</strong>
        </div>
      </div>
      {closeout.failureReason ? <p className="demo-launch-preflight-blocked">{closeout.failureReason}</p> : null}
      <p className="demo-launch-preflight-blocked">{closeout.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Outcome evidence</h3>
        <ul>
          {closeout.evidenceNotes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Outcome next actions</h3>
        <ul>
          {closeout.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function LiveTriggerLaunchPackageResult({ launchPackage }: { launchPackage: DemoLiveTriggerLaunchPackage }) {
  const tone = launchPackage.status === 'READY' ? 'ready' : launchPackage.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {launchPackage.status}
        </span>
        <strong>
          {launchPackage.readyToPost
            ? 'Ready to post live trigger'
            : launchPackage.status === 'BLOCKED'
              ? 'Live trigger package blocked'
              : 'Live trigger package needs attention'}
        </strong>
        <p>{launchPackage.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Live trigger launch package</span>
          <strong>{launchPackage.repository}</strong>
          <small>{launchPackage.issueUrl}</small>
        </div>
        <div>
          <span>Operator archive</span>
          <strong>{launchPackage.operatorHandoffArchiveId ?? 'missing'}</strong>
          <small>{launchPackage.operatorHandoffArchiveReady ? 'ready' : 'not ready'}</small>
        </div>
        <div>
          <span>Live gate</span>
          <strong>{launchPackage.liveLaunchGateStatus}</strong>
          <small>{launchPackage.liveLaunchGateReady ? 'ready to post' : 'not ready'}</small>
        </div>
        <div>
          <span>Exact comment</span>
          <strong>{launchPackage.triggerComment}</strong>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{launchPackage.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Package evidence</h3>
        <ul>
          {launchPackage.evidenceNotes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Package next actions</h3>
        <ul>
          {launchPackage.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function LiveLaunchGateResult({ result }: { result: DemoLiveLaunchGate }) {
  const tone = result.status === 'READY' ? 'ready' : result.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {result.status}
        </span>
        <strong>{result.readyToPost ? 'Ready to post' : result.status === 'BLOCKED' ? 'Blocked' : 'Needs attention'}</strong>
        <p>{result.summary}</p>
      </div>

      <div className="demo-launch-preflight-grid">
        <div>
          <span>Repository</span>
          <strong>{result.repository}</strong>
        </div>
        <div>
          <span>Issue</span>
          <strong>#{result.issueNumber}</strong>
        </div>
        <div>
          <span>Issue URL</span>
          <strong>{result.issueUrl}</strong>
        </div>
        <div>
          <span>Webhook payload</span>
          <strong>{result.webhookSetup.payloadUrl}</strong>
        </div>
        <div>
          <span>Default branch</span>
          <strong>{result.livePublishPreflight.defaultBranch ?? 'unknown'}</strong>
        </div>
        <div>
          <span>Trigger dry run</span>
          <strong>{result.triggerDryRun.status}</strong>
        </div>
      </div>

      <p className="demo-launch-preflight-blocked">{result.sideEffectContract}</p>

      <div className="operator-setup-grid">
        {result.checks.map((check) => (
          <div
            className={`operator-setup-check operator-setup-check-${check.status === 'READY' ? 'ready' : check.status === 'BLOCKED' ? 'blocked' : 'attention'}`}
            key={check.name}
          >
            <span>{check.name}</span>
            <strong>{check.status}</strong>
            <p>{check.message}</p>
            <small>{check.action}</small>
          </div>
        ))}
      </div>

      <div className="demo-launch-preflight-actions">
        <h3>Next actions</h3>
        <ul>
          {result.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
}
