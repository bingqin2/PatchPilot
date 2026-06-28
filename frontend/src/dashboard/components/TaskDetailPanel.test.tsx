import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { TaskDetailState } from '../types';
import { TaskDetailPanel, taskLinkFor } from './TaskDetailPanel';
import type { FixTask, FixTaskAuditSummary, FixTaskEvidencePackageArchive } from '../../types';

const task: FixTask = {
  id: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix demo',
  deliveryId: 'delivery-1',
  commentId: 101,
  status: 'COMPLETED',
  failureReason: null,
  createdAt: '2026-06-20T01:00:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  completedAt: '2026-06-20T01:01:00Z',
  updatedAt: '2026-06-20T01:01:00Z',
  language: 'python',
  buildSystem: 'pytest',
  verificationCommand: 'python3 -m pytest',
  adapterDetectionReason: 'pyproject.toml declares pytest as the verification command',
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

const baseSummary: FixTaskAuditSummary = {
  task,
  timelineEventCount: 4,
  testRunCount: 1,
  toolCallCount: 3,
  modelCallCount: 2,
  totalModelTokens: 1800,
  latestTimelineEvent: {
    id: 'timeline-1',
    taskId: 'task-1',
    eventType: 'COMPLETED',
    message: 'Task completed',
    createdAt: '2026-06-20T01:01:00Z'
  },
  latestTestRunExitCode: 0,
  latestTestRunDurationMs: 12769
};

const baseDetail: TaskDetailState = {
  summary: baseSummary,
  queueItem: {
    id: 'queue-1',
    taskId: 'task-1',
    status: 'FAILED',
    attemptCount: 3,
    lastError: 'maven tests failed',
    availableAt: '2026-06-20T01:02:00Z',
    lockedAt: '2026-06-20T01:01:00Z',
    createdAt: '2026-06-20T01:00:00Z',
    updatedAt: '2026-06-20T01:03:00Z'
  },
  queueItems: [
    {
      id: 'queue-1',
      taskId: 'task-1',
      status: 'FAILED',
      attemptCount: 3,
      lastError: 'maven tests failed',
      availableAt: '2026-06-20T01:02:00Z',
      lockedAt: '2026-06-20T01:01:00Z',
      createdAt: '2026-06-20T01:00:00Z',
      updatedAt: '2026-06-20T01:03:00Z'
    },
    {
      id: 'queue-older',
      taskId: 'task-1',
      status: 'PENDING',
      attemptCount: 1,
      lastError: null,
      availableAt: '2026-06-20T00:58:00Z',
      lockedAt: null,
      createdAt: '2026-06-20T00:57:00Z',
      updatedAt: '2026-06-20T00:58:00Z'
    }
  ],
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: [],
  triggerIntentAudit: null,
  preExecutionSafetySnapshot: null,
  generatedDiff: null,
  patchReview: null,
  issueContext: {
    title: 'Dashboard should show issue context',
    body: 'The issue body explains why the dashboard needs to surface context before operators inspect evidence.',
    url: 'https://github.com/bingqin2/PatchPilot/issues/1',
    comments: [
      {
        id: 1001,
        author: 'alice',
        body: 'The latest reproduction is in the failing smoke test.',
        createdAt: '2026-06-20T01:00:30Z',
        url: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-1001'
      }
    ]
  },
  failureDiagnosis: null,
  evidencePackageArchives: [],
  retryPreflight: null,
  repositorySupportGuidance: null,
  adapterExecutionEvidence: {
    status: 'SUPPORTED',
    language: 'python',
    buildSystem: 'pytest',
    verificationCommand: 'python3 -m pytest',
    detectionReason: 'pyproject.toml declares pytest as the verification command',
    operatorAction: 'Review verification output and Pull Request evidence for this selected adapter.',
    safetyNote: 'Verification command came from a registered language adapter, not from the issue comment.',
    supportedAdapters: []
  }
};

const evidenceArchive: FixTaskEvidencePackageArchive = {
  id: 'task-evidence-archive-1',
  taskId: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  status: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  archivedAt: '2026-06-20T01:05:00Z',
  summary: 'Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.',
  report: '# PatchPilot Task Report\n\n- Task: `task-1`'
};

test('shows execution evidence summary for selected task', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveEvidencePackage={vi.fn()}
      onDownloadEvidencePackageReport={vi.fn()}
    />
  );

  expect(screen.getByText('Execution evidence')).toBeInTheDocument();
  expect(screen.getByText('Timeline 4')).toBeInTheDocument();
  expect(screen.getByText('Tests 1')).toBeInTheDocument();
  expect(screen.getByText('Tools 3')).toBeInTheDocument();
  expect(screen.getByText('Model calls 2')).toBeInTheDocument();
  expect(screen.getByText('Latest test PASS')).toBeInTheDocument();
  expect(screen.getByText('Adapter python / pytest')).toBeInTheDocument();
  expect(
    screen.getByText('Detection pyproject.toml declares pytest as the verification command')
  ).toBeInTheDocument();
  expect(screen.getByText('Verify python3 -m pytest')).toBeInTheDocument();
});

