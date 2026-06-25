import { FormEvent, useState } from 'react';
import type { WebhookDeliveryDiagnostic, WebhookPayloadDiagnosticInput, WebhookPayloadDiagnosticResult } from '../../types';
import { compactTime } from '../format';

interface WebhookDeliveryPanelProps {
  deliveries: WebhookDeliveryDiagnostic[];
  error: string | null;
  evaluatingPayload: boolean;
  payloadDiagnostic: WebhookPayloadDiagnosticResult | null;
  payloadDiagnosticError: string | null;
  onEvaluatePayload: (input: WebhookPayloadDiagnosticInput) => Promise<WebhookPayloadDiagnosticResult>;
}

export function WebhookDeliveryPanel({
  deliveries,
  error,
  evaluatingPayload,
  payloadDiagnostic,
  payloadDiagnosticError,
  onEvaluatePayload
}: WebhookDeliveryPanelProps) {
  const [event, setEvent] = useState('');
  const [deliveryId, setDeliveryId] = useState('');
  const [signature, setSignature] = useState('');
  const [payload, setPayload] = useState('');

  async function submitDiagnostic(formEvent: FormEvent<HTMLFormElement>) {
    formEvent.preventDefault();
    await onEvaluatePayload({
      event: event.trim(),
      deliveryId: deliveryId.trim(),
      signature: signature.trim(),
      payload
    });
  }

  return (
    <section className="panel webhook-delivery-panel" aria-label="Webhook deliveries">
      <div className="panel-header">
        <div>
          <h2>Webhook deliveries</h2>
          <p>{deliveries.length === 0 ? 'No recent deliveries' : `${deliveries.length} recent deliveries`}</p>
        </div>
      </div>
      {error ? <p className="panel-error">{error}</p> : null}
      <form className="webhook-payload-form" onSubmit={(formEvent) => void submitDiagnostic(formEvent)}>
        <label htmlFor="webhook-diagnostic-event">
          GitHub event
          <input
            id="webhook-diagnostic-event"
            value={event}
            onChange={(inputEvent) => setEvent(inputEvent.target.value)}
            placeholder="issue_comment"
            required
          />
        </label>
        <label htmlFor="webhook-diagnostic-delivery">
          Delivery id
          <input
            id="webhook-diagnostic-delivery"
            value={deliveryId}
            onChange={(inputEvent) => setDeliveryId(inputEvent.target.value)}
            placeholder="GitHub delivery id"
          />
        </label>
        <label htmlFor="webhook-diagnostic-signature">
          Signature
          <input
            id="webhook-diagnostic-signature"
            value={signature}
            onChange={(inputEvent) => setSignature(inputEvent.target.value)}
            placeholder="sha256=..."
          />
        </label>
        <label className="webhook-payload-field" htmlFor="webhook-diagnostic-payload">
          Payload
          <textarea
            id="webhook-diagnostic-payload"
            value={payload}
            onChange={(inputEvent) => setPayload(inputEvent.target.value)}
            placeholder='{"action":"created"}'
            required
          />
        </label>
        <button className="secondary-button" type="submit" disabled={evaluatingPayload}>
          {evaluatingPayload ? 'Evaluating payload' : 'Evaluate payload'}
        </button>
      </form>
      {payloadDiagnosticError ? <p className="panel-error">{payloadDiagnosticError}</p> : null}
      {payloadDiagnostic ? <WebhookPayloadDiagnosticResultPanel diagnostic={payloadDiagnostic} /> : null}
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
              <DeliveryOutcome delivery={delivery} />
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

function DeliveryOutcome({ delivery }: { delivery: WebhookDeliveryDiagnostic }) {
  const outcomeType = delivery.outcomeType ?? fallbackOutcomeType(delivery);
  const outcomeId = delivery.outcomeId ?? delivery.taskId;
  const outcomeUrl = delivery.outcomeUrl ?? (delivery.taskId ? `/tasks/${delivery.taskId}` : null);

  return (
    <span className="webhook-delivery-outcome">
      <span>Outcome {outcomeType}</span>
      {outcomeId && outcomeUrl ? (
        <a href={outcomeUrl}>{outcomeId}</a>
      ) : outcomeId ? (
        <strong>{outcomeId}</strong>
      ) : null}
    </span>
  );
}

function WebhookPayloadDiagnosticResultPanel({ diagnostic }: { diagnostic: WebhookPayloadDiagnosticResult }) {
  return (
    <section className="webhook-payload-result" aria-label="Webhook payload diagnostic result">
      <div className="webhook-payload-result-heading">
        <span className={`status-pill status-${diagnosticStatusClass(diagnostic.status)}`}>{diagnostic.status}</span>
        <strong>Signature {diagnostic.signatureStatus}</strong>
      </div>
      <div className="webhook-payload-result-grid">
        <div>
          <span>JSON</span>
          <p>{diagnostic.validJson ? 'Valid' : 'Invalid'}</p>
        </div>
        <div>
          <span>Event</span>
          <p>{diagnostic.supportedEvent ? 'Supported' : 'Unsupported'}</p>
        </div>
        <div>
          <span>Action</span>
          <p>{diagnostic.supportedAction ? 'created' : 'Not supported'}</p>
        </div>
        <div>
          <span>Trigger</span>
          <p>{diagnostic.agentFixCommand ? '/agent fix' : 'Ignored'}</p>
        </div>
      </div>
      <div className="webhook-payload-target">
        <span>{diagnosticRepositoryLabel(diagnostic)}</span>
        {diagnostic.triggerUser ? <span>{diagnostic.triggerUser}</span> : null}
        {diagnostic.triggerComment ? <span>{diagnostic.triggerComment}</span> : null}
      </div>
      <p>{diagnostic.message}</p>
      <p className="webhook-delivery-action">{diagnostic.nextAction}</p>
    </section>
  );
}

function fallbackOutcomeType(delivery: WebhookDeliveryDiagnostic) {
  if (delivery.status === 'TASK_CREATED' || delivery.status === 'ACTIVE_TASK_EXISTS') {
    return 'TASK';
  }
  if (delivery.status === 'DUPLICATE_DELIVERY') {
    return 'DUPLICATE';
  }
  if (delivery.status === 'REJECTED') {
    return 'REJECTED_TRIGGER';
  }
  if (delivery.status === 'IGNORED') {
    return 'IGNORED';
  }
  return 'ERROR';
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

function diagnosticStatusClass(status: WebhookPayloadDiagnosticResult['status']) {
  return status === 'READY_FOR_WEBHOOK' ? 'completed' : 'failed';
}

function diagnosticRepositoryLabel(diagnostic: WebhookPayloadDiagnosticResult) {
  if (!diagnostic.repositoryOwner || !diagnostic.repositoryName) {
    return 'repository unavailable';
  }
  const issue = diagnostic.issueNumber === null ? '' : ` #${diagnostic.issueNumber}`;
  return `${diagnostic.repositoryOwner}/${diagnostic.repositoryName}${issue}`;
}
