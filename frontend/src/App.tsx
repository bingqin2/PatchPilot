import { AlertCircle, CheckCircle2, CircleDot, ExternalLink, GitPullRequest, RefreshCw, RotateCcw, Terminal, XCircle } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  cancelTask,
  getMetricsSummary,
  getModelCalls,
  getQueueSummary,
  getTaskSummary,
  getTestRuns,
  getTimeline,
  getToolCalls,
  listQueueItems,
  listTasks,
  retryTask
} from './api';
import type {
  FixTask,
  FixTaskAuditSummary,
  FixTaskMetricsSummary,
  FixTaskModelCall,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  TaskStatus,
  TaskStatusFilter
} from './types';

interface TaskDetailState {
  summary: FixTaskAuditSummary | null;
  timeline: FixTaskTimelineEvent[];
  testRuns: FixTaskTestRun[];
  toolCalls: FixTaskToolCall[];
  modelCalls: FixTaskModelCall[];
}

const emptyDetail: TaskDetailState = {
  summary: null,
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: []
};

const statusFilters: TaskStatusFilter[] = [
  'ALL',
  'PENDING',
  'RUNNING',
  'RUNNING_TESTS',
  'COMPLETED',
  'FAILED',
  'CANCELLED'
];

export default function App() {
  const [tasks, setTasks] = useState<FixTask[]>([]);
  const [metrics, setMetrics] = useState<FixTaskMetricsSummary | null>(null);
  const [queueSummary, setQueueSummary] = useState<FixTaskQueueSummary | null>(null);
  const [queueItems, setQueueItems] = useState<FixTaskQueueItem[]>([]);
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilter>('ALL');
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [detail, setDetail] = useState<TaskDetailState>(emptyDetail);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [actionTaskId, setActionTaskId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const selectedTask = useMemo(
    () => tasks.find((task) => task.id === selectedTaskId) ?? tasks[0] ?? null,
    [selectedTaskId, tasks]
  );

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [taskList, metricsSummary, queueSummaryData, queueItemList] = await Promise.all([
        listTasks(statusFilter),
        getMetricsSummary(),
        getQueueSummary(),
        listQueueItems()
      ]);
      setTasks(taskList);
      setMetrics(metricsSummary);
      setQueueSummary(queueSummaryData);
      setQueueItems(queueItemList);
      setSelectedTaskId((current) => {
        if (current && taskList.some((task) => task.id === current)) {
          return current;
        }
        return taskList[0]?.id ?? null;
      });
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  useEffect(() => {
    if (!selectedTask) {
      setDetail(emptyDetail);
      return;
    }

    let cancelled = false;
    setDetailLoading(true);
    setError(null);
    Promise.all([
      getTaskSummary(selectedTask.id),
      getTimeline(selectedTask.id),
      getTestRuns(selectedTask.id),
      getToolCalls(selectedTask.id),
      getModelCalls(selectedTask.id)
    ])
      .then(([summary, timeline, testRuns, toolCalls, modelCalls]) => {
        if (!cancelled) {
          setDetail({ summary, timeline, testRuns, toolCalls, modelCalls });
        }
      })
      .catch((caught) => {
        if (!cancelled) {
          setError(errorMessage(caught));
          setDetail(emptyDetail);
        }
      })
      .finally(() => {
        if (!cancelled) {
          setDetailLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [selectedTask]);

  const handleCancelTask = useCallback(async (taskId: string) => {
    setActionTaskId(taskId);
    setError(null);
    try {
      await cancelTask(taskId);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setActionTaskId(null);
    }
  }, [refresh]);

  const handleRetryTask = useCallback(async (taskId: string) => {
    setActionTaskId(taskId);
    setError(null);
    try {
      await retryTask(taskId);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setActionTaskId(null);
    }
  }, [refresh]);

  return (
    <main className="app-shell">
      <header className="top-bar">
        <div>
          <p className="eyebrow">Self-hosted agent control plane</p>
          <h1>PatchPilot Operations</h1>
        </div>
        <button className="icon-button" type="button" onClick={() => void refresh()} aria-label="Refresh dashboard">
          <RefreshCw size={17} />
          Refresh
        </button>
      </header>

      {error ? (
        <section className="alert" role="alert">
          <AlertCircle size={18} />
          <span>{error}</span>
        </section>
      ) : null}

      <section className="metrics-grid" aria-label="Task metrics">
        <MetricCard label="Tasks" value={metrics?.totalCount ?? 0} detail={`${metrics?.completedCount ?? 0} completed`} />
        <MetricCard label="Failed" value={metrics?.failedCount ?? 0} detail={`${percent(metrics?.failureRate)} failure rate`} />
        <MetricCard label="Completion" value={percent(metrics?.completionRate)} detail={`${duration(metrics?.averageCompletionDurationMs)} avg`} />
        <MetricCard label="Test pass" value={percent(metrics?.testPassRate)} detail={`${metrics?.passedTestRunCount ?? 0}/${metrics?.testRunCount ?? 0} runs`} />
        <MetricCard label="Model tokens" value={metrics?.totalModelTokens ?? 0} detail={`${metrics?.averageModelTokensPerCompletedTask ?? 0} avg completed`} />
      </section>

      <section className="workspace-grid">
        <section className="panel task-list-panel">
          <div className="panel-header">
            <div>
              <h2>Tasks</h2>
              <p>{loading ? 'Loading latest tasks' : `${tasks.length} visible tasks`}</p>
            </div>
          </div>
          <div className="filter-bar" aria-label="Task status filters">
            {statusFilters.map((status) => (
              <button
                className={status === statusFilter ? 'filter-button filter-button-active' : 'filter-button'}
                key={status}
                type="button"
                onClick={() => setStatusFilter(status)}
                aria-pressed={status === statusFilter}
              >
                {status}
              </button>
            ))}
          </div>
          <div className="task-list">
            {tasks.map((task) => (
              <button
                className={task.id === selectedTask?.id ? 'task-row task-row-active' : 'task-row'}
                key={task.id}
                type="button"
                onClick={() => setSelectedTaskId(task.id)}
              >
                <span className={`status-pill status-${task.status.toLowerCase().replace('_', '-')}`}>
                  {statusIcon(task.status)}
                  {task.status}
                </span>
                <span className="task-repo">{task.repositoryOwner}/{task.repositoryName}</span>
                <span className="task-issue">#{task.issueNumber}</span>
                <span className="task-comment">{task.triggerComment}</span>
                {task.failureReason ? <span className="task-failure">{task.failureReason}</span> : null}
                {task.pullRequestUrl ? (
                  <a className="task-link" href={task.pullRequestUrl} target="_blank" rel="noreferrer">
                    <GitPullRequest size={14} />
                    PR #{pullRequestNumber(task.pullRequestUrl)}
                  </a>
                ) : null}
              </button>
            ))}
            {!loading && tasks.length === 0 ? <p className="empty-state">No {statusFilter} tasks found.</p> : null}
          </div>
        </section>

        <TaskDetailPanel
          task={selectedTask}
          detail={detail}
          loading={detailLoading}
          actionInFlight={actionTaskId === selectedTask?.id}
          onCancelTask={handleCancelTask}
          onRetryTask={handleRetryTask}
        />
      </section>

      <QueuePanel summary={queueSummary} items={queueItems} />
    </main>
  );
}

function QueuePanel({ summary, items }: { summary: FixTaskQueueSummary | null; items: FixTaskQueueItem[] }) {
  return (
    <section className="panel queue-panel">
      <div className="panel-header">
        <div>
          <h2>Queue</h2>
          <p>{summary ? `${summary.totalCount} queue items` : 'Loading queue state'}</p>
        </div>
      </div>
      <div className="queue-summary">
        <SummaryItem label="Pending" value={`${summary?.pendingCount ?? 0} pending`} />
        <SummaryItem label="Available" value={`${summary?.availablePendingCount ?? 0} available`} />
        <SummaryItem label="Delayed" value={`${summary?.delayedPendingCount ?? 0} delayed`} />
        <SummaryItem label="Running" value={summary?.runningCount ?? 0} />
        <SummaryItem label="Failed" value={summary?.failedCount ?? 0} />
        <SummaryItem label="Cancelled" value={summary?.cancelledCount ?? 0} />
      </div>
      <div className="queue-list">
        {items.map((item) => (
          <div className="queue-row" key={item.id}>
            <span className={`status-pill status-${item.status.toLowerCase()}`}>{item.status}</span>
            <strong>{item.id}</strong>
            <span>{item.taskId}</span>
            <span>attempt {item.attemptCount}</span>
            <time>{compactTime(item.availableAt)}</time>
            {item.lastError ? <p>{item.lastError}</p> : null}
          </div>
        ))}
        {items.length === 0 ? <p className="empty-state">No queue items.</p> : null}
      </div>
    </section>
  );
}

function TaskDetailPanel({
  task,
  detail,
  loading,
  actionInFlight,
  onCancelTask,
  onRetryTask
}: {
  task: FixTask | null;
  detail: TaskDetailState;
  loading: boolean;
  actionInFlight: boolean;
  onCancelTask: (taskId: string) => Promise<void>;
  onRetryTask: (taskId: string) => Promise<void>;
}) {
  if (!task) {
    return (
      <section className="panel detail-panel">
        <h2>Task Detail</h2>
        <p className="empty-state">Select a task to inspect execution records.</p>
      </section>
    );
  }

  const canCancel = task.status === 'PENDING' || task.status === 'RUNNING' || task.status === 'RUNNING_TESTS';
  const canRetry = task.status === 'FAILED' || task.status === 'CANCELLED';

  return (
    <section className="panel detail-panel">
      <div className="panel-header">
        <div>
          <h2>{task.repositoryOwner}/{task.repositoryName} #{task.issueNumber}</h2>
          <p>{task.id}</p>
        </div>
        <div className="detail-actions">
          {canCancel ? (
            <button
              className="secondary-button danger-button"
              type="button"
              disabled={actionInFlight}
              onClick={() => void onCancelTask(task.id)}
            >
              <XCircle size={14} />
              Cancel task
            </button>
          ) : null}
          {canRetry ? (
            <button
              className="secondary-button"
              type="button"
              disabled={actionInFlight}
              onClick={() => void onRetryTask(task.id)}
            >
              <RotateCcw size={14} />
              Retry task
            </button>
          ) : null}
          {task.pullRequestUrl ? (
            <a className="external-link" href={task.pullRequestUrl} target="_blank" rel="noreferrer">
              Open PR
              <ExternalLink size={14} />
            </a>
          ) : null}
        </div>
      </div>

      <div className="detail-summary">
        <SummaryItem label="Timeline" value={detail.summary?.timelineEventCount ?? 0} />
        <SummaryItem label="Tests" value={detail.summary?.testRunCount ?? 0} />
        <SummaryItem label="Tools" value={detail.summary?.toolCallCount ?? 0} />
        <SummaryItem label="Models" value={detail.summary?.modelCallCount ?? 0} />
        <SummaryItem label="Tokens" value={detail.summary?.totalModelTokens ?? 0} />
      </div>

      {detail.summary?.latestTimelineEvent ? (
        <div className="latest-event">
          <span>Latest event</span>
          <strong>{detail.summary.latestTimelineEvent.eventType}</strong>
          <p>{detail.summary.latestTimelineEvent.message}</p>
        </div>
      ) : null}

      {loading ? <p className="empty-state">Loading task records...</p> : null}

      <section className="detail-section">
        <h3>Timeline</h3>
        <div className="timeline">
          {detail.timeline.map((event) => (
            <div className="timeline-item" key={event.id}>
              <span className="timeline-dot" />
              <div>
                <strong>{event.eventType}</strong>
                <p>{event.message}</p>
              </div>
              <time>{compactTime(event.createdAt)}</time>
            </div>
          ))}
        </div>
      </section>

      <section className="detail-section">
        <h3>Maven Test Output</h3>
        {detail.testRuns.map((run) => (
          <div className="test-run" key={run.id}>
            <div className="test-run-header">
              <span className={run.exitCode === 0 ? 'result-pass' : 'result-fail'}>exit {run.exitCode}</span>
              <span>{run.command}</span>
              <span>{duration(run.durationMs)}</span>
            </div>
            <pre>{run.output}</pre>
          </div>
        ))}
      </section>

      <section className="detail-section split-section">
        <div>
          <h3>Tool Calls</h3>
          {detail.toolCalls.map((call) => (
            <RecordLine key={call.id} title={call.toolName} meta={call.success ? 'success' : 'failed'} body={call.outputSummary} />
          ))}
        </div>
        <div>
          <h3>Model Calls</h3>
          {detail.modelCalls.map((call) => (
            <RecordLine key={call.id} title={call.model} meta={`${call.totalTokens} tokens`} body={call.responseSummary} />
          ))}
        </div>
      </section>
    </section>
  );
}

function MetricCard({ label, value, detail }: { label: string; value: string | number; detail: string }) {
  return (
    <div className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
      <p>{detail}</p>
    </div>
  );
}

function SummaryItem({ label, value }: { label: string; value: string | number }) {
  return (
    <div>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function RecordLine({ title, meta, body }: { title: string; meta: string; body: string }) {
  return (
    <div className="record-line">
      <div>
        <strong>{title}</strong>
        <span>{meta}</span>
      </div>
      <p>{body}</p>
    </div>
  );
}

function statusIcon(status: TaskStatus) {
  if (status === 'COMPLETED') {
    return <CheckCircle2 size={14} />;
  }
  if (status === 'FAILED' || status === 'CANCELLED') {
    return <AlertCircle size={14} />;
  }
  if (status === 'RUNNING_TESTS') {
    return <Terminal size={14} />;
  }
  return <CircleDot size={14} />;
}

function percent(value?: number) {
  return `${Math.round((value ?? 0) * 100)}%`;
}

function duration(value?: number | null) {
  if (!value) {
    return '0 ms';
  }
  if (value < 1000) {
    return `${value} ms`;
  }
  return `${(value / 1000).toFixed(1)}s`;
}

function compactTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(new Date(value));
}

function pullRequestNumber(url: string) {
  return url.split('/').filter(Boolean).at(-1) ?? 'link';
}

function errorMessage(caught: unknown) {
  return caught instanceof Error ? caught.message : 'Dashboard request failed';
}
