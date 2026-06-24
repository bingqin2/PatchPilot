import type { FixTaskFailureCauseSummary } from '../../types';

interface FailureCausePanelProps {
  causes: FixTaskFailureCauseSummary[];
}

const causeLabels: Record<string, string> = {
  VERIFICATION_FAILED: 'Verification failed',
  GITHUB_OPERATION_FAILED: 'GitHub operation failed',
  UNSUPPORTED_REPOSITORY: 'Unsupported repository',
  MODEL_FAILED: 'Model failed',
  WORKSPACE_FAILED: 'Workspace failed',
  PATCH_REVIEW_REJECTED: 'Patch review rejected',
  TASK_FAILED: 'Task failed'
};

export function FailureCausePanel({ causes }: FailureCausePanelProps) {
  return (
    <section className="panel failure-cause-panel" aria-label="Failure causes">
      <div className="panel-header">
        <div>
          <h2>Failure causes</h2>
          <p>{causes.length === 0 ? 'No failed tasks' : `${causes.length} grouped causes`}</p>
        </div>
      </div>
      <div className="failure-cause-list">
        {causes.length === 0 ? (
          <p className="empty-state">No failed tasks recorded.</p>
        ) : causes.map((cause) => (
          <div className="failure-cause-row" key={cause.cause}>
            <div>
              <span>{causeLabels[cause.cause] ?? cause.cause}</span>
              <p>{cause.nextAction}</p>
            </div>
            <strong>{cause.count}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}
