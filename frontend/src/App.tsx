import { AlertCircle, RefreshCw } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  cancelTask,
  createTask,
  getBackendHealth,
  getConfigurationSummary,
  getFailureCauseSummary,
  getLatencySummary,
  getMetricsSummary,
  getModelUsageSummary,
  getQueueSummary,
  getTaskDetail,
  getTaskReport,
  listQueueItems,
  listTasks,
  retryTask
} from './api';
import { ConfigurationPanel } from './dashboard/components/ConfigurationPanel';
import { FailureCausePanel } from './dashboard/components/FailureCausePanel';
import { LatencyPanel } from './dashboard/components/LatencyPanel';
import { MetricCard } from './dashboard/components/MetricCard';
import { ModelUsagePanel } from './dashboard/components/ModelUsagePanel';
import { ManualTaskForm } from './dashboard/components/ManualTaskForm';
import { QueuePanel } from './dashboard/components/QueuePanel';
import { TaskDetailPanel } from './dashboard/components/TaskDetailPanel';
import { TaskListPanel } from './dashboard/components/TaskListPanel';
import { compactDateTime, duration, percent } from './dashboard/format';
import { emptyDetail } from './dashboard/types';
import type { TaskDetailState } from './dashboard/types';
import type {
  ConfigurationSummary,
  BackendHealth,
  CreateTaskInput,
  FixTask,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
  FixTaskMetricsSummary,
  FixTaskModelUsageSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  TaskStatusFilter
} from './types';

const TASK_PAGE_SIZE = 50;

