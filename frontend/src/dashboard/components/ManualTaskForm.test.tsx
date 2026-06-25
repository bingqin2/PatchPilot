import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, test, vi } from 'vitest';
import { ManualTaskForm } from './ManualTaskForm';
import type { TriggerEvaluationResult } from '../../types';

const allowedEvaluation: TriggerEvaluationResult = {
  status: 'WOULD_CREATE_TASK',
  source: 'MANUAL',
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
    reason: 'Model trigger classification is disabled',
    category: 'UNKNOWN'
  },
  issueContextLoaded: false,
  nextAction: 'Create task is allowed for this trigger.'
};

const blockedEvaluation: TriggerEvaluationResult = {
  ...allowedEvaluation,
  status: 'BLOCKED',
  source: 'ISSUE_COMMENT',
  wouldCreateTask: false,
  blockedReason: 'Unsafe request rejected: destructive or secret-exfiltration instruction',
  blockedCategory: 'DANGEROUS_INSTRUCTION',
  safetyDecision: {
    allowed: false,
    reason: 'Unsafe request rejected: destructive or secret-exfiltration instruction',
    category: 'DANGEROUS_INSTRUCTION'
  },
  activeTaskDecision: null,
  quarantineDecision: null,
  rateLimitDecision: null,
  triggerIntentDecision: null,
  nextAction: 'Revise the /agent fix request before creating a task.'
};

describe('ManualTaskForm', () => {
  test('evaluates a trigger before creating a task', async () => {
    const user = userEvent.setup();
    const onEvaluateTrigger = vi.fn(async () => allowedEvaluation);

    render(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={null}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={onEvaluateTrigger}
      />
    );

    await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
    await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
    await user.type(screen.getByLabelText('Issue number'), '7');
    await user.clear(screen.getByLabelText('Trigger user'));
    await user.type(screen.getByLabelText('Trigger user'), 'local-operator');
    await user.type(screen.getByLabelText('Command'), '/agent fix touch docs/manual-task.md');
    await user.click(screen.getByRole('button', { name: /evaluate trigger/i }));

    expect(onEvaluateTrigger).toHaveBeenCalledWith({
      source: 'MANUAL',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 7,
      triggerUser: 'local-operator',
      triggerComment: '/agent fix touch docs/manual-task.md'
    });
  });

  test('evaluates a GitHub issue comment preview source', async () => {
    const user = userEvent.setup();
    const onEvaluateTrigger = vi.fn(async () => ({
      ...allowedEvaluation,
      source: 'ISSUE_COMMENT' as const
    }));

    render(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={null}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={onEvaluateTrigger}
      />
    );

    await user.click(screen.getByRole('radio', { name: /github issue comment/i }));
    await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
    await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
    await user.type(screen.getByLabelText('Issue number'), '7');
    await user.clear(screen.getByLabelText('Trigger user'));
    await user.type(screen.getByLabelText('Trigger user'), 'alice');
    await user.type(screen.getByLabelText('Command'), '/agent fix touch docs/webhook-preview.md');
    await user.click(screen.getByRole('button', { name: /evaluate trigger/i }));

    expect(onEvaluateTrigger).toHaveBeenCalledWith({
      source: 'ISSUE_COMMENT',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 7,
      triggerUser: 'alice',
      triggerComment: '/agent fix touch docs/webhook-preview.md'
    });
  });

  test('renders allowed and blocked evaluation results', () => {
    const { rerender } = render(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={allowedEvaluation}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={vi.fn()}
      />
    );

    const allowedRegion = screen.getByLabelText('Trigger evaluation result');
    expect(within(allowedRegion).getByText('Would create task')).toBeInTheDocument();
    expect(within(allowedRegion).getByText('Manual API')).toBeInTheDocument();
    expect(within(allowedRegion).getByText('Create task is allowed for this trigger.')).toBeInTheDocument();
    expect(within(allowedRegion).getByText('Model trigger classification is disabled')).toBeInTheDocument();

    rerender(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={blockedEvaluation}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={vi.fn()}
      />
    );

    const blockedRegion = screen.getByLabelText('Trigger evaluation result');
    expect(within(blockedRegion).getByText('Blocked')).toBeInTheDocument();
    expect(within(blockedRegion).getByText('GitHub issue comment')).toBeInTheDocument();
    expect(
      within(blockedRegion).getByText('Unsafe request rejected: destructive or secret-exfiltration instruction')
    ).toBeInTheDocument();
    expect(within(blockedRegion).getByText('DANGEROUS_INSTRUCTION')).toBeInTheDocument();
  });

  test('copies a manual trigger evaluation report', async () => {
    const user = userEvent.setup();
    const writeText = vi.fn();
    Object.defineProperty(navigator, 'clipboard', {
      configurable: true,
      value: { writeText }
    });

    render(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={allowedEvaluation}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={vi.fn()}
      />
    );

    await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
    await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
    await user.type(screen.getByLabelText('Issue number'), '7');
    await user.clear(screen.getByLabelText('Trigger user'));
    await user.type(screen.getByLabelText('Trigger user'), 'local-operator');
    await user.type(screen.getByLabelText('Command'), '/agent fix touch docs/manual-task.md');
    await user.click(screen.getByRole('button', { name: 'Copy evaluation report' }));

    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('# PatchPilot Trigger Evaluation Report'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Status: `WOULD_CREATE_TASK`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Source: `MANUAL`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Repository: `bingqin2/PatchPilot`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Issue: `#7`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Trigger user: `local-operator`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Command: `/agent fix touch docs/manual-task.md`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Safety: `ALLOW` - Accepted'));
    expect(writeText).toHaveBeenCalledWith(
      expect.stringContaining('- Model: `ALLOW` - Model trigger classification is disabled')
    );
    expect(writeText).toHaveBeenCalledWith(
      expect.stringContaining('- Next action: Create task is allowed for this trigger.')
    );
  });

  test('copies a blocked trigger evaluation report', async () => {
    const user = userEvent.setup();
    const writeText = vi.fn();
    Object.defineProperty(navigator, 'clipboard', {
      configurable: true,
      value: { writeText }
    });

    render(
      <ManualTaskForm
        creating={false}
        evaluating={false}
        evaluation={blockedEvaluation}
        successMessage={null}
        onCreateTask={vi.fn()}
        onEvaluateTrigger={vi.fn()}
      />
    );

    await user.click(screen.getByRole('radio', { name: /github issue comment/i }));
    await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
    await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
    await user.type(screen.getByLabelText('Issue number'), '8');
    await user.clear(screen.getByLabelText('Trigger user'));
    await user.type(screen.getByLabelText('Trigger user'), 'abuse-user');
    await user.type(screen.getByLabelText('Command'), '/agent fix print secrets');
    await user.click(screen.getByRole('button', { name: 'Copy evaluation report' }));

    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Status: `BLOCKED`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Source: `ISSUE_COMMENT`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Would create task: `NO`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Trigger user: `abuse-user`'));
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Blocked category: `DANGEROUS_INSTRUCTION`'));
    expect(writeText).toHaveBeenCalledWith(
      expect.stringContaining('- Blocked reason: Unsafe request rejected: destructive or secret-exfiltration instruction')
    );
    expect(writeText).toHaveBeenCalledWith(
      expect.stringContaining('- Safety: `BLOCK` - Unsafe request rejected: destructive or secret-exfiltration instruction')
    );
    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Active task: `NOT_EVALUATED`'));
    expect(writeText).toHaveBeenCalledWith(
      expect.stringContaining('- Next action: Revise the /agent fix request before creating a task.')
    );
  });
});
