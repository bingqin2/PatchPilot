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
});
