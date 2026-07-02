import { Archive, ClipboardCheck, Copy, Download, FileCheck2, PackageCheck, RefreshCw, Rocket } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  DemoLiveLaunchGate,
  DemoLiveDemoArtifactChainReport,
  DemoLiveDemoReplayPackage,
  DemoLiveDemoReviewerDeliveryCenter,
  DemoLiveDemoCompletionCertificate,
  DemoLiveDemoCompletionCertificateArchive,
  DemoLiveDemoEvidenceBundle,
  DemoLiveDemoEvidenceBundleArchive,
  DemoLiveDemoHandoffDeliveryFinalization,
  DemoLiveDemoHandoffDeliveryFinalizationArchive,
  DemoLiveDemoHandoffDeliveryReceipt,
  DemoLiveDemoHandoffDeliveryReceiptInput,
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
  liveDemoHandoffDeliveryReceipts: DemoLiveDemoHandoffDeliveryReceipt[];
  liveDemoHandoffDeliveryReceiptError: string | null;
  onRecordLiveDemoHandoffDeliveryReceipt: (
    input: DemoLiveDemoHandoffDeliveryReceiptInput
  ) => Promise<DemoLiveDemoHandoffDeliveryReceipt> | Promise<void> | void;
  onDownloadLiveDemoHandoffDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  liveDemoHandoffDeliveryFinalization: DemoLiveDemoHandoffDeliveryFinalization | null;
  liveDemoHandoffDeliveryFinalizationError: string | null;
  onRefreshLiveDemoHandoffDeliveryFinalization:
    () => Promise<DemoLiveDemoHandoffDeliveryFinalization> | Promise<void> | void;
  onDownloadLiveDemoHandoffDeliveryFinalizationReport: () => Promise<Blob>;
  liveDemoHandoffDeliveryFinalizationArchives: DemoLiveDemoHandoffDeliveryFinalizationArchive[];
  liveDemoHandoffDeliveryFinalizationArchiveError: string | null;
  onArchiveLiveDemoHandoffDeliveryFinalization:
    () => Promise<DemoLiveDemoHandoffDeliveryFinalizationArchive> | Promise<void> | void;
  onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport: (archiveId: string) => Promise<Blob>;
  liveDemoCompletionCertificate: DemoLiveDemoCompletionCertificate | null;
  liveDemoCompletionCertificateError: string | null;
  onRefreshLiveDemoCompletionCertificate:
    () => Promise<DemoLiveDemoCompletionCertificate> | Promise<void> | void;
  onDownloadLiveDemoCompletionCertificateReport: () => Promise<Blob>;
  liveDemoCompletionCertificateArchives: DemoLiveDemoCompletionCertificateArchive[];
  liveDemoCompletionCertificateArchiveError: string | null;
  onArchiveLiveDemoCompletionCertificate:
    () => Promise<DemoLiveDemoCompletionCertificateArchive> | Promise<void> | void;
  onDownloadLiveDemoCompletionCertificateArchiveReport: (archiveId: string) => Promise<Blob>;
  liveDemoArtifactChainReport: DemoLiveDemoArtifactChainReport | null;
  liveDemoArtifactChainReportError: string | null;
  onRefreshLiveDemoArtifactChainReport:
    () => Promise<DemoLiveDemoArtifactChainReport> | Promise<void> | void;
  onDownloadLiveDemoArtifactChainReport: () => Promise<Blob>;
  liveDemoReplayPackage: DemoLiveDemoReplayPackage | null;
  liveDemoReplayPackageError: string | null;
  onRefreshLiveDemoReplayPackage: () => Promise<DemoLiveDemoReplayPackage> | Promise<void> | void;
  onDownloadLiveDemoReplayPackage: () => Promise<Blob>;
  liveDemoReviewerDeliveryCenter: DemoLiveDemoReviewerDeliveryCenter | null;
  liveDemoReviewerDeliveryCenterError: string | null;
  onRefreshLiveDemoReviewerDeliveryCenter:
    () => Promise<DemoLiveDemoReviewerDeliveryCenter> | Promise<void> | void;
  onDownloadLiveDemoReviewerDeliveryCenter: () => Promise<Blob>;
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
  onDownloadLiveDemoHandoffPackageReport,
  liveDemoHandoffDeliveryReceipts,
  liveDemoHandoffDeliveryReceiptError,
  onRecordLiveDemoHandoffDeliveryReceipt,
  onDownloadLiveDemoHandoffDeliveryReceiptReport,
  liveDemoHandoffDeliveryFinalization,
  liveDemoHandoffDeliveryFinalizationError,
  onRefreshLiveDemoHandoffDeliveryFinalization,
  onDownloadLiveDemoHandoffDeliveryFinalizationReport,
  liveDemoHandoffDeliveryFinalizationArchives,
  liveDemoHandoffDeliveryFinalizationArchiveError,
  onArchiveLiveDemoHandoffDeliveryFinalization,
  onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport,
  liveDemoCompletionCertificate,
  liveDemoCompletionCertificateError,
  onRefreshLiveDemoCompletionCertificate,
  onDownloadLiveDemoCompletionCertificateReport,
  liveDemoCompletionCertificateArchives,
  liveDemoCompletionCertificateArchiveError,
  onArchiveLiveDemoCompletionCertificate,
  onDownloadLiveDemoCompletionCertificateArchiveReport,
  liveDemoArtifactChainReport,
  liveDemoArtifactChainReportError,
  onRefreshLiveDemoArtifactChainReport,
  onDownloadLiveDemoArtifactChainReport,
  liveDemoReplayPackage,
  liveDemoReplayPackageError,
  onRefreshLiveDemoReplayPackage,
  onDownloadLiveDemoReplayPackage,
  liveDemoReviewerDeliveryCenter,
  liveDemoReviewerDeliveryCenterError,
  onRefreshLiveDemoReviewerDeliveryCenter,
  onDownloadLiveDemoReviewerDeliveryCenter
}: LiveLaunchGatePanelProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('bingqin2');
  const [repositoryName, setRepositoryName] = useState('PatchPilot');
  const [issueNumber, setIssueNumber] = useState('1');
  const [triggerUser, setTriggerUser] = useState('bingqin2');
  const [triggerComment, setTriggerComment] = useState('/agent fix touch docs/live-gate.md');
  const [handoffDeliveryChannel, setHandoffDeliveryChannel] = useState('github-comment');
  const [handoffDeliveryTarget, setHandoffDeliveryTarget] = useState(
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  const [handoffDeliveryOperator, setHandoffDeliveryOperator] = useState('local-operator');
  const [handoffDeliveryNotes, setHandoffDeliveryNotes] = useState(
    'Sent the live demo handoff package to the reviewer.'
  );

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

  function handoffDeliveryReceiptInput(): DemoLiveDemoHandoffDeliveryReceiptInput {
    return {
      deliveryChannel: handoffDeliveryChannel.trim(),
      deliveryTarget: handoffDeliveryTarget.trim(),
      operator: handoffDeliveryOperator.trim(),
      notes: handoffDeliveryNotes.trim(),
      deliveredAt: new Date().toISOString()
    };
  }

  async function recordLiveDemoHandoffDeliveryReceipt() {
    await onRecordLiveDemoHandoffDeliveryReceipt(handoffDeliveryReceiptInput());
  }

  async function downloadLiveDemoHandoffDeliveryReceipt(receiptId: string) {
    const blob = await onDownloadLiveDemoHandoffDeliveryReceiptReport(receiptId);
    downloadMarkdown(blob, `patchpilot-live-demo-handoff-delivery-receipt-${receiptId}.md`);
  }

  async function refreshLiveDemoHandoffDeliveryFinalization() {
    await onRefreshLiveDemoHandoffDeliveryFinalization();
  }

  async function downloadLiveDemoHandoffDeliveryFinalization() {
    const blob = await onDownloadLiveDemoHandoffDeliveryFinalizationReport();
    downloadMarkdown(blob, 'patchpilot-live-demo-handoff-delivery-finalization.md');
  }

  async function archiveLiveDemoHandoffDeliveryFinalization() {
    await onArchiveLiveDemoHandoffDeliveryFinalization();
  }

  async function downloadLiveDemoHandoffDeliveryFinalizationArchive(archiveId: string) {
    const blob = await onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport(archiveId);
    downloadMarkdown(blob, `patchpilot-live-demo-handoff-delivery-finalization-archive-${archiveId}.md`);
  }

  async function refreshLiveDemoCompletionCertificate() {
    await onRefreshLiveDemoCompletionCertificate();
  }

  async function downloadLiveDemoCompletionCertificate() {
    const blob = await onDownloadLiveDemoCompletionCertificateReport();
    downloadMarkdown(blob, 'patchpilot-live-demo-completion-certificate.md');
  }

  async function archiveLiveDemoCompletionCertificate() {
    await onArchiveLiveDemoCompletionCertificate();
  }

  async function downloadLiveDemoCompletionCertificateArchive(archiveId: string) {
    const blob = await onDownloadLiveDemoCompletionCertificateArchiveReport(archiveId);
    downloadMarkdown(blob, `patchpilot-live-demo-completion-certificate-archive-${archiveId}.md`);
  }

  async function refreshLiveDemoArtifactChainReport() {
    await onRefreshLiveDemoArtifactChainReport();
  }

  async function downloadLiveDemoArtifactChainReport() {
    const blob = await onDownloadLiveDemoArtifactChainReport();
    downloadMarkdown(blob, 'patchpilot-live-demo-artifact-chain-report.md');
  }

  async function refreshLiveDemoReplayPackage() {
    await onRefreshLiveDemoReplayPackage();
  }

  async function downloadLiveDemoReplayPackage() {
    const blob = await onDownloadLiveDemoReplayPackage();
    downloadMarkdown(blob, 'patchpilot-live-demo-replay-package.md');
  }

  async function refreshLiveDemoReviewerDeliveryCenter() {
    await onRefreshLiveDemoReviewerDeliveryCenter();
  }

  async function downloadLiveDemoReviewerDeliveryCenter() {
    const blob = await onDownloadLiveDemoReviewerDeliveryCenter();
    downloadMarkdown(blob, 'patchpilot-live-demo-reviewer-delivery-center.md');
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
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoHandoffDeliveryFinalization()}
              aria-label="Refresh live demo handoff delivery finalization"
            >
              <RefreshCw size={16} />
              Refresh handoff finalization
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoHandoffDeliveryFinalization()}
              disabled={!liveDemoHandoffDeliveryFinalization}
              aria-label="Download live demo handoff delivery finalization"
            >
              <FileCheck2 size={16} />
              Download handoff finalization
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveLiveDemoHandoffDeliveryFinalization()}
              disabled={!liveDemoHandoffDeliveryFinalization?.finalized}
              aria-label="Archive live demo handoff delivery finalization"
            >
              <Archive size={16} />
              Archive handoff finalization
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoCompletionCertificate()}
              aria-label="Refresh live demo completion certificate"
            >
              <RefreshCw size={16} />
              Refresh completion certificate
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoCompletionCertificate()}
              disabled={!liveDemoCompletionCertificate}
              aria-label="Download live demo completion certificate"
            >
              <FileCheck2 size={16} />
              Download completion certificate
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveLiveDemoCompletionCertificate()}
              disabled={!liveDemoCompletionCertificate?.certified}
              aria-label="Archive live demo completion certificate"
            >
              <Archive size={16} />
              Archive completion certificate
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoArtifactChainReport()}
              aria-label="Refresh live demo artifact chain report"
            >
              <RefreshCw size={16} />
              Refresh artifact chain
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoArtifactChainReport()}
              disabled={!liveDemoArtifactChainReport}
              aria-label="Download live demo artifact chain report"
            >
              <FileCheck2 size={16} />
              Download artifact chain
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoReplayPackage()}
              aria-label="Refresh live demo replay package"
            >
              <RefreshCw size={16} />
              Refresh replay package
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoReplayPackage()}
              disabled={!liveDemoReplayPackage}
              aria-label="Download live demo replay package"
            >
              <FileCheck2 size={16} />
              Download replay package
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void refreshLiveDemoReviewerDeliveryCenter()}
              aria-label="Refresh live demo reviewer delivery center"
            >
              <RefreshCw size={16} />
              Refresh delivery center
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadLiveDemoReviewerDeliveryCenter()}
              disabled={!liveDemoReviewerDeliveryCenter}
              aria-label="Download live demo reviewer delivery center"
            >
              <FileCheck2 size={16} />
              Download delivery center
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

      {liveDemoHandoffDeliveryReceiptError ? (
        <div className="adapter-api-error">
          <strong>Live demo handoff delivery receipt failed</strong>
          <span>{liveDemoHandoffDeliveryReceiptError}</span>
        </div>
      ) : null}

      {liveDemoHandoffDeliveryFinalizationError ? (
        <div className="adapter-api-error">
          <strong>Live demo handoff delivery finalization failed</strong>
          <span>{liveDemoHandoffDeliveryFinalizationError}</span>
        </div>
      ) : null}

      {liveDemoHandoffDeliveryFinalizationArchiveError ? (
        <div className="adapter-api-error">
          <strong>Live demo handoff delivery finalization archive failed</strong>
          <span>{liveDemoHandoffDeliveryFinalizationArchiveError}</span>
        </div>
      ) : null}

      {liveDemoCompletionCertificateError ? (
        <div className="adapter-api-error">
          <strong>Live demo completion certificate failed</strong>
          <span>{liveDemoCompletionCertificateError}</span>
        </div>
      ) : null}

      {liveDemoCompletionCertificateArchiveError ? (
        <div className="adapter-api-error">
          <strong>Live demo completion certificate archive failed</strong>
          <span>{liveDemoCompletionCertificateArchiveError}</span>
        </div>
      ) : null}

      {liveDemoArtifactChainReportError ? (
        <div className="adapter-api-error">
          <strong>Live demo artifact chain report failed</strong>
          <span>{liveDemoArtifactChainReportError}</span>
        </div>
      ) : null}

      {liveDemoReplayPackageError ? (
        <div className="adapter-api-error">
          <strong>Live demo replay package failed</strong>
          <span>{liveDemoReplayPackageError}</span>
        </div>
      ) : null}

      {liveDemoReviewerDeliveryCenterError ? (
        <div className="adapter-api-error">
          <strong>Live demo reviewer delivery center failed</strong>
          <span>{liveDemoReviewerDeliveryCenterError}</span>
        </div>
      ) : null}

      {result ? (
        <>
          {liveDemoReviewerDeliveryCenter ? (
            <LiveDemoReviewerDeliveryCenterResult center={liveDemoReviewerDeliveryCenter} />
          ) : null}
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
          <LiveDemoHandoffDeliveryReceiptRecorder
            deliveryChannel={handoffDeliveryChannel}
            deliveryTarget={handoffDeliveryTarget}
            operator={handoffDeliveryOperator}
            notes={handoffDeliveryNotes}
            disabled={!liveDemoHandoffPackage?.readyForReview}
            onDeliveryChannelChange={setHandoffDeliveryChannel}
            onDeliveryTargetChange={setHandoffDeliveryTarget}
            onOperatorChange={setHandoffDeliveryOperator}
            onNotesChange={setHandoffDeliveryNotes}
            onRecord={() => void recordLiveDemoHandoffDeliveryReceipt()}
          />
          <LiveDemoHandoffDeliveryReceiptList
            receipts={liveDemoHandoffDeliveryReceipts}
            onDownloadReceipt={(receiptId) => void downloadLiveDemoHandoffDeliveryReceipt(receiptId)}
          />
          {liveDemoHandoffDeliveryFinalization ? (
            <LiveDemoHandoffDeliveryFinalizationResult finalization={liveDemoHandoffDeliveryFinalization} />
          ) : null}
          <LiveDemoHandoffDeliveryFinalizationArchiveList
            archives={liveDemoHandoffDeliveryFinalizationArchives}
            onDownloadArchive={(archiveId) => void downloadLiveDemoHandoffDeliveryFinalizationArchive(archiveId)}
          />
          {liveDemoCompletionCertificate ? (
            <LiveDemoCompletionCertificateResult certificate={liveDemoCompletionCertificate} />
          ) : null}
          <LiveDemoCompletionCertificateArchiveList
            archives={liveDemoCompletionCertificateArchives}
            onDownloadArchive={(archiveId) => void downloadLiveDemoCompletionCertificateArchive(archiveId)}
          />
          {liveDemoArtifactChainReport ? (
            <LiveDemoArtifactChainReportResult report={liveDemoArtifactChainReport} />
          ) : null}
          {liveDemoReplayPackage ? (
            <LiveDemoReplayPackageResult replayPackage={liveDemoReplayPackage} />
          ) : null}
        </>
      ) : (
        <div className="empty-state">No live launch gate run yet.</div>
      )}
    </section>
  );
}

