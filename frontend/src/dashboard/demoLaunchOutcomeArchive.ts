import type { TaskStatus } from '../types';

export interface DemoLaunchOutcomeArchiveItem {
  id: string;
  archivedAt: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
  taskId: string | null;
  taskStatus: TaskStatus | 'PENDING';
  pullRequestUrl: string | null;
  report: string;
}

export const DEMO_LAUNCH_OUTCOME_ARCHIVE_STORAGE_KEY = 'patchpilot.demoLaunchOutcomeArchive';
export const DEMO_LAUNCH_OUTCOME_ARCHIVE_LIMIT = 5;

export function addDemoLaunchOutcomeArchiveItem(
  currentArchive: DemoLaunchOutcomeArchiveItem[],
  item: DemoLaunchOutcomeArchiveItem
) {
  return [
    item,
    ...currentArchive.filter((existing) => archiveKey(existing) !== archiveKey(item))
  ].slice(0, DEMO_LAUNCH_OUTCOME_ARCHIVE_LIMIT);
}

export function loadDemoLaunchOutcomeArchive(): DemoLaunchOutcomeArchiveItem[] {
  if (typeof globalThis.localStorage === 'undefined') {
    return [];
  }

  try {
    const rawArchive = globalThis.localStorage.getItem(DEMO_LAUNCH_OUTCOME_ARCHIVE_STORAGE_KEY);
    if (!rawArchive) {
      return [];
    }

    const parsedArchive: unknown = JSON.parse(rawArchive);
    if (!Array.isArray(parsedArchive)) {
      return [];
    }

    return parsedArchive.filter(isDemoLaunchOutcomeArchiveItem).slice(0, DEMO_LAUNCH_OUTCOME_ARCHIVE_LIMIT);
  } catch {
    return [];
  }
}

export function persistDemoLaunchOutcomeArchive(archive: DemoLaunchOutcomeArchiveItem[]) {
  if (typeof globalThis.localStorage === 'undefined') {
    return;
  }

  try {
    if (archive.length === 0) {
      globalThis.localStorage.removeItem(DEMO_LAUNCH_OUTCOME_ARCHIVE_STORAGE_KEY);
      return;
    }
    globalThis.localStorage.setItem(DEMO_LAUNCH_OUTCOME_ARCHIVE_STORAGE_KEY, JSON.stringify(archive));
  } catch {
    // Browsers may reject localStorage writes in private contexts; archive is optional.
  }
}

function archiveKey(item: DemoLaunchOutcomeArchiveItem) {
  return item.taskId ?? `${item.repositoryOwner}/${item.repositoryName}#${item.issueNumber}:${item.triggerComment}`;
}

function isDemoLaunchOutcomeArchiveItem(value: unknown): value is DemoLaunchOutcomeArchiveItem {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'string' &&
    typeof value.archivedAt === 'string' &&
    typeof value.repositoryOwner === 'string' &&
    typeof value.repositoryName === 'string' &&
    typeof value.issueNumber === 'number' &&
    typeof value.triggerUser === 'string' &&
    typeof value.triggerComment === 'string' &&
    (value.taskId === null || typeof value.taskId === 'string') &&
    typeof value.taskStatus === 'string' &&
    (value.pullRequestUrl === null || typeof value.pullRequestUrl === 'string') &&
    typeof value.report === 'string'
  );
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}
