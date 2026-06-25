import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoLaunchPreflight } from '../../types';
import { DemoLaunchPreflightPanel } from './DemoLaunchPreflightPanel';

const readyPreflight: DemoLaunchPreflight = {
  status: 'READY',
  readyToPost: true,
  summary: 'Demo launch preflight is ready to post the tested /agent fix comment.',
  readiness: {
    status: 'READY',
    summary: 'PatchPilot is ready for a live demo.',
    checks: [],
    nextActions: []
  },
  triggerEvaluation: {
    status: 'WOULD_CREATE_TASK',
    source: 'ISSUE_COMMENT',
    wouldCreateTask: true,
    blockedReason: null,
    blockedCategory: null,
    safetyDecision: {
      allowed: true,
      reason: 'Accepted',
      category: 'UNKNOWN'
    },
    activeTaskDecision: {
      allowed: true,
      reason: 'No active task exists for this issue',
      category: 'UNKNOWN'
    },
    quarantineDecision: {
      allowed: true,
      reason: 'Trigger quarantine accepted',
      category: 'UNKNOWN'
    },
    rateLimitDecision: {
      allowed: true,
      reason: 'Trigger rate limit accepted',
      category: 'UNKNOWN'
    },
    triggerIntentDecision: {
      allowed: true,
      reason: 'Model trigger classification accepted',
      category: 'UNKNOWN'
    },
    issueContextLoaded: true,
    nextAction: 'Create task is allowed for this trigger.'
  },
  nextActions: ['Post the tested /agent fix comment on the controlled GitHub issue.']
};

const blockedPreflight: DemoLaunchPreflight = {
  ...readyPreflight,
  status: 'BLOCKED',
  readyToPost: false,
  summary: 'Demo launch preflight is blocked because the tested /agent fix comment would not create a task.',
  triggerEvaluation: {
    ...readyPreflight.triggerEvaluation,
    status: 'BLOCKED',
    wouldCreateTask: false,
    blockedReason: 'Trigger user intruder is not allowed',
    blockedCategory: 'TRIGGER_USER_NOT_ALLOWED',
    safetyDecision: {
      allowed: false,
      reason: 'Trigger user intruder is not allowed',
      category: 'TRIGGER_USER_NOT_ALLOWED'
    },
    activeTaskDecision: null,
    quarantineDecision: null,
    rateLimitDecision: null,
    triggerIntentDecision: null,
    nextAction: 'Revise the /agent fix request before creating a task.'
  },
  nextActions: [
    'Revise the tested /agent fix comment: Trigger user intruder is not allowed.',
    'Revise the /agent fix request before creating a task.'
  ]
};

test('runs demo launch preflight for the exact issue comment that will be posted', async () => {
  const user = userEvent.setup();
  const onRunPreflight = vi.fn(async () => readyPreflight);

  render(
    <DemoLaunchPreflightPanel
      result={null}
      error={null}
      pending={false}
      onRunPreflight={onRunPreflight}
    />
  );

  await user.clear(screen.getByLabelText('Repository owner'));
  await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
  await user.clear(screen.getByLabelText('Repository name'));
  await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
  await user.clear(screen.getByLabelText('Issue number'));
  await user.type(screen.getByLabelText('Issue number'), '1');
  await user.clear(screen.getByLabelText('Trigger user'));
  await user.type(screen.getByLabelText('Trigger user'), 'bingqin2');
  await user.clear(screen.getByLabelText('GitHub issue comment'));
  await user.type(screen.getByLabelText('GitHub issue comment'), '/agent fix replace docs/demo.md PatchPilot smoke test');
  await user.click(screen.getByRole('button', { name: 'Run launch preflight' }));

  expect(onRunPreflight).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test'
  });
});

test('shows ready and blocked demo launch preflight outcomes', () => {
  const { rerender } = render(
    <DemoLaunchPreflightPanel
      result={readyPreflight}
      error={null}
      pending={false}
      onRunPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch preflight' });
  expect(within(panel).getByText('Ready to post')).toBeInTheDocument();
  expect(within(panel).getByText('WOULD_CREATE_TASK')).toBeInTheDocument();
  expect(within(panel).getByText('Post the tested /agent fix comment on the controlled GitHub issue.')).toBeInTheDocument();

  rerender(
    <DemoLaunchPreflightPanel
      result={blockedPreflight}
      error="Trigger user intruder is not allowed"
      pending={false}
      onRunPreflight={vi.fn()}
    />
  );

  expect(within(panel).getByText('Not ready')).toBeInTheDocument();
  expect(within(panel).getByText('BLOCKED')).toBeInTheDocument();
  expect(within(panel).getAllByText('Trigger user intruder is not allowed')).toHaveLength(2);
  expect(
    within(panel).getByText('Revise the tested /agent fix comment: Trigger user intruder is not allowed.')
  ).toBeInTheDocument();
});

test('copies demo launch preflight evidence as markdown', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoLaunchPreflightPanel
      result={readyPreflight}
      error={null}
      pending={false}
      onRunPreflight={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy launch preflight report' }));

  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('# PatchPilot Demo Launch Preflight Report'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Status: `READY`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Ready to post: `YES`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Trigger evaluation: `WOULD_CREATE_TASK`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Source: `ISSUE_COMMENT`'));
  expect(writeText).toHaveBeenCalledWith(
    expect.stringContaining('- Next action: Post the tested /agent fix comment on the controlled GitHub issue.')
  );
});
