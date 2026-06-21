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
  TaskSort,
  TaskStatusFilter
} from './types';

const TASK_PAGE_SIZE = 50;
const TASK_STATUS_FILTERS: TaskStatusFilter[] = [
  'ALL',
  'PENDING',
  'RUNNING',
  'RUNNING_TESTS',
  'COMPLETED',
  'FAILED',
  'CANCELLED'
];
const TASK_SORTS: TaskSort[] = ['createdAtDesc', 'createdAtAsc'];

export default function App() {
  const initialFilters = useMemo(() => filtersFromUrl(), []);
  const [tasks, setTasks] = useState<FixTask[]>([]);
  const [metrics, setMetrics] = useState<FixTaskMetricsSummary | null>(null);
  const [failureCauses, setFailureCauses] = useState<FixTaskFailureCauseSummary[]>([]);
  const [modelUsage, setModelUsage] = useState<FixTaskModelUsageSummary | null>(null);
  const [latency, setLatency] = useState<FixTaskLatencySummary | null>(null);
  const [configuration, setConfiguration] = useState<ConfigurationSummary | null>(null);
  const [backendHealth, setBackendHealth] = useState<BackendHealth | null>(null);
  const [queueSummary, setQueueSummary] = useState<FixTaskQueueSummary | null>(null);
  const [queueItems, setQueueItems] = useState<FixTaskQueueItem[]>([]);
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilter>(initialFilters.status);
  const [searchQuery, setSearchQuery] = useState(initialFilters.query);
  const [repositoryOwnerFilter, setRepositoryOwnerFilter] = useState(initialFilters.repositoryOwner);
  const [repositoryNameFilter, setRepositoryNameFilter] = useState(initialFilters.repositoryName);
  const [taskSort, setTaskSort] = useState<TaskSort>(initialFilters.sort);
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

  const handleStatusFilterChange = useCallback((status: TaskStatusFilter) => {
    setStatusFilter(status);
    writeTaskListStateToUrl({
      status,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      sort: taskSort
    });
  }, [repositoryNameFilter, repositoryOwnerFilter, searchQuery, taskSort]);

  const handleSearchQueryChange = useCallback((query: string) => {
    setSearchQuery(query);
    writeTaskListStateToUrl({
      status: statusFilter,
      query,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      sort: taskSort
    });
  }, [repositoryNameFilter, repositoryOwnerFilter, statusFilter, taskSort]);

  const handleRepositoryOwnerFilterChange = useCallback((repositoryOwner: string) => {
    setRepositoryOwnerFilter(repositoryOwner);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner,
      repositoryName: repositoryNameFilter,
      sort: taskSort
    });
  }, [repositoryNameFilter, searchQuery, statusFilter, taskSort]);

  const handleRepositoryNameFilterChange = useCallback((repositoryName: string) => {
    setRepositoryNameFilter(repositoryName);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName,
      sort: taskSort
    });
  }, [repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleTaskSortChange = useCallback((sort: TaskSort) => {
    setTaskSort(sort);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      sort
    });
  }, [repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter]);

  const handleClearFilters = useCallback(() => {
    setStatusFilter('ALL');
    setSearchQuery('');
    setRepositoryOwnerFilter('');
    setRepositoryNameFilter('');
    writeTaskListStateToUrl({
      status: 'ALL',
      query: '',
      repositoryOwner: '',
      repositoryName: '',
      sort: taskSort
    });
  }, [taskSort]);

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
        listTasks({
          status: statusFilter,
          query: searchQuery,
          repositoryOwner: repositoryOwnerFilter,
          repositoryName: repositoryNameFilter,
          sort: taskSort,
          limit: TASK_PAGE_SIZE
        }),
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
  }, [repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleLoadMoreTasks = useCallback(async () => {
    setLoadingMoreTasks(true);
    setError(null);
    try {
      const nextTaskPage = await listTasks({
        status: statusFilter,
        query: searchQuery,
        repositoryOwner: repositoryOwnerFilter,
        repositoryName: repositoryNameFilter,
        sort: taskSort,
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
  }, [repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort, tasks.length]);

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
          repositoryOwnerFilter={repositoryOwnerFilter}
          repositoryNameFilter={repositoryNameFilter}
          taskSort={taskSort}
          loading={loading}
          totalCount={taskTotal}
          canLoadMore={canLoadMoreTasks}
          loadingMore={loadingMoreTasks}
          canClearFilters={
            statusFilter !== 'ALL' ||
            searchQuery.trim().length > 0 ||
            repositoryOwnerFilter.trim().length > 0 ||
            repositoryNameFilter.trim().length > 0
          }
          onStatusFilterChange={handleStatusFilterChange}
          onSearchQueryChange={handleSearchQueryChange}
          onRepositoryOwnerFilterChange={handleRepositoryOwnerFilterChange}
          onRepositoryNameFilterChange={handleRepositoryNameFilterChange}
          onTaskSortChange={handleTaskSortChange}
          onClearFilters={handleClearFilters}
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

function filtersFromUrl() {
  const searchParams = new URLSearchParams(window.location.search);
  return {
    status: statusFilterFromUrl(searchParams.get('status')),
    query: searchParams.get('query') ?? '',
    repositoryOwner: searchParams.get('repositoryOwner') ?? '',
    repositoryName: searchParams.get('repositoryName') ?? '',
    sort: sortFromUrl(searchParams.get('sort'))
  };
}

function statusFilterFromUrl(value: string | null): TaskStatusFilter {
  if (value && TASK_STATUS_FILTERS.includes(value as TaskStatusFilter)) {
    return value as TaskStatusFilter;
  }
  return 'ALL';
}

function sortFromUrl(value: string | null): TaskSort {
  if (value && TASK_SORTS.includes(value as TaskSort)) {
    return value as TaskSort;
  }
  return 'createdAtDesc';
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

interface TaskListUrlState {
  status: TaskStatusFilter;
  query: string;
  repositoryOwner: string;
  repositoryName: string;
  sort: TaskSort;
}

function writeTaskListStateToUrl({
  status,
  query,
  repositoryOwner,
  repositoryName,
  sort
}: TaskListUrlState) {
  const nextUrl = new URL(window.location.href);
  nextUrl.searchParams.delete('taskId');
  if (status === 'ALL') {
    nextUrl.searchParams.delete('status');
  } else {
    nextUrl.searchParams.set('status', status);
  }
  if (query.trim()) {
    nextUrl.searchParams.set('query', query.trim());
  } else {
    nextUrl.searchParams.delete('query');
  }
  if (repositoryOwner.trim()) {
    nextUrl.searchParams.set('repositoryOwner', repositoryOwner.trim());
  } else {
    nextUrl.searchParams.delete('repositoryOwner');
  }
  if (repositoryName.trim()) {
    nextUrl.searchParams.set('repositoryName', repositoryName.trim());
  } else {
    nextUrl.searchParams.delete('repositoryName');
  }
  if (sort === 'createdAtDesc') {
    nextUrl.searchParams.delete('sort');
  } else {
    nextUrl.searchParams.set('sort', sort);
  }
  window.history.replaceState(null, '', `${nextUrl.pathname}${nextUrl.search}${nextUrl.hash}`);
}