test('shows structured adapter execution evidence for selected task', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveEvidencePackage={vi.fn()}
      onDownloadEvidencePackageReport={vi.fn()}
    />
  );

  const evidence = screen.getByLabelText('Adapter execution evidence');
  expect(within(evidence).getByText('Adapter execution evidence')).toBeInTheDocument();
  expect(within(evidence).getByText('SUPPORTED')).toBeInTheDocument();
  expect(within(evidence).getByText('python / pytest')).toBeInTheDocument();
  expect(within(evidence).getByText('python3 -m pytest')).toBeInTheDocument();
  expect(
    within(evidence).getByText('pyproject.toml declares pytest as the verification command')
  ).toBeInTheDocument();
  expect(
    within(evidence).getByText('Verification command came from a registered language adapter, not from the issue comment.')
  ).toBeInTheDocument();
});

test('shows pending adapter execution evidence before preflight records metadata', () => {
  render(
    <TaskDetailPanel
      task={{ ...task, language: null, buildSystem: null, verificationCommand: null, adapterDetectionReason: null }}
      detail={{
        ...baseDetail,
        adapterExecutionEvidence: {
          status: 'PENDING',
          language: null,
          buildSystem: null,
          verificationCommand: null,
          detectionReason: null,
          operatorAction: 'Wait for workspace preflight to record adapter evidence before trusting verification status.',
          safetyNote: 'No adapter-selected verification command has been recorded for this task yet.',
          supportedAdapters: []
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveEvidencePackage={vi.fn()}
      onDownloadEvidencePackageReport={vi.fn()}
    />
  );

  const evidence = screen.getByLabelText('Adapter execution evidence');
  expect(within(evidence).getByText('PENDING')).toBeInTheDocument();
  expect(within(evidence).getByText('Not selected yet')).toBeInTheDocument();
  expect(within(evidence).getByText('No adapter-selected command')).toBeInTheDocument();
  expect(within(evidence).getByText('No detection evidence recorded')).toBeInTheDocument();
});

test('shows unsupported adapter execution evidence with supported adapter options', () => {
  render(
    <TaskDetailPanel
      task={{ ...task, status: 'FAILED', language: null, buildSystem: null, verificationCommand: null, adapterDetectionReason: null }}
      detail={{
        ...baseDetail,
        adapterExecutionEvidence: {
          status: 'UNSUPPORTED',
          language: null,
          buildSystem: null,
          verificationCommand: null,
          detectionReason: 'Unsupported repository: no supported language adapter detected',
          operatorAction:
            'Add one supported project marker and deterministic test command, then trigger /agent fix again. PatchPilot will not run arbitrary commands for unsupported repositories.',
          safetyNote:
            'PatchPilot stopped before model patch generation, verification, commit, push, or Pull Request creation.',
          supportedAdapters: [
            {
              language: 'java',
              buildSystem: 'maven',
              verificationCommand: ['mvn', 'test'],
              detectionSignals: ['pom.xml', 'mvnw'],
              demoFixturePath: 'docs/demo-repositories/java-maven',
              status: 'SUPPORTED'
            }
          ]
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveEvidencePackage={vi.fn()}
      onDownloadEvidencePackageReport={vi.fn()}
    />
  );

  const evidence = screen.getByLabelText('Adapter execution evidence');
  expect(within(evidence).getByText('UNSUPPORTED')).toBeInTheDocument();
  expect(
    within(evidence).getByText('PatchPilot stopped before model patch generation, verification, commit, push, or Pull Request creation.')
  ).toBeInTheDocument();
  expect(within(evidence).getByText('java / maven')).toBeInTheDocument();
  expect(within(evidence).getByText('mvn test')).toBeInTheDocument();
});

test('shows trigger intent audit for accepted tasks', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        triggerIntentAudit: {
          eventId: 'timeline-trigger',
          summary: 'Trigger accepted',
          safetyDecision: 'safety gate accepted',
          issueContextStatus: 'issue context loaded',
          modelDecision: 'model accepted trigger: Issue context describes a concrete failing test',
          createdAt: '2026-06-20T01:00:30Z'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const audit = screen.getByLabelText('Trigger intent');
  expect(within(audit).getByText('Trigger intent')).toBeInTheDocument();
  expect(within(audit).getByText('Trigger accepted')).toBeInTheDocument();
  expect(within(audit).getByText('safety gate accepted')).toBeInTheDocument();
  expect(within(audit).getByText('issue context loaded')).toBeInTheDocument();
  expect(
    within(audit).getByText('model accepted trigger: Issue context describes a concrete failing test')
  ).toBeInTheDocument();
});

test('shows pre-execution safety snapshot for accepted tasks', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        preExecutionSafetySnapshot: {
          eventId: 'timeline-trigger',
          source: 'ISSUE_COMMENT',
          finalDecision: 'ALLOWED',
          safetyDecision: 'safety gate accepted',
          quarantineDecision: 'not blocked before task creation',
          rateLimitDecision: 'not rate limited before task creation',
          issueContextStatus: 'issue context loaded',
          modelDecision: 'model accepted trigger: Issue context describes a concrete failing test',
          createdAt: '2026-06-20T01:00:30Z'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const snapshot = screen.getByLabelText('Pre-execution safety');
  expect(within(snapshot).getByText('Pre-execution safety')).toBeInTheDocument();
  expect(within(snapshot).getByText('ISSUE_COMMENT')).toBeInTheDocument();
  expect(within(snapshot).getByText('ALLOWED')).toBeInTheDocument();
  expect(within(snapshot).getByText('safety gate accepted')).toBeInTheDocument();
  expect(within(snapshot).getByText('not blocked before task creation')).toBeInTheDocument();
  expect(within(snapshot).getByText('not rate limited before task creation')).toBeInTheDocument();
  expect(within(snapshot).getByText('issue context loaded')).toBeInTheDocument();
  expect(
    within(snapshot).getByText('model accepted trigger: Issue context describes a concrete failing test')
  ).toBeInTheDocument();
});

test('labels failed task status comment as failure feedback', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'FAILED',
        failureReason: 'verification failed: npm test failed',
        pullRequestUrl: null,
        completedAt: null,
        statusCommentId: 123,
        statusCommentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-123'
      }}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByRole('link', { name: /Failure feedback/i })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-123'
  );
  expect(screen.queryByRole('link', { name: /Status Comment/i })).not.toBeInTheDocument();
});

test('shows failure diagnosis for failed tasks', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'FAILED',
        failureReason: 'maven tests failed: token=ghp_123456789012345678901234567890123456',
        pullRequestUrl: null,
        completedAt: null
      }}
      detail={{
        ...baseDetail,
        failureDiagnosis: {
          category: 'VERIFICATION_FAILED',
          nextAction: 'Inspect the verification output, fix the failing test or build error, then retry the task.',
          safeReason: 'maven tests failed: token=[REDACTED]'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const diagnosis = screen.getByLabelText('Failure diagnosis');
  expect(within(diagnosis).getByText('Failure diagnosis')).toBeInTheDocument();
  expect(within(diagnosis).getByText('Verification failed')).toBeInTheDocument();
  expect(
    within(diagnosis).getByText('Inspect the verification output, fix the failing test or build error, then retry the task.')
  ).toBeInTheDocument();
  expect(within(diagnosis).getByText('maven tests failed: token=[REDACTED]')).toBeInTheDocument();
  expect(within(diagnosis).queryByText('ghp_123456789012345678901234567890123456')).not.toBeInTheDocument();
});

test('blocks retry when retry preflight requires operator action', () => {
  const onRetryTask = vi.fn();

  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'FAILED',
        failureReason: 'GitHub token is required to create Pull Requests',
        pullRequestUrl: null,
        completedAt: null
      }}
      detail={{
        ...baseDetail,
        retryPreflight: {
          taskId: 'task-1',
          status: 'FAILED',
          retryable: false,
          category: 'GITHUB_OPERATION_FAILED',
          reason: 'GitHub token is required to create Pull Requests: token=[REDACTED]',
          operatorAction: 'Check GitHub token or App permissions, then retry the task after access is fixed.'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={onRetryTask}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const preflight = screen.getByLabelText('Retry preflight');
  expect(within(preflight).getByText('Retry blocked')).toBeInTheDocument();
  expect(within(preflight).getByText('GitHub operation failed')).toBeInTheDocument();
  expect(
    within(preflight).getByText('Check GitHub token or App permissions, then retry the task after access is fixed.')
  ).toBeInTheDocument();
  expect(within(preflight).getByText('GitHub token is required to create Pull Requests: token=[REDACTED]'))
    .toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Retry blocked' })).toBeDisabled();
  expect(onRetryTask).not.toHaveBeenCalled();
});

test('keeps retry available when retry preflight is clear', async () => {
  const onRetryTask = vi.fn();
  const user = userEvent.setup();

  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'FAILED',
        failureReason: 'maven tests failed',
        pullRequestUrl: null,
        completedAt: null
      }}
      detail={{
        ...baseDetail,
        retryPreflight: {
          taskId: 'task-1',
          status: 'FAILED',
          retryable: true,
          category: 'VERIFICATION_FAILED',
          reason: 'maven tests failed',
          operatorAction: 'Inspect the verification output, fix the failing test or build error, then retry the task.'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={onRetryTask}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const preflight = screen.getByLabelText('Retry preflight');
  expect(within(preflight).getByText('Ready to retry')).toBeInTheDocument();
  expect(within(preflight).getByText('Verification failed')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Retry task' })).toBeDisabled();

  await user.type(screen.getByLabelText('Retry reason'), 'Verified failing test and want a clean rerun');
  await user.click(screen.getByRole('button', { name: 'Retry task' }));

  expect(onRetryTask).toHaveBeenCalledWith('task-1', {
    reason: 'Verified failing test and want a clean rerun'
  });
});

test('shows patch review evidence for model generated edits', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        patchReview: {
          id: 'patch-review-1',
          taskId: 'task-1',
          decision: 'APPROVE',
          reason: 'The generated edit matches the issue and stays inside the planned file.',
          confidence: 'HIGH',
          requiredFollowUp: 'Run python3 -m pytest before opening the PR.',
          editedFiles: ['src/app.py', 'tests/test_app.py'],
          createdAt: '2026-06-20T01:00:14Z'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const patchReview = screen.getByLabelText('Patch review');
  expect(within(patchReview).getByText('Patch review')).toBeInTheDocument();
  expect(within(patchReview).getByText('APPROVE')).toBeInTheDocument();
  expect(within(patchReview).getByText('HIGH confidence')).toBeInTheDocument();
  expect(
    within(patchReview).getByText('The generated edit matches the issue and stays inside the planned file.')
  ).toBeInTheDocument();
  expect(within(patchReview).getByText('Run python3 -m pytest before opening the PR.')).toBeInTheDocument();
  expect(within(patchReview).getByText('src/app.py')).toBeInTheDocument();
  expect(within(patchReview).getByText('tests/test_app.py')).toBeInTheDocument();
});

test('marks rejected patch reviews as review gate blocks', () => {
  render(
    <TaskDetailPanel
      task={{ ...task, status: 'FAILED', failureReason: 'Model patch review rejected generated edits: unsafe edit' }}
      detail={{
        ...baseDetail,
        patchReview: {
          id: 'patch-review-2',
          taskId: 'task-1',
          decision: 'REJECT',
          reason: 'The patch changes an unrelated authentication file.',
          confidence: 'HIGH',
          requiredFollowUp: 'Regenerate an edit limited to the planned target files.',
          editedFiles: ['src/auth.ts'],
          createdAt: '2026-06-20T01:00:14Z'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const patchReview = screen.getByLabelText('Patch review');
  expect(within(patchReview).getByText('Review gate BLOCKED')).toBeInTheDocument();
  expect(within(patchReview).getByText('The patch changes an unrelated authentication file.')).toBeInTheDocument();
  expect(within(patchReview).getByText('Retry regenerates the patch instead of reusing this rejected edit.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Retry task' })).toBeInTheDocument();
});

test('shows retry lineage for tasks recovered from patch review rejection', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING',
        retrySourceTaskId: 'task-1',
        retrySourceStatus: 'FAILED',
        retrySourceFailureReason: 'Model patch review rejected generated edits: unrelated authentication change',
        retryReason: 'Regenerate a focused patch after reviewing the rejected edit',
        retriedAt: '2026-06-20T01:05:00Z'
      }}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const retryLineage = screen.getByLabelText('Retry lineage');
  expect(within(retryLineage).getByText('Retry lineage')).toBeInTheDocument();
  expect(within(retryLineage).getByText('Recovered from FAILED')).toBeInTheDocument();
  expect(
    within(retryLineage).getByText('Model patch review rejected generated edits: unrelated authentication change')
  ).toBeInTheDocument();
  expect(
    within(retryLineage).getByText('Regenerate a focused patch after reviewing the rejected edit')
  ).toBeInTheDocument();
});

test('shows issue context for selected task', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const issueContext = screen.getByLabelText('Issue context');
  expect(within(issueContext).getByText('Dashboard should show issue context')).toBeInTheDocument();
  expect(
    within(issueContext).getByText('The issue body explains why the dashboard needs to surface context before operators inspect evidence.')
  ).toBeInTheDocument();
  expect(within(issueContext).getByText('1 recent comments')).toBeInTheDocument();
  expect(within(issueContext).getByText('alice')).toBeInTheDocument();
  expect(within(issueContext).getByText('The latest reproduction is in the failing smoke test.')).toBeInTheDocument();
  expect(within(issueContext).getByRole('link', { name: /View source/ })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1'
  );
  expect(within(issueContext).getByRole('link', { name: /Comment/ })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-1001'
  );
});

test('shows selected task queue state in task detail', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Queue FAILED')).toBeInTheDocument();
  expect(screen.getByText('attempt 3')).toBeInTheDocument();
  expect(screen.getByText('maven tests failed')).toBeInTheDocument();
  const queueDetail = screen.getByText('Queue FAILED').closest('.queue-detail');
  expect(queueDetail).not.toBeNull();
  expect(within(queueDetail as HTMLElement).getByText(/Available /)).toBeInTheDocument();
  expect(within(queueDetail as HTMLElement).getByText(/Locked /)).toBeInTheDocument();
});

test('shows selected task queue history in task detail', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Queue History')).toBeInTheDocument();
  expect(screen.getByText('queue-1')).toBeInTheDocument();
  expect(screen.getByText('queue-older')).toBeInTheDocument();
  expect(screen.getByText('FAILED · attempt 3')).toBeInTheDocument();
  expect(screen.getByText('PENDING · attempt 1')).toBeInTheDocument();
  expect(screen.getAllByText(/maven tests failed/)).toHaveLength(2);
});

test('shows missing latest test evidence when no test result is recorded', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        summary: {
          ...baseSummary,
          testRunCount: 0,
          latestTestRunExitCode: null,
          latestTestRunDurationMs: null
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Tests 0')).toBeInTheDocument();
  expect(screen.getByText('Latest test None')).toBeInTheDocument();
});

test('shows unsupported repository guidance with supported adapter signals', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'FAILED',
        failureReason: 'Unsupported repository: no supported language adapter detected',
        language: null,
        buildSystem: null,
        verificationCommand: null,
        adapterDetectionReason: null
      }}
      detail={{
        ...baseDetail,
        repositorySupportGuidance: {
          status: 'UNSUPPORTED',
          reason: 'Unsupported repository: no supported language adapter detected',
          operatorAction: 'Add one supported project marker and deterministic test command, then trigger /agent fix again. PatchPilot will not run arbitrary commands for unsupported repositories.',
          supportedAdapters: [
            {
              language: 'java',
              buildSystem: 'maven',
              verificationCommand: ['mvn', 'test'],
              detectionSignals: ['pom.xml', 'mvnw'],
              demoFixturePath: 'docs/demo-repositories/java-maven',
              status: 'SUPPORTED'
            },
            {
              language: 'go',
              buildSystem: 'go',
              verificationCommand: ['go', 'test', './...'],
              detectionSignals: ['go.mod', '*_test.go'],
              demoFixturePath: 'docs/demo-repositories/go-module',
              status: 'SUPPORTED'
            }
          ]
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const guidance = screen.getByRole('region', { name: 'Repository support guidance' });
  expect(within(guidance).getByText('Repository support guidance')).toBeInTheDocument();
  expect(within(guidance).getByText('UNSUPPORTED')).toBeInTheDocument();
  expect(within(guidance).getByText('Unsupported repository: no supported language adapter detected')).toBeInTheDocument();
  expect(within(guidance).getByText(/PatchPilot will not run arbitrary commands/)).toBeInTheDocument();
  expect(within(guidance).getByText('java / maven')).toBeInTheDocument();
  expect(within(guidance).getByText('mvn test')).toBeInTheDocument();
  expect(within(guidance).getByText('pom.xml, mvnw')).toBeInTheDocument();
  expect(within(guidance).getByText('go / go')).toBeInTheDocument();
  expect(within(guidance).getByText('go test ./...')).toBeInTheDocument();
  expect(within(guidance).getByText('go.mod, *_test.go')).toBeInTheDocument();
});

test('surfaces generated diff risk gate failures in task evidence', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING_REVIEW',
        failureReason: 'Generated diff rejected: sensitive path .github/workflows/deploy.yml'
      }}
      detail={{
        ...baseDetail,
        toolCalls: [
          {
            id: 'tool-risk',
            taskId: 'task-1',
            toolName: 'GeneratedDiffRiskGate',
            inputSummary: 'changedBytes=480',
            outputSummary: 'Generated diff rejected: sensitive path .github/workflows/deploy.yml',
            success: false,
            startedAt: '2026-06-20T01:00:12Z',
            finishedAt: '2026-06-20T01:00:13Z',
            durationMs: 78
          }
        ]
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Risk gate BLOCKED')).toBeInTheDocument();
  expect(screen.getByText('GeneratedDiffRiskGate')).toBeInTheDocument();
  expect(screen.getByText('failed · 78 ms')).toBeInTheDocument();
  expect(screen.getByText('Generated diff rejected: sensitive path .github/workflows/deploy.yml')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Cancel task' })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Approve review' })).toBeInTheDocument();
  expect(screen.queryByRole('button', { name: 'Retry task' })).not.toBeInTheDocument();
});