function LiveDemoReviewerDeliveryCenterResult({ center }: { center: DemoLiveDemoReviewerDeliveryCenter }) {
  const tone = center.status === 'READY' ? 'ready' : center.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {center.status}
        </span>
        <strong>Live demo reviewer delivery center</strong>
        <p>{center.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Deliverable</span>
          <strong>{center.deliverable ? 'Yes' : 'No'}</strong>
          <small>{center.nextAction}</small>
        </div>
        <div>
          <span>Repository</span>
          <strong>{center.repository ?? 'missing'}</strong>
          <small>{center.issueUrl ?? 'issue missing'}</small>
        </div>
        <div>
          <span>Task</span>
          <strong>{center.taskStatus ?? 'missing'}</strong>
          <small>{center.taskId ?? 'task missing'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{center.pullRequestUrl ?? 'missing'}</strong>
          <small>{center.generatedAt}</small>
        </div>
      </div>
      <section className="demo-launch-preflight-section">
        <h3>Readiness cards</h3>
        <ul>
          {center.readinessCards.map((card) => (
            <li key={card.name}>
              <strong>{card.name}</strong>
              <span>{card.status}</span>
              <p>{card.summary}</p>
              <small>{card.nextAction}</small>
            </li>
          ))}
        </ul>
      </section>
      {center.blockers.length > 0 ? (
        <section className="demo-launch-preflight-section">
          <h3>Blockers</h3>
          <ul>
            {center.blockers.map((blocker) => (
              <li key={blocker}>{blocker}</li>
            ))}
          </ul>
        </section>
      ) : null}
      <section className="demo-launch-preflight-section">
        <h3>Evidence links</h3>
        <ul>
          {center.evidenceLinks.map((link) => (
            <li key={`${link.label}-${link.url}`}>
              <strong>{link.label}</strong>
              <span>{link.url}</span>
              <small>{link.description}</small>
            </li>
          ))}
        </ul>
      </section>
      <section className="demo-launch-preflight-section">
        <h3>Download actions</h3>
        <ul>
          {center.downloadActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </section>
      <p className="demo-launch-preflight-contract">{center.sideEffectContract}</p>
    </div>
  );
}

function LiveDemoReplayPackageResult({ replayPackage }: { replayPackage: DemoLiveDemoReplayPackage }) {
  const tone =
    replayPackage.status === 'READY' ? 'ready' : replayPackage.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {replayPackage.status}
        </span>
        <strong>Live demo replay package</strong>
        <p>{replayPackage.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Artifact chain</span>
          <strong>{replayPackage.artifactChainStatus}</strong>
          <small>{replayPackage.completionCertificateArchiveId ?? 'completion certificate missing'}</small>
        </div>
        <div>
          <span>Repository</span>
          <strong>{replayPackage.repository ?? 'missing'}</strong>
          <small>{replayPackage.issueUrl ?? 'issue missing'}</small>
        </div>
        <div>
          <span>Task</span>
          <strong>{replayPackage.taskId ?? 'missing'}</strong>
          <small>{replayPackage.taskStatus ?? 'status missing'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{replayPackage.pullRequestUrl ?? 'missing'}</strong>
          <small>{replayPackage.replayReady ? 'Replay ready' : 'Replay blocked'}</small>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{replayPackage.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Replay walkthrough</h3>
        <ul>
          {replayPackage.sections.map((section) => (
            <li key={section.name}>
              <strong>{section.name}</strong>
              <span>{section.status}</span>
              <span>{section.summary}</span>
              <small>{section.action}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Evidence links</h3>
        <ul>
          {replayPackage.evidenceLinks.map((link) => (
            <li key={`${link.label}-${link.url}`}>
              <strong>{link.label}</strong>
              <span>{link.url}</span>
              <small>{link.description}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Replay steps</h3>
        <ul>
          {replayPackage.replaySteps.map((step) => (
            <li key={step}>{step}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Replay downloads</h3>
        <ul>
          {replayPackage.downloadActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
      <p className="demo-launch-preflight-blocked">Next action: {replayPackage.nextAction}</p>
    </div>
  );
}

function LiveDemoArtifactChainReportResult({ report }: { report: DemoLiveDemoArtifactChainReport }) {
  const tone =
    report.status === 'READY' ? 'ready' : report.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {report.status}
        </span>
        <strong>Live demo artifact chain report</strong>
        <p>{report.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Launch archive</span>
          <strong>{report.launchPackageArchiveId ?? 'missing'}</strong>
          <small>{report.repository ?? 'repository missing'}</small>
        </div>
        <div>
          <span>Outcome archive</span>
          <strong>{report.outcomeCloseoutArchiveId ?? 'missing'}</strong>
          <small>{report.taskId ? `Task ${report.taskId}` : 'No matching task'}</small>
        </div>
        <div>
          <span>Evidence archive</span>
          <strong>{report.evidenceBundleArchiveId ?? 'missing'}</strong>
          <small>{report.taskStatus ?? 'status missing'}</small>
        </div>
        <div>
          <span>Completion archive</span>
          <strong>{report.completionCertificateArchiveId ?? 'missing'}</strong>
          <small>{report.pullRequestUrl ?? 'Pull Request missing'}</small>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{report.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Artifact chain steps</h3>
        <ul>
          {report.steps.map((step) => (
            <li key={`${step.name}-${step.artifactId ?? 'missing'}`}>
              <strong>{step.name}</strong>
              <span>{step.status}</span>
              <span>{step.artifactId ?? 'missing'}</span>
              <small>{step.summary}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Consistency checks</h3>
        <ul>
          {report.checks.map((check) => (
            <li key={check.name}>
              <strong>{check.name}</strong>
              <span>{check.status}</span>
              <span>{check.summary}</span>
              <small>{check.nextAction}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Artifact chain evidence</h3>
        <ul>
          {report.evidenceNotes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Artifact chain downloads</h3>
        <ul>
          {report.downloadActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
      <p className="demo-launch-preflight-blocked">Next action: {report.nextAction}</p>
    </div>
  );
}

function LiveDemoHandoffDeliveryFinalizationResult({
  finalization
}: {
  finalization: DemoLiveDemoHandoffDeliveryFinalization;
}) {
  const tone =
    finalization.status === 'READY' ? 'ready' : finalization.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {finalization.status}
        </span>
        <strong>Live demo handoff delivery finalization</strong>
        <p>{finalization.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Delivery receipt</span>
          <strong>{finalization.latestDeliveryReceiptId ?? 'missing'}</strong>
          <small>{finalization.deliveryReceiptFreshness}</small>
        </div>
        <div>
          <span>Evidence archive</span>
          <strong>{finalization.evidenceBundleArchiveId ?? 'missing'}</strong>
          <small>{finalization.repository ?? 'repository missing'}</small>
        </div>
        <div>
          <span>Delivery target</span>
          <strong>{finalization.latestDeliveryTarget ?? 'missing'}</strong>
          <small>{finalization.latestDeliveryChannel ?? 'channel missing'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{finalization.pullRequestUrl ?? 'not created'}</strong>
          <small>{finalization.taskId ? `Task ${finalization.taskId}` : 'No matching task'}</small>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{finalization.deliveryReceiptFreshnessSummary}</p>
      <p className="demo-launch-preflight-blocked">{finalization.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Finalization checks</h3>
        <ul>
          {finalization.checks.map((check) => (
            <li key={check.name}>
              <strong>{check.name}</strong>
              <span>{check.status}</span>
              <span>{check.summary}</span>
              <small>{check.nextAction}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Finalization evidence</h3>
        <ul>
          {finalization.evidenceNotes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      </div>
      <div className="demo-launch-preflight-actions">
        <h3>Finalization downloads</h3>
        <ul>
          {finalization.downloadActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
      <p className="demo-launch-preflight-blocked">Next action: {finalization.nextAction}</p>
    </div>
  );
}

function LiveDemoCompletionCertificateResult({
  certificate
}: {
  certificate: DemoLiveDemoCompletionCertificate;
}) {
  const tone =
    certificate.status === 'READY' ? 'ready' : certificate.status === 'BLOCKED' ? 'blocked' : 'attention';

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {certificate.status}
        </span>
        <strong>Live demo completion certificate</strong>
        <p>{certificate.summary}</p>
      </div>
      <div className="demo-launch-preflight-grid">
        <div>
          <span>Handoff archive</span>
          <strong>{certificate.latestFinalizationArchiveId ?? 'missing'}</strong>
          <small>{certificate.latestFinalizationArchivedAt ?? 'not archived'}</small>
        </div>
        <div>
          <span>Delivery receipt</span>
          <strong>{certificate.latestDeliveryReceiptId ?? 'missing'}</strong>
          <small>{certificate.deliveryReceiptFreshness}</small>
        </div>
        <div>
          <span>Evidence archive</span>
          <strong>{certificate.evidenceBundleArchiveId ?? 'missing'}</strong>
          <small>{certificate.repository ?? 'repository missing'}</small>
        </div>
        <div>
          <span>Pull Request</span>
          <strong>{certificate.pullRequestUrl ?? 'not created'}</strong>
          <small>{certificate.taskId ? `Task ${certificate.taskId}` : 'No matching task'}</small>
        </div>
      </div>
      <p className="demo-launch-preflight-blocked">{certificate.sideEffectContract}</p>
      <div className="demo-launch-preflight-actions">
        <h3>Completion certificate downloads</h3>
        <ul>
          {certificate.downloadActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
      <p className="demo-launch-preflight-blocked">Next action: {certificate.nextAction}</p>
    </div>
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

function LiveDemoHandoffDeliveryReceiptRecorder({
  deliveryChannel,
  deliveryTarget,
  operator,
  notes,
  disabled,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onOperatorChange,
  onNotesChange,
  onRecord
}: {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  disabled: boolean;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onNotesChange: (value: string) => void;
  onRecord: () => void;
}) {
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onRecord();
  }

  return (
    <div className="demo-launch-preflight-actions">
      <h3>Record live demo handoff delivery</h3>
      <form
        className="demo-launch-preflight-form"
        aria-label="Live demo handoff delivery receipt form"
        onSubmit={submit}
      >
        <label htmlFor="live-demo-handoff-delivery-channel">
          Live demo handoff delivery channel
          <input
            id="live-demo-handoff-delivery-channel"
            value={deliveryChannel}
            onChange={(event) => onDeliveryChannelChange(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-demo-handoff-delivery-target">
          Live demo handoff delivery target
          <input
            id="live-demo-handoff-delivery-target"
            value={deliveryTarget}
            onChange={(event) => onDeliveryTargetChange(event.target.value)}
            required
          />
        </label>
        <label htmlFor="live-demo-handoff-delivery-operator">
          Live demo handoff delivery operator
          <input
            id="live-demo-handoff-delivery-operator"
            value={operator}
            onChange={(event) => onOperatorChange(event.target.value)}
            required
          />
        </label>
        <label className="demo-launch-comment" htmlFor="live-demo-handoff-delivery-notes">
          Live demo handoff delivery notes
          <input
            id="live-demo-handoff-delivery-notes"
            value={notes}
            onChange={(event) => onNotesChange(event.target.value)}
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={disabled || deliveryTarget.trim().length === 0 || deliveryChannel.trim().length === 0}
        >
          <ClipboardCheck size={16} />
          Record live demo handoff delivery receipt
        </button>
      </form>
    </div>
  );
}

function LiveDemoHandoffDeliveryReceiptList({
  receipts,
  onDownloadReceipt
}: {
  receipts: DemoLiveDemoHandoffDeliveryReceipt[];
  onDownloadReceipt: (receiptId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Live demo handoff delivery receipts</h3>
      {receipts.length === 0 ? (
        <p>No live demo handoff delivery receipts recorded.</p>
      ) : (
        <ul>
          {receipts.map((receipt) => (
            <li key={receipt.id}>
              <strong>{receipt.id}</strong>
              <span> {receipt.status} </span>
              <span>{receipt.deliveryChannel}</span>
              <span> {receipt.deliveryTarget}</span>
              <span>Delivered at {receipt.deliveredAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download live demo handoff delivery receipt ${receipt.id}`}
                onClick={() => onDownloadReceipt(receipt.id)}
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

function LiveDemoHandoffDeliveryFinalizationArchiveList({
  archives,
  onDownloadArchive
}: {
  archives: DemoLiveDemoHandoffDeliveryFinalizationArchive[];
  onDownloadArchive: (archiveId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Recent live demo handoff delivery finalization archives</h3>
      {archives.length === 0 ? (
        <p>No live demo handoff delivery finalization archives recorded.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <strong>{archive.id}</strong>
              <span> {archive.status} </span>
              <span>{archive.latestDeliveryReceiptId ?? 'No delivery receipt'}</span>
              <span>{archive.deliveryReceiptFreshness}</span>
              <span>Archived at {archive.archivedAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download live demo handoff delivery finalization archive ${archive.id}`}
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

function LiveDemoCompletionCertificateArchiveList({
  archives,
  onDownloadArchive
}: {
  archives: DemoLiveDemoCompletionCertificateArchive[];
  onDownloadArchive: (archiveId: string) => void;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <h3>Recent live demo completion certificate archives</h3>
      {archives.length === 0 ? (
        <p>No live demo completion certificate archives recorded.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <strong>{archive.id}</strong>
              <span> {archive.status} </span>
              <span>{archive.latestFinalizationArchiveId ?? 'No handoff finalization archive'}</span>
              <span>{archive.latestDeliveryReceiptId ?? 'No delivery receipt'}</span>
              <span>Archived at {archive.archivedAt}</span>
              <button
                className="secondary-button compact-button"
                type="button"
                aria-label={`Download live demo completion certificate archive ${archive.id}`}
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
