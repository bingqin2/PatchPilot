import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoEvidenceBundle } from '../../types';
import { DemoEvidenceBundlePanel } from './DemoEvidenceBundlePanel';

const bundle: DemoEvidenceBundle = {
  status: 'NEEDS_ATTENTION',
  summary: 'Demo evidence bundle needs attention.',
  summaryCounts: {
    adapterFixtureCount: 12,
    failedAdapterFixtureCount: 1,
    recentTaskCount: 3,
    activeQuarantineCount: 1,
    recentPullRequestAvailable: true
  },
  readiness: {
    status: 'READY',
    summary: 'PatchPilot is ready for a controlled demo.',
    checks: [],
    nextActions: []
  },
  smokeChecklist: {
    status: 'NEEDS_ATTENTION',
    summary: 'Live demo smoke checklist needs attention.',
    steps: [],
    nextActions: ['Fix webhook delivery before demo.']
  },
  configuration: null,
  adapterFixtures: {
    totalCount: 12,
    failedCount: 1
  },
  queueSummary: {
    totalCount: 3,
    pendingCount: 1,
    availablePendingCount: 1,
    delayedPendingCount: 0,
    runningCount: 0,
    completedCount: 2,
    failedCount: 0,
    cancelledCount: 0
  },
  recentTask: {
    id: 'task-2',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    installationId: 0,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md demo',
    deliveryId: 'delivery-task-2',
    commentId: 123,
    status: 'COMPLETED',
    failureReason: null,
    createdAt: '2026-06-24T00:00:00Z',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    completedAt: '2026-06-24T00:05:00Z',
    updatedAt: '2026-06-24T00:05:00Z',
    language: 'java',
    buildSystem: 'maven',
    verificationCommand: 'mvn test',
    adapterDetectionReason: 'Detected Maven project',
    statusCommentId: 456,
    statusCommentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
    riskReviewApprovedAt: null,
    riskReviewApprovedBy: null,
    riskReviewApprovalReason: null,
    retrySourceTaskId: null,
    retrySourceStatus: null,
    retrySourceFailureReason: null,
    retryReason: null,
    retriedAt: null
  },
  recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookSetupReadiness: {
    status: 'READY',
    secretConfigured: true,
    publicUrlReady: true,
    publicBaseUrl: 'https://demo.trycloudflare.com',
    payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    healthUrl: 'https://demo.trycloudflare.com/health',
    latestDeliveryStatus: 'TASK_CREATED',
    latestDeliveryId: 'delivery-1',
    redeliveryRecommended: false,
    summary: 'Webhook setup is ready for GitHub deliveries.',
    nextActions: ['Use the payload URL in GitHub Webhooks and continue the live demo.'],
    checkedAt: '2026-06-27T01:00:00Z',
    markdownReport: '# PatchPilot Webhook Setup Readiness'
  },
  latestWebhookDelivery: {
    id: 'delivery-1',
    deliveryId: 'delivery-1',
    event: 'issue_comment.created',
    status: 'TASK_CREATED',
    taskId: 'task-2',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md demo',
    message: 'Webhook created a task.',
    redeliveryRecommended: false,
    operatorAction: 'Task was created.',
    outcomeType: 'TASK',
    outcomeId: 'task-2',
    outcomeUrl: '/tasks/task-2',
    createdAt: '2026-06-24T00:00:00Z'
  },
  recentWebhookDeliveries: [
    {
      id: 'delivery-1',
      deliveryId: 'delivery-1',
      event: 'issue_comment.created',
      status: 'TASK_CREATED',
      taskId: 'task-2',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix replace docs/demo.md demo',
      message: 'Webhook created a task.',
      redeliveryRecommended: false,
      operatorAction: 'Task was created.',
      outcomeType: 'TASK',
      outcomeId: 'task-2',
      outcomeUrl: '/tasks/task-2',
      createdAt: '2026-06-24T00:00:00Z'
    },
    {
      id: 'delivery-2',
      deliveryId: 'delivery-2',
      event: 'issue_comment.created',
      status: 'REJECTED',
      taskId: null,
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: 'please do stuff',
      message: 'Webhook trigger was rejected before task creation.',
      redeliveryRecommended: false,
      operatorAction: 'Review rejected trigger evidence.',
      outcomeType: 'REJECTED_TRIGGER',
      outcomeId: 'rejected-1',
      outcomeUrl: '/rejected-triggers/rejected-1',
      createdAt: '2026-06-24T00:01:00Z'
    }
  ],
  rejectedTriggerSummary: {
    totalCount: 4,
    categoryCounts: [{ value: 'MODEL_REJECTED', count: 4 }],
    sourceCounts: [],
    triggerUserCounts: [],
    repositoryCounts: []
  },
  activeQuarantineCount: 1,
  generatedAt: '2026-06-24T00:10:00Z',
  nextActions: ['Fix failing adapter fixtures before a live demo.', 'Inspect active trigger quarantines before a live demo.']
};

test('summarizes demo evidence bundle for operators', () => {
  render(<DemoEvidenceBundlePanel bundle={bundle} error={null} onCopyRunbook={vi.fn()} />);

  const panel = screen.getByRole('region', { name: 'Demo evidence bundle' });
  expect(within(panel).getByRole('heading', { name: 'Demo evidence bundle' })).toBeInTheDocument();
  expect(within(panel).getByText('Needs attention')).toBeInTheDocument();
  expect(within(panel).getByText('Demo evidence bundle needs attention.')).toBeInTheDocument();
  expect(within(panel).getByText('12')).toBeInTheDocument();
  expect(within(panel).getByText('1 failed')).toBeInTheDocument();
  expect(within(panel).getByText('Recent PR available')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Open recent Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(within(panel).getByText('Latest webhook delivery')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup readiness')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup is ready for GitHub deliveries.')).toBeInTheDocument();
  expect(within(panel).getByText('https://demo.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
  expect(within(panel).getAllByText('delivery-1')).toHaveLength(2);
  expect(within(panel).getByText('Recent webhook delivery trail')).toBeInTheDocument();
  expect(within(panel).getByText('delivery-2')).toBeInTheDocument();
  expect(within(panel).getByText('REJECTED')).toBeInTheDocument();
  expect(within(panel).getByText('REJECTED_TRIGGER')).toBeInTheDocument();
  expect(within(panel).getByText('please do stuff')).toBeInTheDocument();
  expect(within(panel).getByText('Fix failing adapter fixtures before a live demo.')).toBeInTheDocument();
  expect(within(panel).getByText('Inspect active trigger quarantines before a live demo.')).toBeInTheDocument();
});

test('shows loading and API errors without hiding existing bundle data', () => {
  const { rerender } = render(<DemoEvidenceBundlePanel bundle={null} error={null} onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Demo evidence bundle has not loaded yet.')).toBeInTheDocument();

  rerender(<DemoEvidenceBundlePanel bundle={bundle} error="Backend request failed" onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Demo evidence bundle unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('Demo evidence bundle needs attention.')).toBeInTheDocument();
});

test('copies demo runbook markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyRunbook = vi.fn().mockResolvedValue('# PatchPilot Demo Runbook\n\n- Status: `READY`');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(<DemoEvidenceBundlePanel bundle={bundle} error={null} onCopyRunbook={onCopyRunbook} />);

  await userEvent.click(screen.getByRole('button', { name: 'Copy runbook' }));

  expect(onCopyRunbook).toHaveBeenCalledTimes(1);
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Runbook\n\n- Status: `READY`');
  expect(screen.getByText('Demo runbook copied')).toBeInTheDocument();
});
