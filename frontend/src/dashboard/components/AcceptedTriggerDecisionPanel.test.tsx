import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AcceptedTriggerDecisionPanel } from './AcceptedTriggerDecisionPanel';
import type { AcceptedTriggerDecision } from '../../types';

const acceptedDecisions: AcceptedTriggerDecision[] = [
  {
    id: 'decision-1',
    taskId: 'task-1',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 17,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix update failing parser test',
    taskStatus: 'COMPLETED',
    source: 'ISSUE_COMMENT',
    finalDecision: 'ALLOWED',
    safetyDecision: { allowed: true, reason: 'safety gate accepted', category: 'UNKNOWN' },
    activeTaskDecision: { allowed: true, reason: 'No active task exists for this issue', category: 'UNKNOWN' },
    quarantineDecision: { allowed: true, reason: 'not blocked before task creation', category: 'UNKNOWN' },
    rateLimitDecision: { allowed: true, reason: 'not rate limited before task creation', category: 'UNKNOWN' },
    triggerIntentDecision: {
      allowed: true,
      reason: 'model accepted trigger: Issue context describes a concrete failing test',
      category: 'UNKNOWN'
    },
    issueContextLoaded: true,
    createdAt: '2026-06-20T01:00:30Z'
  }
];

test('renders accepted trigger decisions and opens the related task', async () => {
  const user = userEvent.setup();
  const onSelectTask = vi.fn();

  render(<AcceptedTriggerDecisionPanel decisions={acceptedDecisions} error={null} onSelectTask={onSelectTask} />);

  const panel = screen.getByRole('region', { name: 'Accepted trigger audit' });
  expect(within(panel).getByText('1 recent accepted triggers')).toBeInTheDocument();
  expect(within(panel).getByText('bingqin2/PatchPilot #17')).toBeInTheDocument();
  expect(within(panel).getByText('/agent fix update failing parser test')).toBeInTheDocument();
  expect(within(panel).getByText('safety gate accepted')).toBeInTheDocument();
  expect(within(panel).getByText('model accepted trigger: Issue context describes a concrete failing test')).toBeInTheDocument();
  expect(within(panel).getByText('loaded')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', { name: 'Open task' }));

  expect(onSelectTask).toHaveBeenCalledWith('task-1');
});

test('renders accepted trigger empty and error states', () => {
  render(
    <AcceptedTriggerDecisionPanel
      decisions={[]}
      error="Accepted trigger decision API unavailable"
      onSelectTask={vi.fn()}
    />
  );

  expect(screen.getByText('No accepted triggers recorded')).toBeInTheDocument();
  expect(screen.getByText('Accepted trigger decision API unavailable')).toBeInTheDocument();
  expect(screen.getByText('No accepted `/agent fix` trigger decisions recorded yet.')).toBeInTheDocument();
});