export default function App() {
  const [tasks, setTasks] = useState<FixTask[]>([]);
  const [metrics, setMetrics] = useState<FixTaskMetricsSummary | null>(null);
  const [failureCauses, setFailureCauses] = useState<FixTaskFailureCauseSummary[]>([]);
  const [modelUsage, setModelUsage] = useState<FixTaskModelUsageSummary | null>(null);
  const [latency, setLatency] = useState<FixTaskLatencySummary | null>(null);
  const [configuration, setConfiguration] = useState<ConfigurationSummary | null>(null);
  const [backendHealth, setBackendHealth] = useState<BackendHealth | null>(null);
  const [queueSummary, setQueueSummary] = useState<FixTaskQueueSummary | null>(null);
  const [queueItems, setQueueItems] = useState<FixTaskQueueItem[]>([]);
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilter>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(() => taskIdFromUrl());
  const [detail, setDetail] = useState<TaskDetailState>(emptyDetail);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [actionTaskId, setActionTaskId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [canLoadMoreTasks, setCanLoadMoreTasks] = useState(false);
  const [loadingMoreTasks, setLoadingMoreTasks] = useState(false);
  const [taskTotal, setTaskTotal] = useState(0);
  const [lastRefreshedAt, setLastRefreshedAt] = useState<string | null>(null);
  const [creatingTask, setCreatingTask] = useState(false);
  const [createTaskStatus, setCreateTaskStatus] = useState<string | null>(null);

  const selectedTask = useMemo(
    () => tasks.find((task) => task.id === selectedTaskId) ?? tasks[0] ?? null,
    [selectedTaskId, tasks]
  );

  const selectTask = useCallback((taskId: string) => {
    setSelectedTaskId(taskId);
    writeTaskIdToUrl(taskId);
  }, []);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [
        taskList,
        metricsSummary,
        failureCauseSummary,
        modelUsageSummary,
        latencySummary,
        configurationSummary,
        healthSummary,
        queueSummaryData,
        queueItemList
      ] = await Promise.all([
        listTasks({ status: statusFilter, query: searchQuery, limit: TASK_PAGE_SIZE }),
        getMetricsSummary(),
        getFailureCauseSummary(),
        getModelUsageSummary(),
        getLatencySummary(),
        getConfigurationSummary(),
        getBackendHealth(),
        getQueueSummary(),
        listQueueItems()
      ]);
      setTasks(taskList.items);
      setMetrics(metricsSummary);
      setFailureCauses(failureCauseSummary);
      setModelUsage(modelUsageSummary);
      setLatency(latencySummary);
      setConfiguration(configurationSummary);
      setBackendHealth(healthSummary);
      setQueueSummary(queueSummaryData);
      setQueueItems(queueItemList);
      setCanLoadMoreTasks(taskList.hasMore);
      setTaskTotal(taskList.total);
      setSelectedTaskId((current) => selectedTaskIdFromList(taskList.items, current));
      setLastRefreshedAt(new Date().toISOString());
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
    getTaskDetail(selectedTask.id)
      .then((taskDetail) => {
        if (!cancelled) {
          setDetail(taskDetail);
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

  const handleCopyReport = useCallback((taskId: string) => getTaskReport(taskId), []);

  const handleCreateTask = useCallback(async (input: CreateTaskInput) => {
    setCreatingTask(true);
    setCreateTaskStatus(null);
    setError(null);
    try {
      const task = await createTask(input);
      setSelectedTaskId(task.id);
      writeTaskIdToUrl(task.id);
      setCreateTaskStatus('Manual task queued');
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
      throw caught;
    } finally {
      setCreatingTask(false);
    }
  }, [refresh]);

  return (
    <main className="app-shell">
      <header className="top-bar">
        <div>
          <p className="eyebrow">Self-hosted agent control plane</p>
          <h1>PatchPilot Operations</h1>
          {lastRefreshedAt ? (
            <time className="last-refresh-time" dateTime={lastRefreshedAt}>
              Last refreshed {compactDateTime(lastRefreshedAt)}
            </time>
          ) : null}
        </div>
        <button
          className="icon-button"
          type="button"
          onClick={() => void refresh()}
          aria-label={loading ? 'Refreshing dashboard' : 'Refresh dashboard'}
          disabled={loading}
        >
          <RefreshCw size={17} />
          {loading ? 'Refreshing' : 'Refresh'}
        </button>
      </header>

      {loading ? (
        <section className="refresh-status" role="status">
          Dashboard refreshing
        </section>
      ) : null}

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

      <section className="summary-panel-grid" aria-label="Operational summaries">
        <FailureCausePanel causes={failureCauses} />
        <ModelUsagePanel usage={modelUsage} />
        <LatencyPanel latency={latency} />
      </section>

      <ManualTaskForm
        creating={creatingTask}
        successMessage={createTaskStatus}
        onCreateTask={handleCreateTask}
      />

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
          onSelectTask={selectTask}
          onLoadMoreTasks={handleLoadMoreTasks}
        />

        <TaskDetailPanel
          task={selectedTask}
          detail={detail}
          loading={detailLoading}
          actionInFlight={actionTaskId === selectedTask?.id}
          onCancelTask={handleCancelTask}
          onRetryTask={handleRetryTask}
          onCopyReport={handleCopyReport}
        />
      </section>

      <ConfigurationPanel configuration={configuration} backendHealth={backendHealth} />

      <QueuePanel summary={queueSummary} items={queueItems} />
    </main>
  );
}

function errorMessage(caught: unknown) {
  return caught instanceof Error ? caught.message : 'Dashboard request failed';
}

function taskIdFromUrl() {
  const pathMatch = window.location.pathname.match(/^\/tasks\/([^/]+)$/);
  if (pathMatch) {
    return decodeURIComponent(pathMatch[1]);
  }
  return new URLSearchParams(window.location.search).get('taskId');
}

function selectedTaskIdFromList(tasks: FixTask[], currentTaskId: string | null) {
  if (currentTaskId && tasks.some((task) => task.id === currentTaskId)) {
    return currentTaskId;
  }
  return tasks[0]?.id ?? null;
}

function writeTaskIdToUrl(taskId: string) {
  const nextUrl = new URL(window.location.href);
  nextUrl.pathname = `/tasks/${encodeURIComponent(taskId)}`;
  nextUrl.searchParams.delete('taskId');
  window.history.replaceState(null, '', `${nextUrl.pathname}${nextUrl.search}${nextUrl.hash}`);
}
