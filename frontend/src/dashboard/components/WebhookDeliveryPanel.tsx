import type { WebhookDeliveryDiagnostic } from '../../types';
import { compactTime } from '../format';

interface WebhookDeliveryPanelProps {
  deliveries: WebhookDeliveryDiagnostic[];
  error: string | null;
}

export function WebhookDeliveryPanel({ deliveries, error }: WebhookDeliveryPanelProps) {
  return (
    <section className="panel webhook-delivery-panel" aria-label="Webhook deliveries">
      <div className="panel-header">
        <div>
          <h2>Webhook deliveries</h2>
          <p>{deliveries.length === 0 ? 'No recent deliveries' : `${deliveries.length} recent deliveries`}</p>
        </div>
      </div>
      {error ? <p className="panel-error">{error}</p> : null}
      <div className="webhook-delivery-list">
        {deliveries.map((delivery) => (
          <article className="webhook-delivery-row" key={delivery.id}>
            <div className="webhook-delivery-main">
              <span className={`status-pill status-${statusClass(delivery.status)}`}>{delivery.status}</span>
              <div>
                <strong>{delivery.deliveryId ?? 'missing delivery id'}</strong>
                <span>{delivery.event}</span>
              </div>
              <time>{compactTime(delivery.createdAt)}</time>
            </div>
            <div className="webhook-delivery-meta">
              <span>{repositoryLabel(delivery)}</span>
              {delivery.triggerUser ? <span>{delivery.triggerUser}</span> : null}
              {delivery.taskId ? <span>task {delivery.taskId}</span> : null}
            </div>
            <p>{delivery.message}</p>
            <div className="webhook-delivery-action">
              {delivery.redeliveryRecommended ? <span>Redeliver after fix</span> : null}
              <p>{delivery.operatorAction}</p>
            </div>
          </article>
        ))}
        {deliveries.length === 0 && !error ? <p className="empty-state">No webhook deliveries recorded.</p> : null}
      </div>
    </section>
  );
}

function repositoryLabel(delivery: WebhookDeliveryDiagnostic) {
  if (!delivery.repositoryOwner || !delivery.repositoryName) {
    return 'repository unavailable';
  }
  const issue = delivery.issueNumber === null ? '' : ` #${delivery.issueNumber}`;
  return `${delivery.repositoryOwner}/${delivery.repositoryName}${issue}`;
}

function statusClass(status: WebhookDeliveryDiagnostic['status']) {
  if (status === 'TASK_CREATED') {
    return 'completed';
  }
  if (status === 'IGNORED' || status === 'DUPLICATE_DELIVERY' || status === 'ACTIVE_TASK_EXISTS') {
    return 'pending';
  }
  return 'failed';
}