test('shows generated diff preview before approving review tasks', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING_REVIEW',
        failureReason: 'Generated diff rejected: sensitive path .github/workflows/deploy.yml'
      }}
      detail={{
        ...baseDetail,
        generatedDiff: {
          toolCallId: 'tool-diff',
          diff: 'diff --git a/docs/demo.md b/docs/demo.md\n+PatchPilot smoke test',
          generatedAt: '2026-06-20T01:00:13Z'
        }
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  const diffPreview = screen.getByLabelText('Generated diff preview');
  const diffSection = diffPreview.closest('.generated-diff-section') as HTMLElement;
  expect(within(diffSection).getByText('Generated diff')).toBeInTheDocument();
  expect(within(diffSection).getByText('Review these changes before approving the task.')).toBeInTheDocument();
  expect(within(diffSection).getByText((_, element) => (
    element?.tagName.toLowerCase() === 'time' && element.textContent?.startsWith('Generated ')
  ))).toBeInTheDocument();
  expect(diffPreview).toHaveTextContent('+PatchPilot smoke test');
});

test('approves pending review tasks from the detail panel', async () => {
  const onApproveReview = vi.fn().mockResolvedValue(undefined);
  const user = userEvent.setup();
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING_REVIEW',
        failureReason: 'Generated diff rejected: sensitive path .github/workflows/deploy.yml'
      }}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={onApproveReview}
      onCopyReport={vi.fn()}
    />
  );

  await user.selectOptions(screen.getByLabelText('Approver'), 'release-captain');
  await user.type(
    screen.getByLabelText('Approval reason'),
    'Reviewed generated diff and accepted docs-only change'
  );
  await user.click(screen.getByRole('button', { name: 'Approve review' }));

  expect(onApproveReview).toHaveBeenCalledWith('task-1', {
    operator: 'release-captain',
    reason: 'Reviewed generated diff and accepted docs-only change'
  });
});

