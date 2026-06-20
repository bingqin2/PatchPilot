import { AlertCircle, RefreshCw } from 'lucide-react';
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
import { MetricCard } from './dashboard/components/MetricCard';
import { QueuePanel } from './dashboard/components/QueuePanel';
import { TaskDetailPanel } from './dashboard/components/TaskDetailPanel';
import { TaskListPanel } from './dashboard/components/TaskListPanel';
import { duration, percent } from './dashboard/format';
import { emptyDetail } from './dashboard/types';
import type { TaskDetailState } from './dashboard/types';
import type {
  FixTask,
  FixTaskMetricsSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  TaskStatusFilter
} from './types';

const TASK_PAGE_SIZE = 50;

export default function App() {
  const [tasks, setTasks] = useState<FixTask[]>([]);
  const [metrics, setMetrics] = useState<FixTaskMetricsSummary | null>(null);
  const [queueSummary, setQueueSummary] = useState<FixTaskQueueSummary | null>(null);
  const [queueItems, setQueueItems] = useState<FixTaskQueueItem[]>([]);
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilter>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [detail, setDetail] = useState<TaskDetailState>(emptyDetail);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [actionTaskId, setActionTaskId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [canLoadMoreTasks, setCanLoadMoreTasks] = useState(false);
  const [loadingMoreTasks, setLoadingMoreTasks] = useState(false);
  const [taskTotal, setTaskTotal] = useState(0);

  const selectedTask = useMemo(
    () => tasks.find((task) => task.id === selectedTaskId) ?? tasks[0] ?? null,
    [selectedTaskId, tasks]
  );

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [taskList, metricsSummary, queueSummaryData, queueItemList] = await Promise.all([
        listTasks({ status: statusFilter, query: searchQuery, limit: TASK_PAGE_SIZE }),
        getMetricsSummary(),
        getQueueSummary(),
        listQueueItems()
      ]);
      setTasks(taskList.items);
      setMetrics(metricsSummary);
      setQueueSummary(queueSummaryData);
      setQueueItems(queueItemList);
      setCanLoadMoreTasks(taskList.hasMore);
      setTaskTotal(taskList.total);
      setSelectedTaskId((current) => {
        if (current && taskList.items.some((task) => task.id === current)) {
          return current;
        }
        return taskList.items[0]?.id ?? null;
      });
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setLoading(false);
    }
  }, [searchQuery, statusFilter]);

  const handleLoadMoreTasks = useCallback(async () => {
    setLoadingMoreTasks(true);
    setError(null);
    try {
      const nextTaskPage = await listTasks({
        status: statusFilter,
        query: searchQuery,
        limit: TASK_PAGE_SIZE,
        offset: tasks.length
      });
      setTasks((current) => [...current, ...nextTaskPage.items]);
      setCanLoadMoreTasks(nextTaskPage.hasMore);
      setTaskTotal(nextTaskPage.total);
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setLoadingMoreTasks(false);
    }
  }, [searchQuery, statusFilter, tasks.length]);

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
        <TaskListPanel
          tasks={tasks}
          selectedTask={selectedTask}
          statusFilter={statusFilter}
          searchQuery={searchQuery}
          loading={loading}
          totalCount={taskTotal}
          canLoadMore={canLoadMoreTasks}
          loadingMore={loadingMoreTasks}
          onStatusFilterChange={setStatusFilter}
          onSearchQueryChange={setSearchQuery}
          onSelectTask={setSelectedTaskId}
          onLoadMoreTasks={handleLoadMoreTasks}
        />

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

function errorMessage(caught: unknown) {
  return caught instanceof Error ? caught.message : 'Dashboard request failed';
}
