import type { FixTaskFailureCauseSummary } from '../../types';

interface FailureCausePanelProps {
  causes: FixTaskFailureCauseSummary[];
}

const causeLabels: Record<string, string> = {
  MAVEN_TESTS: 'Maven tests',
  GITHUB_AUTH: 'GitHub auth',
  MODEL_ERROR: 'Model error',
  SANDBOX_REJECTION: 'Sandbox rejection',
  UNKNOWN: 'Unknown'
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
            <span>{causeLabels[cause.cause] ?? cause.cause}</span>
            <strong>{cause.count}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}