test('disables pending review approval when no configured approvers exist', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING_REVIEW',
        failureReason: 'Generated diff rejected: sensitive path .github/workflows/deploy.yml'
      }}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={[]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Configure review approval operators before approving this task.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Approve review' })).toBeDisabled();
});

test('shows review approval audit metadata after a task is approved', () => {
  render(
    <TaskDetailPanel
      task={{
        ...task,
        status: 'PENDING',
        riskReviewApprovedAt: '2026-06-20T01:09:00Z',
        riskReviewApprovedBy: 'release-captain',
        riskReviewApprovalReason: 'Reviewed generated diff and accepted docs-only change',
        retrySourceTaskId: null,
        retrySourceStatus: null,
        retrySourceFailureReason: null,
        retryReason: null,
        retriedAt: null
      }}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={['release-captain']}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Review approval')).toBeInTheDocument();
  expect(screen.getByText('release-captain')).toBeInTheDocument();
  expect(screen.getByText('Reviewed generated diff and accepted docs-only change')).toBeInTheDocument();
});

test('builds a shareable task link from the current dashboard URL', () => {
  expect(taskLinkFor('task-1', 'http://127.0.0.1:5173/?status=FAILED')).toBe(
    'http://127.0.0.1:5173/tasks/task-1?status=FAILED'
  );
});

test('builds a shareable task link with encoded task id and hash fragment', () => {
  expect(taskLinkFor('task/with spaces', 'http://127.0.0.1:5173/tasks/old-task?status=FAILED#timeline')).toBe(
    'http://127.0.0.1:5173/tasks/task%2Fwith%20spaces?status=FAILED#timeline'
  );
});

test('copies the selected task deep link', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  window.history.replaceState(null, '', '/?status=FAILED');

  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: /copy link/i }));

  expect(writeText).toHaveBeenCalledWith('http://localhost:3000/tasks/task-1?status=FAILED');
  expect(screen.getByText('Task link copied')).toBeInTheDocument();
});

