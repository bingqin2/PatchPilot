import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, test, vi } from 'vitest';
import { WebhookDeliveryPanel } from './WebhookDeliveryPanel';
import type { WebhookPayloadDiagnosticResult } from '../../types';

const readyDiagnostic: WebhookPayloadDiagnosticResult = {
  status: 'READY_FOR_WEBHOOK',
  signatureStatus: 'VALID',
  validJson: true,
  supportedEvent: true,
  supportedAction: true,
  agentFixCommand: true,
  repositoryOwner: 'octocat',
  repositoryName: 'hello-world',
  issueNumber: 42,
  triggerUser: 'alice',
  triggerComment: '/agent fix touch docs/webhook-diagnostic.md',
  message: 'Payload is an issue_comment.created /agent fix trigger.',
  nextAction: 'The payload shape is ready. Use GitHub redeliver.'
};

describe('WebhookDeliveryPanel', () => {
  test('evaluates a pasted webhook payload diagnostic', async () => {
    const user = userEvent.setup();
    const onEvaluatePayload = vi.fn(async () => readyDiagnostic);

    render(
      <WebhookDeliveryPanel
        deliveries={[]}
        error={null}
        evaluatingPayload={false}
        payloadDiagnostic={null}
        payloadDiagnosticError={null}
        onEvaluatePayload={onEvaluatePayload}
      />
    );

    await user.type(screen.getByLabelText('GitHub event'), 'issue_comment');
    await user.type(screen.getByLabelText('Delivery id'), 'diagnostic-delivery');
    await user.type(screen.getByLabelText('Signature'), 'sha256=test');
    await user.click(screen.getByLabelText('Payload'));
    await user.paste('{"action":"created"}');
    await user.click(screen.getByRole('button', { name: 'Evaluate payload' }));

    expect(onEvaluatePayload).toHaveBeenCalledWith({
      event: 'issue_comment',
      deliveryId: 'diagnostic-delivery',
      signature: 'sha256=test',
      payload: '{"action":"created"}'
    });
  });

  test('renders webhook payload diagnostic results', () => {
    render(
      <WebhookDeliveryPanel
        deliveries={[]}
        error={null}
        evaluatingPayload={false}
        payloadDiagnostic={readyDiagnostic}
        payloadDiagnosticError={null}
        onEvaluatePayload={vi.fn()}
      />
    );

    const result = screen.getByLabelText('Webhook payload diagnostic result');
    expect(within(result).getByText('READY_FOR_WEBHOOK')).toBeInTheDocument();
    expect(within(result).getByText('Signature VALID')).toBeInTheDocument();
    expect(within(result).getByText('octocat/hello-world #42')).toBeInTheDocument();
    expect(within(result).getByText('alice')).toBeInTheDocument();
    expect(within(result).getByText('/agent fix touch docs/webhook-diagnostic.md')).toBeInTheDocument();
    expect(within(result).getByText('The payload shape is ready. Use GitHub redeliver.')).toBeInTheDocument();
  });
});
