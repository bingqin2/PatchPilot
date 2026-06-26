import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoPreparedLaunchCommand, FixTask, WebhookDeliveryDiagnostic } from '../../types';
import { DemoLaunchTrackerPanel } from './DemoLaunchTrackerPanel';

const preparedCommand: DemoPreparedLaunchCommand = {
  triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  triggerUser: 'bingqin2',
  operation: 'replace',
  targetPath: 'docs/demo.md',
  replacementText: 'PatchPilot smoke test',
  savedAt: '2026-06-26T01:00:00Z'
};

const completedTask: FixTask = {
  id: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
  deliveryId: 'delivery-1',
  commentId: 101,
  status: 'COMPLETED',
  failureReason: null,
  createdAt: '2026-06-26T01:02:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  completedAt: '2026-06-26T01:07:00Z',
  updatedAt: '2026-06-26T01:07:00Z',
  language: 'java',
  buildSystem: 'maven',
  verificationCommand: './mvnw test',
  adapterDetectionReason: 'pom.xml detected',
  statusCommentId: null,
  statusCommentUrl: null,
  riskReviewApprovedAt: null,
  riskReviewApprovedBy: null,
  riskReviewApprovalReason: null,
  retrySourceTaskId: null,
  retrySourceStatus: null,
  retrySourceFailureReason: null,
  retryReason: null,
  retriedAt: null
};

const webhookDelivery: WebhookDeliveryDiagnostic = {
  id: 'delivery-row-1',
  deliveryId: 'delivery-1',
  event: 'issue_comment',
  status: 'TASK_CREATED',
  taskId: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
  message: 'Task created from /agent fix',
  redeliveryRecommended: false,
  operatorAction: 'Task was created. Do not redeliver this webhook.',
  outcomeType: 'TASK',
  outcomeId: 'task-1',
  outcomeUrl: '/tasks/task-1',
  createdAt: '2026-06-26T01:01:00Z'
};

test('tracks a successful launch through webhook, task, and Pull Request evidence', () => {
  render(
    <DemoLaunchTrackerPanel
      preparedLaunchCommands={[preparedCommand]}
      tasks={[completedTask]}
      webhookDeliveries={[webhookDelivery]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch tracker' });
  expect(within(panel).getByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook received')).toBeInTheDocument();
  expect(within(panel).getByText('Task completed')).toBeInTheDocument();
  expect(within(panel).getByText('Pull Request ready')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Open task task-1' })).toHaveAttribute('href', '/tasks/task-1');
  expect(within(panel).getByRole('link', { name: 'Open Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/8'
  );
});

test('copies a launch outcome report with webhook, task, and Pull Request evidence', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(window.navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoLaunchTrackerPanel
      preparedLaunchCommands={[preparedCommand]}
      tasks={[completedTask]}
      webhookDeliveries={[webhookDelivery]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch tracker' });
  await user.click(
    within(panel).getByRole('button', {
      name: 'Copy outcome report for /agent fix replace docs/demo.md PatchPilot smoke test'
    })
  );

  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('# PatchPilot Demo Launch Outcome Report'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Repository: `bingqin2/PatchPilot`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Issue: `#1`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Command: `/agent fix replace docs/demo.md PatchPilot smoke test`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Webhook status: `TASK_CREATED`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Webhook delivery id: `delivery-1`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Task: `task-1`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Task status: `COMPLETED`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Pull Request: https://github.com/bingqin2/PatchPilot/pull/8'));
  expect(writeText).toHaveBeenCalledWith(
    expect.stringContaining('- Next action: Launch succeeded. Open the Pull Request and review the generated patch.')
  );
});

test('shows waiting guidance when a prepared launch has no webhook yet', () => {
  render(
    <DemoLaunchTrackerPanel
      preparedLaunchCommands={[preparedCommand]}
      tasks={[]}
      webhookDeliveries={[]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch tracker' });
  expect(within(panel).getByText('Waiting for webhook')).toBeInTheDocument();
  expect(within(panel).getByText('Task pending')).toBeInTheDocument();
  expect(within(panel).getByText('Pull Request pending')).toBeInTheDocument();
  expect(
    within(panel).getByText('Post or redeliver the prepared GitHub issue comment, then refresh the dashboard.')
  ).toBeInTheDocument();
});

test('shows failure guidance when the launch task failed', () => {
  render(
    <DemoLaunchTrackerPanel
      preparedLaunchCommands={[preparedCommand]}
      tasks={[{ ...completedTask, status: 'FAILED', failureReason: 'maven tests failed', pullRequestUrl: null }]}
      webhookDeliveries={[webhookDelivery]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch tracker' });
  expect(within(panel).getByText('Webhook received')).toBeInTheDocument();
  expect(within(panel).getByText('Task failed')).toBeInTheDocument();
  expect(within(panel).getByText('Pull Request pending')).toBeInTheDocument();
  expect(
    within(panel).getByText('Launch reached task execution but failed. Open the task detail and inspect failure diagnosis.')
  ).toBeInTheDocument();
});