test('copies the selected task report', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyReport = vi.fn().mockResolvedValue('# PatchPilot Task Report\n\n- Task: `task-1`');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={onCopyReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: /copy report/i }));

  expect(onCopyReport).toHaveBeenCalledWith('task-1');
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Task Report\n\n- Task: `task-1`');
  expect(screen.getByText('Task report copied')).toBeInTheDocument();
});

test('downloads archives and re-downloads selected task evidence packages', async () => {
  const user = userEvent.setup();
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:task-evidence-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadReport = vi.fn(async () => new Blob(['current report'], { type: 'text/markdown' }));
  const onArchiveEvidencePackage = vi.fn(async () => evidenceArchive);
  const onDownloadEvidencePackageReport = vi.fn(async () => new Blob(['archived report'], { type: 'text/markdown' }));

  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        evidencePackageArchives: [evidenceArchive]
      }}
      loading={false}
      actionInFlight={false}
      reviewApprovalAllowedOperators={["release-captain"]}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onApproveReview={vi.fn()}
      onCopyReport={vi.fn()}
      onDownloadReport={onDownloadReport}
      onArchiveEvidencePackage={onArchiveEvidencePackage}
      onDownloadEvidencePackageReport={onDownloadEvidencePackageReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download report' }));
  expect(onDownloadReport).toHaveBeenCalledWith('task-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(click).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Task report downloaded')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Archive evidence' }));
  expect(onArchiveEvidencePackage).toHaveBeenCalledWith('task-1');
  expect(screen.getByText('Evidence archived task-evidence-archive-1')).toBeInTheDocument();

  const archiveRegion = screen.getByLabelText('Task evidence packages');
  expect(within(archiveRegion).getByText('Task evidence packages')).toBeInTheDocument();
  expect(within(archiveRegion).getByText('task-evidence-archive-1')).toBeInTheDocument();
  expect(within(archiveRegion).getByText('Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.'))
    .toBeInTheDocument();
  await user.click(within(archiveRegion).getByRole('button', {
    name: 'Download archived evidence task-evidence-archive-1'
  }));
  expect(onDownloadEvidencePackageReport).toHaveBeenCalledWith('task-evidence-archive-1');
  expect(click).toHaveBeenCalledTimes(2);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:task-evidence-package');
  expect(screen.getByText('Archived evidence task-evidence-archive-1 downloaded')).toBeInTheDocument();
});
