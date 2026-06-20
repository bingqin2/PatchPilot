import type { FixTaskModelUsageSummary } from '../../types';
import { number, usd } from '../format';

interface ModelUsagePanelProps {
  usage: FixTaskModelUsageSummary | null;
}

export function ModelUsagePanel({ usage }: ModelUsagePanelProps) {
  const totalCalls = (usage?.successfulCalls ?? 0) + (usage?.failedCalls ?? 0);

  return (
    <section className="panel model-usage-panel" aria-label="Model usage">
      <div className="panel-header">
        <div>
          <h2>Model usage</h2>
          <p>{number(totalCalls)} model calls</p>
        </div>
      </div>
      <div className="model-usage-grid">
        <div>
          <span>Total tokens</span>
          <strong>{number(usage?.totalTokens)}</strong>
        </div>
        <div>
          <span>Prompt</span>
          <strong>{number(usage?.totalPromptTokens)}</strong>
        </div>
        <div>
          <span>Completion tokens</span>
          <strong>{number(usage?.totalCompletionTokens)}</strong>
        </div>
        <div>
          <span>Calls</span>
          <strong>{number(totalCalls)}</strong>
          <p>{number(usage?.successfulCalls)} successful</p>
          <p>{number(usage?.failedCalls)} failed</p>
        </div>
        <div>
          <span>Estimated cost</span>
          <strong>{usd(usage?.estimatedCostUsd)}</strong>
        </div>
      </div>
    </section>
  );
}
