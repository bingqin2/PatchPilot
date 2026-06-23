import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { RejectedTriggerPanel } from './RejectedTriggerPanel';

test('renders rejected trigger audit rows and retries a rejected trigger', async () => {
  const user = userEvent.setup();
  const onRetryRejectedTrigger = vi.fn();
  const onSelectTask = vi.fn();
  const onCategoryFilterChange = vi.fn();

  render(
    <RejectedTriggerPanel
      error={null}
      quarantines={[
        {
          id: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          reason: 'Unsafe request rejected: trigger user is temporarily quarantined',
          category: 'ABUSE_QUARANTINED',
          evidenceCount: 5,
          windowMs: 600000,
          startedAt: '2026-06-20T01:03:00Z',
          expiresAt: '2026-06-20T01:33:00Z',
          createdAt: '2026-06-20T01:03:00Z',
          updatedAt: '2026-06-20T01:08:00Z',
          createdBy: null,
          releasedAt: null,
          releasedBy: null,
          releaseReason: null,
          active: true
        }
      ]}
      operatorSafetyAudits={[
        {
          id: 'operator-audit-1',
          action: 'MANUAL_QUARANTINE_CREATED',
          resourceType: 'TRIGGER_QUARANTINE',
          resourceId: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          operator: 'local-admin',
          reason: 'Operator blocked noisy demo trigger user',
          createdAt: '2026-06-24T01:00:00Z'
        }
      ]}
      summary={{
        totalCount: 4,
        categoryCounts: [
          { value: 'NOT_ACTIONABLE', count: 2 },
          { value: 'ABUSE_QUARANTINED', count: 1 },
          { value: 'DANGEROUS_INSTRUCTION', count: 1 },
          { value: 'TRIGGER_USER_NOT_ALLOWED', count: 1 }
        ],
        sourceCounts: [
          { value: 'webhook', count: 3 },
          { value: 'manual', count: 1 }
        ],
        triggerUserCounts: [
          { value: 'drive-by-user', count: 3 },
          { value: 'local-operator', count: 1 }
        ],
        repositoryCounts: [{ value: 'bingqin2/PatchPilot', count: 4 }]
      }}
      categoryFilter="ALL"
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={onRetryRejectedTrigger}
      onSelectTask={onSelectTask}
      onCategoryFilterChange={onCategoryFilterChange}
      onCreateTriggerQuarantine={vi.fn()}
      onReleaseTriggerQuarantine={vi.fn()}
      creatingTriggerQuarantine={false}
      releasingTriggerQuarantineId={null}
      rejectedTriggers={[
        {
          id: 'rejected-1',
          source: 'webhook',
          deliveryId: 'delivery-rejected',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'drive-by-user',
          triggerComment: '/agent fix make it better',
          category: 'ABUSE_QUARANTINED',
          reason: 'Unsafe request rejected: trigger user is temporarily quarantined',
          commentId: 456,
          commentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
          retriedTaskId: 'task-from-rejected-1',
          retriedAt: '2026-06-20T01:08:05Z',
          createdAt: '2026-06-20T01:03:05Z'
        }
      ]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Rejected triggers' });
  expect(within(panel).getByText('1 recent rejection')).toBeInTheDocument();
  const summary = within(panel).getByRole('group', { name: 'Rejected trigger summary' });
  expect(within(summary).getByText('Rejected trigger summary')).toBeInTheDocument();
  expect(within(summary).getByText('4 rejected triggers analyzed')).toBeInTheDocument();
  expect(within(summary).getByRole('button', { name: 'Filter by Dangerous instruction, 1 rejected trigger' })).toBeInTheDocument();
  expect(within(summary).getByRole('button', { name: 'Filter by Abuse quarantined, 1 rejected trigger' })).toBeInTheDocument();
  expect(within(summary).getByText('drive-by-user')).toBeInTheDocument();
  expect(within(summary).getByText('bingqin2/PatchPilot')).toBeInTheDocument();
  const quarantineRows = within(panel).getByRole('group', { name: 'Active trigger quarantines' });
  expect(within(quarantineRows).getByText('Active trigger quarantines')).toBeInTheDocument();
  expect(within(quarantineRows).getByText('drive-by-user')).toBeInTheDocument();
  expect(within(quarantineRows).getByText('5 rejected triggers')).toBeInTheDocument();
  expect(within(quarantineRows).getByText('Unsafe request rejected: trigger user is temporarily quarantined')).toBeInTheDocument();
  const operatorAuditRows = within(panel).getByRole('group', { name: 'Operator safety audit rows' });
  expect(within(operatorAuditRows).getByText('Manual quarantine created')).toBeInTheDocument();
  expect(within(operatorAuditRows).getByText('drive-by-user')).toBeInTheDocument();
  expect(within(operatorAuditRows).getByText('local-admin')).toBeInTheDocument();
  expect(within(operatorAuditRows).getByText('Operator blocked noisy demo trigger user')).toBeInTheDocument();
  expect(within(panel).getByRole('combobox', { name: 'Filter rejected triggers by category' })).toHaveValue('ALL');
  const auditRows = within(panel).getByRole('group', { name: 'Rejected trigger audit rows' });
  expect(within(auditRows).getByText('/agent fix make it better')).toBeInTheDocument();
  expect(within(auditRows).getByText('bingqin2/PatchPilot #1')).toBeInTheDocument();
  expect(within(auditRows).getByText('drive-by-user')).toBeInTheDocument();
  expect(within(auditRows).getByText('delivery-rejected')).toBeInTheDocument();
  expect(within(auditRows).getByText('Unsafe request rejected: trigger user is temporarily quarantined')).toBeInTheDocument();
  expect(within(auditRows).getByText('Abuse quarantined')).toBeInTheDocument();
  expect(within(auditRows).getByRole('link', { name: 'Refusal comment' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456'
  );
  expect(within(auditRows).getByRole('link', { name: 'Retried task' })).toHaveAttribute(
    'href',
    '/tasks/task-from-rejected-1'
  );

  await user.click(within(auditRows).getByRole('link', { name: 'Retried task' }));

  expect(onSelectTask).toHaveBeenCalledWith('task-from-rejected-1');

  await user.click(within(auditRows).getByRole('button', { name: 'Retry trigger' }));

  expect(onRetryRejectedTrigger).toHaveBeenCalledWith('rejected-1');

  await user.selectOptions(
    within(panel).getByRole('combobox', { name: 'Filter rejected triggers by category' }),
    'DANGEROUS_INSTRUCTION'
  );

  expect(onCategoryFilterChange).toHaveBeenCalledWith('DANGEROUS_INSTRUCTION');

  await user.click(within(summary).getByRole('button', { name: 'Filter by Dangerous instruction, 1 rejected trigger' }));

  expect(onCategoryFilterChange).toHaveBeenCalledWith('DANGEROUS_INSTRUCTION');

  await user.click(within(summary).getByRole('button', { name: 'Filter by Abuse quarantined, 1 rejected trigger' }));

  expect(onCategoryFilterChange).toHaveBeenCalledWith('ABUSE_QUARANTINED');
});

test('renders rejected trigger empty and error states', () => {
  const { rerender } = render(
    <RejectedTriggerPanel
      error={null}
      quarantines={[]}
      operatorSafetyAudits={[]}
      summary={null}
      categoryFilter="ALL"
      rejectedTriggers={[]}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={vi.fn()}
      onSelectTask={vi.fn()}
      onCategoryFilterChange={vi.fn()}
      onCreateTriggerQuarantine={vi.fn()}
      onReleaseTriggerQuarantine={vi.fn()}
      creatingTriggerQuarantine={false}
      releasingTriggerQuarantineId={null}
    />
  );

  expect(screen.getByText('No recent rejections')).toBeInTheDocument();
  expect(screen.getByText('No rejected /agent fix triggers recorded.')).toBeInTheDocument();

  rerender(
    <RejectedTriggerPanel
      error="Rejected trigger API unavailable"
      quarantines={[]}
      operatorSafetyAudits={[]}
      summary={null}
      categoryFilter="ALL"
      rejectedTriggers={[]}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={vi.fn()}
      onSelectTask={vi.fn()}
      onCategoryFilterChange={vi.fn()}
      onCreateTriggerQuarantine={vi.fn()}
      onReleaseTriggerQuarantine={vi.fn()}
      creatingTriggerQuarantine={false}
      releasingTriggerQuarantineId={null}
    />
  );

  expect(screen.getByText('Rejected trigger API unavailable')).toBeInTheDocument();
});

test('disables the retry action while retrying a rejected trigger', () => {
  render(
    <RejectedTriggerPanel
      error={null}
      quarantines={[]}
      operatorSafetyAudits={[]}
      summary={null}
      categoryFilter="NOT_ACTIONABLE"
      retryingRejectedTriggerId="rejected-1"
      onRetryRejectedTrigger={vi.fn()}
      onSelectTask={vi.fn()}
      onCategoryFilterChange={vi.fn()}
      onCreateTriggerQuarantine={vi.fn()}
      onReleaseTriggerQuarantine={vi.fn()}
      creatingTriggerQuarantine={false}
      releasingTriggerQuarantineId={null}
      rejectedTriggers={[
        {
          id: 'rejected-1',
          source: 'webhook',
          deliveryId: 'delivery-rejected',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'drive-by-user',
          triggerComment: '/agent fix make it better',
          category: 'NOT_ACTIONABLE',
          reason: 'Unsafe request rejected: instruction is not actionable',
          commentId: null,
          commentUrl: null,
          retriedTaskId: null,
          retriedAt: null,
          createdAt: '2026-06-20T01:03:05Z'
        }
      ]}
    />
  );

  expect(screen.getByRole('button', { name: 'Retrying trigger' })).toBeDisabled();
  expect(screen.getByRole('combobox', { name: 'Filter rejected triggers by category' })).toHaveValue('NOT_ACTIONABLE');
});

test('creates and releases manual trigger quarantines', async () => {
  const user = userEvent.setup();
  const onCreateTriggerQuarantine = vi.fn();
  const onReleaseTriggerQuarantine = vi.fn();

  render(
    <RejectedTriggerPanel
      error={null}
      quarantines={[
        {
          id: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          reason: 'Operator blocked noisy demo trigger user',
          category: 'MANUAL_QUARANTINE',
          evidenceCount: 0,
          windowMs: 0,
          startedAt: '2026-06-24T01:00:00Z',
          expiresAt: '2026-06-24T01:30:00Z',
          createdAt: '2026-06-24T01:00:00Z',
          updatedAt: '2026-06-24T01:00:00Z',
          createdBy: 'local-admin',
          releasedAt: null,
          releasedBy: null,
          releaseReason: null,
          active: true
        }
      ]}
      operatorSafetyAudits={[]}
      summary={null}
      categoryFilter="ALL"
      rejectedTriggers={[]}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={vi.fn()}
      onSelectTask={vi.fn()}
      onCategoryFilterChange={vi.fn()}
      onCreateTriggerQuarantine={onCreateTriggerQuarantine}
      onReleaseTriggerQuarantine={onReleaseTriggerQuarantine}
      creatingTriggerQuarantine={false}
      releasingTriggerQuarantineId={null}
    />
  );

  const panel = screen.getByRole('region', { name: 'Rejected triggers' });
  await user.selectOptions(within(panel).getByRole('combobox', { name: 'Manual quarantine scope' }), 'REPOSITORY');
  await user.type(within(panel).getByLabelText('Manual quarantine target'), 'bingqin2/PatchPilot');
  await user.type(within(panel).getByLabelText('Manual quarantine reason'), 'Blocking noisy demo repository');
  await user.clear(within(panel).getByLabelText('Manual quarantine duration minutes'));
  await user.type(within(panel).getByLabelText('Manual quarantine duration minutes'), '45');
  await user.type(within(panel).getByLabelText('Manual quarantine operator'), 'local-admin');
  await user.click(within(panel).getByRole('button', { name: 'Create quarantine' }));

  expect(onCreateTriggerQuarantine).toHaveBeenCalledWith({
    scope: 'REPOSITORY',
    scopeKey: 'bingqin2/PatchPilot',
    reason: 'Blocking noisy demo repository',
    durationMs: 2700000,
    operator: 'local-admin'
  });

  await user.click(within(panel).getByRole('button', { name: 'Release drive-by-user quarantine' }));

  expect(onReleaseTriggerQuarantine).toHaveBeenCalledWith('quarantine-1', {
    operator: 'local-admin',
    reason: 'Operator released active quarantine from dashboard'
  });
});
