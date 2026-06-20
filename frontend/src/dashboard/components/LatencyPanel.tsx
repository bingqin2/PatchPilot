import type { FixTaskLatencySummary } from '../../types';
import { duration, number } from '../format';

interface LatencyPanelProps {
  latency: FixTaskLatencySummary | null;
}

export function LatencyPanel({ latency }: LatencyPanelProps) {
  return (
    <section className="panel latency-panel" aria-label="Latency">
      <div className="panel-header">
        <div>
          <h2>Latency</h2>
          <p>{number(latency?.completedTaskCount)} completed tasks</p>
        </div>
      </div>
      <div className="latency-grid">
        <div>
          <span>Task</span>
          <strong>{duration(latency?.averageTaskDurationMs)} avg task</strong>
          <p>{duration(latency?.maxTaskDurationMs)} max</p>
        </div>
        <div>
          <span>Model</span>
          <strong>{duration(latency?.averageModelCallDurationMs)} model avg</strong>
          <p>{number(latency?.modelCallCount)} calls</p>
        </div>
        <div>
          <span>Tools</span>
          <strong>{duration(latency?.averageToolCallDurationMs)} tool avg</strong>
          <p>{number(latency?.toolCallCount)} calls</p>
        </div>
        <div>
          <span>Tests</span>
          <strong>{duration(latency?.averageTestRunDurationMs)} test avg</strong>
          <p>{number(latency?.testRunCount)} runs</p>
        </div>
      </div>
    </section>
  );
}
