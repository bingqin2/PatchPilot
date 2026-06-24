import { render, screen, within } from '@testing-library/react';
import { TriggerDecisionPanel } from './TriggerDecisionPanel';
import type { FixTask, FixTaskTimelineEvent, RejectedTriggerAudit, RejectedTriggerAuditSummary } from '../../types';

const selectedTask: FixTask = {
  id: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 17,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix update failing test for parser',
  deliveryId: 'delivery-1',
  commentId: 101,
  status: 'COMPLETED',
  failureReason: null,
  createdAt: '2026-06-20T01:00:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/17',
  completedAt: '2026-06-20T01:04:00Z',
  updatedAt: '2026-06-20T01:04:00Z',
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
  retriedAt: null
};

const timeline: FixTaskTimelineEvent[] = [
  {
    id: 'timeline-trigger-accepted',
    taskId: 'task-1',
    eventType: 'TRIGGER_ACCEPTED',
    message: 'Trigger accepted: repository allowlist passed and model classified the issue as actionable',
    createdAt: '2026-06-20T00:59:58Z'
  }
];

const rejectedTriggers: RejectedTriggerAudit[] = [
  {
    id: 'rejected-1',
    source: 'WEBHOOK',
    category: 'NOT_ACTIONABLE',
    reason: 'Unsafe request rejected: instruction is not actionable',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 17,
    triggerUser: 'drive-by',
    triggerComment: '/agent fix make it better',
    deliveryId: 'delivery-rejected',
    commentId: 201,
    commentUrl: 'https://github.com/bingqin2/PatchPilot/issues/17#issuecomment-201',
    retriedTaskId: null,
    retriedAt: null,
    retryable: true,
    retryBlockedReason: null,
    createdAt: '2026-06-20T00:58:00Z'
  }
];

const summary: RejectedTriggerAuditSummary = {
  totalCount: 1,
  categoryCounts: [{ value: 'NOT_ACTIONABLE', count: 1 }],
  sourceCounts: [{ value: 'WEBHOOK', count: 1 }],
  triggerUserCounts: [{ value: 'drive-by', count: 1 }],
  repositoryCounts: [{ value: 'bingqin2/PatchPilot', count: 1 }]
};

describe('TriggerDecisionPanel', () => {
  it('shows accepted trigger evidence beside recent rejected decisions', () => {
    render(
      <TriggerDecisionPanel
        task={selectedTask}
        timeline={timeline}
        rejectedTriggers={rejectedTriggers}
        summary={summary}
      />
    );

    const panel = screen.getByRole('region', { name: 'Trigger decisions' });
    expect(within(panel).getByText('Accepted trigger evidence')).toBeInTheDocument();
    expect(within(panel).getByText('Rejected trigger decisions')).toBeInTheDocument();
    expect(
      within(panel).getByText('Trigger accepted: repository allowlist passed and model classified the issue as actionable')
    ).toBeInTheDocument();
    expect(within(panel).getAllByText('Not actionable')).toHaveLength(2);
    expect(within(panel).getByText('Unsafe request rejected: instruction is not actionable')).toBeInTheDocument();
    expect(within(panel).getByText('/agent fix make it better')).toBeInTheDocument();
    expect(within(panel).getByRole('link', { name: 'Refusal comment' })).toHaveAttribute(
      'href',
      'https://github.com/bingqin2/PatchPilot/issues/17#issuecomment-201'
    );
  });

  it('keeps the accepted side actionable when no task is selected', () => {
    render(
      <TriggerDecisionPanel
        task={null}
        timeline={[]}
        rejectedTriggers={[]}
        summary={null}
      />
    );

    expect(screen.getByText('Select a task to inspect accepted trigger evidence.')).toBeInTheDocument();
    expect(screen.getByText('No rejected trigger decisions recorded.')).toBeInTheDocument();
  });
});
