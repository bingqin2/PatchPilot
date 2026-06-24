import { render, screen, within } from '@testing-library/react';
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
    retriedAt: null
  },
  recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
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
    createdAt: '2026-06-24T00:00:00Z'
  },
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
  render(<DemoEvidenceBundlePanel bundle={bundle} error={null} />);

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
  expect(within(panel).getByText('delivery-1')).toBeInTheDocument();
  expect(within(panel).getByText('Fix failing adapter fixtures before a live demo.')).toBeInTheDocument();
  expect(within(panel).getByText('Inspect active trigger quarantines before a live demo.')).toBeInTheDocument();
});

test('shows loading and API errors without hiding existing bundle data', () => {
  const { rerender } = render(<DemoEvidenceBundlePanel bundle={null} error={null} />);

  expect(screen.getByText('Demo evidence bundle has not loaded yet.')).toBeInTheDocument();

  rerender(<DemoEvidenceBundlePanel bundle={bundle} error="Backend request failed" />);

  expect(screen.getByText('Demo evidence bundle unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('Demo evidence bundle needs attention.')).toBeInTheDocument();
});
