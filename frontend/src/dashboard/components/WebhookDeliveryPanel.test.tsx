import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, test, vi } from 'vitest';
import { WebhookDeliveryPanel } from './WebhookDeliveryPanel';
import type { GitHubWebhookSetupReadiness, WebhookDeliveryDiagnostic, WebhookPayloadDiagnosticResult } from '../../types';

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

const deliveryWithOutcome: WebhookDeliveryDiagnostic = {
  id: 'diagnostic-1',
  deliveryId: 'delivery-created',
  event: 'issue_comment',
  status: 'TASK_CREATED',
  taskId: 'task-123',
  outcomeType: 'TASK',
  outcomeId: 'task-123',
  outcomeUrl: '/tasks/task-123',
  repositoryOwner: 'octocat',
  repositoryName: 'hello-world',
  issueNumber: 42,
  triggerUser: 'alice',
  triggerComment: '/agent fix touch docs/demo.md',
  message: 'Task created from /agent fix',
  redeliveryRecommended: false,
  operatorAction: 'Task was created. Do not redeliver this webhook.',
  createdAt: '2026-06-25T00:00:00Z'
};

const setupReadiness: GitHubWebhookSetupReadiness = {
  status: 'NEEDS_ATTENTION',
  secretConfigured: true,
  publicUrlReady: true,
  publicBaseUrl: 'https://demo.trycloudflare.com',
  payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
  healthUrl: 'https://demo.trycloudflare.com/health',
  latestDeliveryStatus: 'INVALID_SIGNATURE',
  latestDeliveryId: 'delivery-invalid',
  redeliveryRecommended: true,
  summary: 'Webhook setup needs attention before redelivery.',
  nextActions: ["Fix the latest webhook delivery issue, then use GitHub's Redeliver action for that delivery."],
  checkedAt: '2026-06-27T02:10:00Z',
  markdownReport: '# PatchPilot Webhook Setup Readiness\n\n- Status: `NEEDS_ATTENTION`'
};

describe('WebhookDeliveryPanel', () => {
  test('evaluates a pasted webhook payload diagnostic', async () => {
    const user = userEvent.setup();
    const onEvaluatePayload = vi.fn(async () => readyDiagnostic);

    render(
      <WebhookDeliveryPanel
        setupReadiness={null}
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
        setupReadiness={null}
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

  test('renders delivery outcome correlation targets', () => {
    render(
      <WebhookDeliveryPanel
        setupReadiness={null}
        deliveries={[
          deliveryWithOutcome,
          {
            ...deliveryWithOutcome,
            id: 'diagnostic-2',
            deliveryId: 'delivery-rejected',
            status: 'REJECTED',
            taskId: null,
            outcomeType: 'REJECTED_TRIGGER',
            outcomeId: 'rejected-123',
            outcomeUrl: '#rejected-trigger-rejected-123',
            message: 'Rejected dangerous trigger',
            operatorAction: 'PatchPilot rejected this trigger by policy.'
          }
        ]}
        error={null}
        evaluatingPayload={false}
        payloadDiagnostic={null}
        payloadDiagnosticError={null}
        onEvaluatePayload={vi.fn()}
      />
    );

    const rows = screen.getAllByRole('article');
    expect(within(rows[0]).getByText('Outcome TASK')).toBeInTheDocument();
    expect(within(rows[0]).getByRole('link', { name: 'task-123' })).toHaveAttribute('href', '/tasks/task-123');
    expect(within(rows[1]).getByText('Outcome REJECTED_TRIGGER')).toBeInTheDocument();
    expect(within(rows[1]).getByRole('link', { name: 'rejected-123' })).toHaveAttribute(
      'href',
      '#rejected-trigger-rejected-123'
    );
  });

  test('renders webhook setup readiness before delivery rows', () => {
    render(
      <WebhookDeliveryPanel
        setupReadiness={setupReadiness}
        deliveries={[deliveryWithOutcome]}
        error={null}
        evaluatingPayload={false}
        payloadDiagnostic={null}
        payloadDiagnosticError={null}
        onEvaluatePayload={vi.fn()}
      />
    );

    const setup = screen.getByLabelText('Webhook setup readiness');
    expect(within(setup).getByText('NEEDS_ATTENTION')).toBeInTheDocument();
    expect(within(setup).getByText('https://demo.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
    expect(within(setup).getByText('INVALID_SIGNATURE')).toBeInTheDocument();
    expect(
      within(setup).getByText("Fix the latest webhook delivery issue, then use GitHub's Redeliver action for that delivery.")
    ).toBeInTheDocument();
    expect(setup).toHaveTextContent('# PatchPilot Webhook Setup Readiness');
  });
});
