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
