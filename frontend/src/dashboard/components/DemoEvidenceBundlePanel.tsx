import { Copy } from 'lucide-react';
import { useState } from 'react';
import type { DemoEvidenceBundle, DemoReadinessStatus } from '../../types';
import { compactDateTime } from '../format';

interface DemoEvidenceBundlePanelProps {
  bundle: DemoEvidenceBundle | null;
  error: string | null;
  onCopyRunbook: () => Promise<string>;
}

export function DemoEvidenceBundlePanel({ bundle, error, onCopyRunbook }: DemoEvidenceBundlePanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const certificateEvidence = bundle?.launchAcceptanceCertificateEvidence ?? {
    status: 'NEEDS_ATTENTION' as DemoReadinessStatus,
    archived: false,
    certified: false,
    summary: 'No launch acceptance certificate archive is available.',
    nextAction: 'Archive the final launch acceptance certificate after the launch acceptance closeout is certified.',
    archiveCount: 0,
    latestArchiveId: null,
    latestCloseoutArchiveId: null,
    latestEvidenceArchiveId: null,
    latestDeliveryReceiptId: null,
    latestPullRequestUrl: null,
    latestArchivedAt: null,
    downloadActions: [
      'Archive the final launch acceptance certificate before using the evidence bundle as the external-review launch record.'
    ]
  };
  const taskCertificateEvidence = bundle?.taskEvidenceAcceptanceCertificateEvidence ?? {
    status: 'NEEDS_ATTENTION' as DemoReadinessStatus,
    archived: false,
    certified: false,
    summary: 'No task evidence acceptance certificate archive is available.',
    nextAction: 'Archive a certified task evidence acceptance certificate after final task evidence closeout.',
    archiveCount: 0,
    latestArchiveId: null,
    latestCloseoutArchiveId: null,
    latestEvidenceArchiveId: null,
    latestDeliveryReceiptId: null,
    latestTaskId: null,
    latestPullRequestUrl: null,
    latestArchivedAt: null,
    downloadActions: [
      'Archive a task evidence acceptance certificate before using the evidence bundle as task-level review proof.'
    ]
  };
  const finalPackageArchiveEvidence = bundle?.finalHandoffReportPackageArchiveEvidence ?? {
    status: 'NEEDS_ATTENTION' as DemoReadinessStatus,
    archived: false,
    downloadReady: false,
    summary: 'No final handoff report package archive is available.',
    nextAction: 'Archive the final handoff report package after the post-demo handoff package is finalized.',
    archiveCount: 0,
    latestArchiveId: null,
    latestHandoffArchiveId: null,
    latestSessionId: null,
    latestDeliveryReceiptId: null,
    taskCertificateArchiveId: null,
    taskCertificateReady: false,
    latestArchivedAt: null,
    downloadActions: [
      'Archive the final handoff report package before using the evidence bundle as post-demo closeout proof.'
    ]
  };

  async function copyRunbook() {
    try {
      const runbook = await onCopyRunbook();
      await navigator.clipboard.writeText(runbook);
      setCopyStatus('Demo runbook copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  return (
    <section className="panel demo-evidence-panel" aria-label="Demo evidence bundle">
      <div className="panel-header">
        <div>
          <h2>Demo evidence bundle</h2>
          <p>{bundle?.summary ?? 'Loading demo evidence bundle'}</p>
        </div>
        <div className="demo-evidence-header-actions">
          {bundle ? (
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(bundle.status)}`}>
              {statusLabel(bundle.status)}
            </span>
          ) : null}
          <button className="secondary-button" type="button" onClick={() => void copyRunbook()}>
            <Copy size={14} />
            Copy runbook
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo evidence bundle unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {bundle ? (
        <>
          <div className="demo-evidence-grid">
            <EvidenceStat
              label="Adapter fixtures"
              value={bundle.summaryCounts.adapterFixtureCount}
              detail={`${bundle.summaryCounts.failedAdapterFixtureCount} failed`}
            />
            <EvidenceStat
              label="Recent tasks"
              value={bundle.summaryCounts.recentTaskCount}
              detail={`${bundle.queueSummary.failedCount} failed queue items`}
            />
            <EvidenceStat
              label="Rejected triggers"
              value={bundle.rejectedTriggerSummary?.totalCount ?? 0}
              detail={`${bundle.summaryCounts.activeQuarantineCount} active quarantines`}
            />
            <EvidenceStat
              label="Pull Request"
              value={bundle.summaryCounts.recentPullRequestAvailable ? 'Ready' : 'Missing'}
              detail={bundle.summaryCounts.recentPullRequestAvailable ? 'Recent PR available' : 'Run smoke task'}
            />
          </div>

          <div className="demo-evidence-records">
            <div>
              <span>Webhook setup readiness</span>
              <strong>{bundle.webhookSetupReadiness?.status ?? 'No setup evidence'}</strong>
              <small>{bundle.webhookSetupReadiness?.summary ?? 'No setup summary'}</small>
              {bundle.webhookSetupReadiness?.payloadUrl ? (
                <code>{bundle.webhookSetupReadiness.payloadUrl}</code>
              ) : null}
            </div>
            <div>
              <span>Latest webhook delivery</span>
              <strong>{bundle.latestWebhookDelivery?.deliveryId ?? 'No delivery evidence'}</strong>
              <small>{bundle.latestWebhookDelivery?.status ?? 'No webhook status'}</small>
            </div>
            <div>
              <span>Full evaluation run readiness</span>
              <strong>{statusLabel(bundle.evaluationRunReadiness.status)}</strong>
              <small>Latest evaluation run {bundle.evaluationRunReadiness.latestRunId ?? 'none'}</small>
              <small>Previous evaluation run {bundle.evaluationRunReadiness.previousRunId ?? 'none'}</small>
              <small>
                Deltas passed {signed(bundle.evaluationRunReadiness.passedDelta)}, failed {signed(bundle.evaluationRunReadiness.failedDelta)}, skipped{' '}
                {signed(bundle.evaluationRunReadiness.skippedDelta)}
              </small>
              <small>
                Coverage {csv(bundle.evaluationRunReadiness.coveredLanguages)} / {csv(bundle.evaluationRunReadiness.coveredBuildSystems)}
              </small>
              <small>Safety {csv(bundle.evaluationRunReadiness.safetyRejectionCategories)}</small>
              <small>{bundle.evaluationRunReadiness.nextAction}</small>
            </div>
            <div>
              <span>Handoff share checklist</span>
              <strong>{statusLabel(bundle.handoffShareChecklistStatus)}</strong>
              <small>{bundle.handoffShareChecklistSummary}</small>
              <small>{bundle.handoffShareChecklistNextAction}</small>
            </div>
            <div>
              <span>Handoff share center</span>
              <strong>{statusLabel(bundle.handoffShareCenterStatus)}</strong>
              <small>{bundle.handoffShareCenterSummary}</small>
              <small>{bundle.handoffShareCenterNextAction}</small>
              {bundle.handoffShareCenterDownloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Launch evidence share center</span>
              <strong>{launchShareCenterStatusLabel(bundle.launchEvidenceShareCenterStatus)}</strong>
              <small>{bundle.launchEvidenceShareCenterSummary}</small>
              <small>{bundle.launchEvidenceShareCenterNextAction}</small>
              <small>{bundle.launchEvidenceShareCenterArchiveCount} archived packages</small>
              <small>{bundle.launchEvidenceShareCenterLatestArchiveId ?? 'No launch archive'}</small>
              <small>{bundle.launchEvidenceShareCenterLatestSessionId ?? 'No launch session'}</small>
              <small>{bundle.launchEvidenceShareCenterLatestPullRequestUrl ?? 'No archived launch Pull Request'}</small>
              {bundle.launchEvidenceShareCenterDownloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Launch evidence finalization</span>
              <strong>{bundle.launchEvidenceFinalized ? 'Finalized' : statusLabel(bundle.launchEvidenceFinalizationStatus)}</strong>
              <small>{bundle.launchEvidenceFinalizationSummary}</small>
              <small>{bundle.launchEvidenceFinalizationNextAction}</small>
              <small>{deliveryFreshnessLabel(bundle.launchEvidenceFinalizationDeliveryReceiptFreshness)}</small>
              <small>{bundle.launchEvidenceFinalizationLatestDeliveryReceiptId ?? 'No accepted launch receipt'}</small>
            </div>
            <div>
              <span>Launch acceptance closeout</span>
              <strong>
                {bundle.launchAcceptanceCloseoutEvidence.accepted
                  ? 'Accepted archive'
                  : statusLabel(bundle.launchAcceptanceCloseoutEvidence.status)}
              </strong>
              <small>{bundle.launchAcceptanceCloseoutEvidence.summary}</small>
              <small>{bundle.launchAcceptanceCloseoutEvidence.nextAction}</small>
              <small>{bundle.launchAcceptanceCloseoutEvidence.archiveCount} closeout archives</small>
              <small>{bundle.launchAcceptanceCloseoutEvidence.latestArchiveId ?? 'No closeout archive'}</small>
              <small>{bundle.launchAcceptanceCloseoutEvidence.latestEvidenceArchiveId ?? 'No linked launch evidence archive'}</small>
              <small>{bundle.launchAcceptanceCloseoutEvidence.latestDeliveryReceiptId ?? 'No accepted launch receipt'}</small>
              {bundle.launchAcceptanceCloseoutEvidence.latestArchivedAt ? (
                <small>Archived {compactDateTime(bundle.launchAcceptanceCloseoutEvidence.latestArchivedAt)}</small>
              ) : null}
              {bundle.launchAcceptanceCloseoutEvidence.downloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Launch acceptance certificate</span>
              <strong>
                {certificateEvidence.certified
                  ? 'Certified archive'
                  : statusLabel(certificateEvidence.status)}
              </strong>
              <small>{certificateEvidence.summary}</small>
              <small>{certificateEvidence.nextAction}</small>
              <small>{certificateEvidence.archiveCount} certificate archives</small>
              <small>{certificateEvidence.latestArchiveId ?? 'No certificate archive'}</small>
              <small>{certificateEvidence.latestCloseoutArchiveId ?? 'No linked closeout archive'}</small>
              <small>{certificateEvidence.latestEvidenceArchiveId ?? 'No linked launch evidence archive'}</small>
              <small>{certificateEvidence.latestDeliveryReceiptId ?? 'No accepted launch receipt'}</small>
              {certificateEvidence.latestPullRequestUrl ? (
                <a href={certificateEvidence.latestPullRequestUrl}>Open certificate Pull Request</a>
              ) : (
                <small>No certificate Pull Request</small>
              )}
              {certificateEvidence.latestArchivedAt ? (
                <small>Certified {compactDateTime(certificateEvidence.latestArchivedAt)}</small>
              ) : null}
              {certificateEvidence.downloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Task evidence acceptance certificate</span>
              <strong>
                {taskCertificateEvidence.certified
                  ? 'Certified archive'
                  : statusLabel(taskCertificateEvidence.status)}
              </strong>
              <small>{taskCertificateEvidence.summary}</small>
              <small>{taskCertificateEvidence.nextAction}</small>
              <small>{taskCertificateEvidence.archiveCount} certificate archives</small>
              <small>{taskCertificateEvidence.latestArchiveId ?? 'No task certificate archive'}</small>
              <small>{taskCertificateEvidence.latestCloseoutArchiveId ?? 'No linked task closeout archive'}</small>
              <small>{taskCertificateEvidence.latestEvidenceArchiveId ?? 'No linked task evidence archive'}</small>
              <small>{taskCertificateEvidence.latestDeliveryReceiptId ?? 'No accepted task evidence receipt'}</small>
              <small>{taskCertificateEvidence.latestTaskId ? `Task ${taskCertificateEvidence.latestTaskId}` : 'No certified task'}</small>
              {taskCertificateEvidence.latestPullRequestUrl ? (
                <a href={taskCertificateEvidence.latestPullRequestUrl}>Open task certificate Pull Request</a>
              ) : (
                <small>No task certificate Pull Request</small>
              )}
              {taskCertificateEvidence.latestArchivedAt ? (
                <small>Certified {compactDateTime(taskCertificateEvidence.latestArchivedAt)}</small>
              ) : null}
              {taskCertificateEvidence.downloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Final handoff report package archive</span>
              <strong>
                {finalPackageArchiveEvidence.downloadReady
                  ? 'Download-ready archive'
                  : statusLabel(finalPackageArchiveEvidence.status)}
              </strong>
              <small>{finalPackageArchiveEvidence.summary}</small>
              <small>{finalPackageArchiveEvidence.nextAction}</small>
              <small>{finalPackageArchiveEvidence.archiveCount} final package archives</small>
              <small>{finalPackageArchiveEvidence.latestArchiveId ?? 'No final package archive'}</small>
              <small>{finalPackageArchiveEvidence.latestHandoffArchiveId ?? 'No linked handoff archive'}</small>
              <small>{finalPackageArchiveEvidence.latestSessionId ?? 'No linked session'}</small>
              <small>{finalPackageArchiveEvidence.latestDeliveryReceiptId ?? 'No linked delivery receipt'}</small>
              <small>{finalPackageArchiveEvidence.taskCertificateArchiveId ?? 'No linked task certificate'}</small>
              {finalPackageArchiveEvidence.latestArchivedAt ? (
                <small>Archived {compactDateTime(finalPackageArchiveEvidence.latestArchivedAt)}</small>
              ) : null}
              {finalPackageArchiveEvidence.downloadActions.slice(0, 2).map((action) => (
                <small key={action}>{action}</small>
              ))}
            </div>
            <div>
              <span>Handoff share delivery</span>
              <strong>{deliveryFreshnessLabel(bundle.handoffShareDeliveryReceiptFreshness)}</strong>
              <small>{bundle.handoffShareDeliveryReceiptFreshnessSummary}</small>
              <small>{bundle.handoffShareDeliveryReceiptRecorded ? bundle.handoffShareLatestDeliveryReceiptId : 'No receipt'}</small>
              <small>
                {bundle.handoffShareDeliveryReceiptRecorded
                  ? `${bundle.handoffShareLatestDeliveryChannel} - ${bundle.handoffShareLatestDeliveryTarget}`
                  : 'Record a delivery receipt after sending the package.'}
              </small>
              {bundle.handoffShareLatestDeliveredAt ? (
                <small>Delivered {compactDateTime(bundle.handoffShareLatestDeliveredAt)}</small>
              ) : null}
            </div>
            <div>
              <span>Handoff finalization</span>
              <strong>{bundle.handoffFinalized ? 'Finalized' : statusLabel(bundle.handoffFinalizationStatus)}</strong>
              <small>{bundle.handoffFinalizationSummary}</small>
              <small>{bundle.handoffFinalizationNextAction}</small>
              <small>{deliveryFreshnessLabel(bundle.handoffFinalizationDeliveryReceiptFreshness)}</small>
              <small>{bundle.handoffFinalizationLatestDeliveryReceiptId ?? 'No accepted receipt'}</small>
            </div>
            <div>
              <span>Recent task</span>
              <strong>{bundle.recentTask?.id ?? 'No recent task'}</strong>
              <small>{bundle.recentTask?.status ?? 'No task status'}</small>
            </div>
            <div>
              <span>Generated</span>
              <strong>{compactDateTime(bundle.generatedAt)}</strong>
              <small>Evidence snapshot</small>
            </div>
          </div>

          <div className="demo-webhook-delivery-trail">
            <div className="section-heading">
              <h3>Recent webhook delivery trail</h3>
              <span>{bundle.recentWebhookDeliveries.length} deliveries</span>
            </div>
            {bundle.recentWebhookDeliveries.length === 0 ? (
              <p>No recent webhook deliveries recorded.</p>
            ) : (
              <ul>
                {bundle.recentWebhookDeliveries.slice(0, 5).map((delivery) => (
                  <li key={delivery.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{delivery.deliveryId ?? 'No delivery id'}</strong>
                      <span>{delivery.status}</span>
                    </div>
                    <div className="demo-webhook-delivery-meta">
                      <span>{delivery.outcomeType ?? 'No outcome'}</span>
                      <span>
                        {delivery.repositoryOwner ?? 'unknown'}/{delivery.repositoryName ?? 'unknown'}#
                        {delivery.issueNumber ?? 'none'}
                      </span>
                    </div>
                    <p>{delivery.triggerComment ?? delivery.message}</p>
                    <small>{delivery.operatorAction}</small>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {bundle.recentPullRequestUrl ? (
            <a className="external-link" href={bundle.recentPullRequestUrl} target="_blank" rel="noreferrer">
              Open recent Pull Request
            </a>
          ) : null}

          <div className="demo-evidence-actions">
            <h3>Evidence next actions</h3>
            <ul>
              {bundle.nextActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div className="empty-state">Demo evidence bundle has not loaded yet.</div>
      )}
    </section>
  );
}

interface EvidenceStatProps {
  label: string;
  value: number | string;
  detail: string;
}

function EvidenceStat({ label, value, detail }: EvidenceStatProps) {
  return (
    <div className="demo-evidence-stat">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
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

function launchShareCenterStatusLabel(status: DemoReadinessStatus | 'NO_ARCHIVE') {
  if (status === 'NO_ARCHIVE') {
    return 'No archive';
  }
  return statusLabel(status);
}

function deliveryFreshnessLabel(freshness: string) {
  switch (freshness) {
    case 'FRESH':
      return 'Fresh';
    case 'STALE':
      return 'Stale';
    case 'MISSING':
      return 'Missing';
    default:
      return freshness;
  }
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}

function signed(value: number) {
  return value > 0 ? `+${value}` : String(value);
}

function csv(values: string[]) {
  return values.length === 0 ? 'none' : values.join(', ');
}
