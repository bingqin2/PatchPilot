import { SearchCheck, Send } from 'lucide-react';
import { FormEvent, useState } from 'react';
import type { CreateTaskInput, TriggerEvaluationResult } from '../../types';

interface ManualTaskFormProps {
  creating: boolean;
  evaluating: boolean;
  evaluation: TriggerEvaluationResult | null;
  successMessage: string | null;
  onCreateTask: (input: CreateTaskInput) => Promise<void>;
  onEvaluateTrigger: (input: CreateTaskInput) => Promise<TriggerEvaluationResult>;
}

export function ManualTaskForm({
  creating,
  evaluating,
  evaluation,
  successMessage,
  onCreateTask,
  onEvaluateTrigger
}: ManualTaskFormProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('');
  const [repositoryName, setRepositoryName] = useState('');
  const [issueNumber, setIssueNumber] = useState('');
  const [triggerUser, setTriggerUser] = useState('local-operator');
  const [triggerComment, setTriggerComment] = useState('');

  function input(): CreateTaskInput {
    return {
      repositoryOwner: repositoryOwner.trim(),
      repositoryName: repositoryName.trim(),
      issueNumber: Number(issueNumber),
      triggerUser: triggerUser.trim(),
      triggerComment: triggerComment.trim()
    };
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      await onCreateTask(input());
      setTriggerComment('');
    } catch {
      // App owns the visible error; keep form values for correction and retry.
    }
  }

  async function evaluate() {
    await onEvaluateTrigger(input());
  }

  return (
    <section className="panel manual-task-panel" aria-label="Manual task creation">
      <div className="panel-header">
        <div>
          <h2>Manual Task</h2>
          <p>Queue a local task without posting a GitHub issue comment.</p>
        </div>
        {successMessage ? <span className="manual-task-status">{successMessage}</span> : null}
      </div>
      <form className="manual-task-form" onSubmit={(event) => void submit(event)}>
        <label htmlFor="manual-repository-owner">
          Repository owner
          <input
            id="manual-repository-owner"
            value={repositoryOwner}
            onChange={(event) => setRepositoryOwner(event.target.value)}
            required
          />
        </label>
        <label htmlFor="manual-repository-name">
          Repository name
          <input
            id="manual-repository-name"
            value={repositoryName}
            onChange={(event) => setRepositoryName(event.target.value)}
            required
          />
        </label>
        <label htmlFor="manual-issue-number">
          Issue number
          <input
            id="manual-issue-number"
            min="1"
            type="number"
            value={issueNumber}
            onChange={(event) => setIssueNumber(event.target.value)}
            required
          />
        </label>
        <label htmlFor="manual-trigger-user">
          Trigger user
          <input
            id="manual-trigger-user"
            value={triggerUser}
            onChange={(event) => setTriggerUser(event.target.value)}
            required
          />
        </label>
        <label className="manual-task-command" htmlFor="manual-trigger-command">
          Command
          <input
            id="manual-trigger-command"
            value={triggerComment}
            onChange={(event) => setTriggerComment(event.target.value)}
            placeholder="/agent fix touch docs/manual-task.md"
            required
          />
        </label>
        <div className="manual-task-actions">
          <button
            className="secondary-button"
            type="button"
            disabled={evaluating || creating}
            onClick={() => void evaluate()}
          >
            <SearchCheck size={16} />
            {evaluating ? 'Evaluating trigger' : 'Evaluate trigger'}
          </button>
          <button className="icon-button" type="submit" disabled={creating}>
            <Send size={16} />
            {creating ? 'Creating task' : 'Create task'}
          </button>
        </div>
      </form>
      {evaluation ? <TriggerEvaluationSummary evaluation={evaluation} /> : null}
    </section>
  );
}

function TriggerEvaluationSummary({ evaluation }: { evaluation: TriggerEvaluationResult }) {
  return (
    <section
      className={`trigger-evaluation-result ${evaluation.wouldCreateTask ? 'is-allowed' : 'is-blocked'}`}
      aria-label="Trigger evaluation result"
    >
      <div className="trigger-evaluation-heading">
        <strong>{evaluation.wouldCreateTask ? 'Would create task' : 'Blocked'}</strong>
        {evaluation.blockedCategory ? <span>{evaluation.blockedCategory}</span> : null}
      </div>
      <p>{evaluation.wouldCreateTask ? 'Ready to create task.' : 'Review the gate details below before retrying.'}</p>
      <div className="trigger-evaluation-grid">
        <DecisionItem label="Safety" decision={evaluation.safetyDecision} />
        <DecisionItem label="Active task" decision={evaluation.activeTaskDecision} />
        <DecisionItem label="Quarantine" decision={evaluation.quarantineDecision} />
        <DecisionItem label="Rate limit" decision={evaluation.rateLimitDecision} />
        <DecisionItem label="Model" decision={evaluation.triggerIntentDecision} />
        <div>
          <span>Issue context</span>
          <p>{evaluation.issueContextLoaded ? 'Loaded' : 'Not loaded'}</p>
        </div>
      </div>
      <p className="trigger-evaluation-next-action">{evaluation.nextAction}</p>
    </section>
  );
}

function DecisionItem({
  label,
  decision
}: {
  label: string;
  decision: TriggerEvaluationResult['safetyDecision'];
}) {
  return (
    <div>
      <span>{label}</span>
      <p>{decision ? decision.reason : 'Not evaluated'}</p>
    </div>
  );
}
