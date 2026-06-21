import { Send } from 'lucide-react';
import { FormEvent, useState } from 'react';
import type { CreateTaskInput } from '../../types';

interface ManualTaskFormProps {
  creating: boolean;
  successMessage: string | null;
  onCreateTask: (input: CreateTaskInput) => Promise<void>;
}

export function ManualTaskForm({ creating, successMessage, onCreateTask }: ManualTaskFormProps) {
  const [repositoryOwner, setRepositoryOwner] = useState('');
  const [repositoryName, setRepositoryName] = useState('');
  const [issueNumber, setIssueNumber] = useState('');
  const [triggerUser, setTriggerUser] = useState('local-operator');
  const [triggerComment, setTriggerComment] = useState('');

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      await onCreateTask({
        repositoryOwner: repositoryOwner.trim(),
        repositoryName: repositoryName.trim(),
        issueNumber: Number(issueNumber),
        triggerUser: triggerUser.trim(),
        triggerComment: triggerComment.trim()
      });
      setTriggerComment('');
    } catch {
      // App owns the visible error; keep form values for correction and retry.
    }
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
        <button className="icon-button" type="submit" disabled={creating}>
          <Send size={16} />
          {creating ? 'Creating task' : 'Create task'}
        </button>
      </form>
    </section>
  );
}
