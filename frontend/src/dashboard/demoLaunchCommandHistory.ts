import type {
  DemoLaunchCommand,
  DemoLaunchCommandInput,
  DemoLaunchCommandOperation,
  DemoLaunchPreflightInput,
  DemoPreparedLaunchCommand
} from '../types';

export interface DemoLaunchCommandHistoryItem {
  id: string;
  savedAt: string;
  input: DemoLaunchCommandInput;
  result: DemoLaunchCommand;
}

export const DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY = 'patchpilot.demoLaunchCommandHistory';
export const DEMO_LAUNCH_COMMAND_HISTORY_LIMIT = 5;

export function createDemoLaunchCommandHistoryItem(
  commandInput: DemoLaunchCommandInput,
  commandResult: DemoLaunchCommand
): DemoLaunchCommandHistoryItem {
  return {
    id: `${Date.now()}-${commandResult.triggerComment}`,
    savedAt: new Date().toISOString(),
    input: commandInput,
    result: commandResult
  };
}

export function addDemoLaunchCommandHistoryItem(
  currentHistory: DemoLaunchCommandHistoryItem[],
  item: DemoLaunchCommandHistoryItem
) {
  return [
    item,
    ...currentHistory.filter((existing) => existing.result.triggerComment !== item.result.triggerComment)
  ].slice(0, DEMO_LAUNCH_COMMAND_HISTORY_LIMIT);
}

export function loadDemoLaunchCommandHistory(): DemoLaunchCommandHistoryItem[] {
  if (typeof globalThis.localStorage === 'undefined') {
    return [];
  }

  try {
    const rawHistory = globalThis.localStorage.getItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY);
    if (!rawHistory) {
      return [];
    }

    const parsedHistory: unknown = JSON.parse(rawHistory);
    if (!Array.isArray(parsedHistory)) {
      return [];
    }

    return parsedHistory.filter(isDemoLaunchCommandHistoryItem).slice(0, DEMO_LAUNCH_COMMAND_HISTORY_LIMIT);
  } catch {
    return [];
  }
}

export function persistDemoLaunchCommandHistory(history: DemoLaunchCommandHistoryItem[]) {
  if (typeof globalThis.localStorage === 'undefined') {
    return;
  }

  try {
    if (history.length === 0) {
      globalThis.localStorage.removeItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY);
      return;
    }
    globalThis.localStorage.setItem(DEMO_LAUNCH_COMMAND_HISTORY_STORAGE_KEY, JSON.stringify(history));
  } catch {
    // Browsers may reject localStorage writes in private contexts; history is optional.
  }
}

export function toPreparedLaunchCommands(history: DemoLaunchCommandHistoryItem[]): DemoPreparedLaunchCommand[] {
  return history.map((item) => ({
    triggerComment: item.result.triggerComment,
    repositoryOwner: item.input.repositoryOwner,
    repositoryName: item.input.repositoryName,
    issueNumber: item.input.issueNumber,
    triggerUser: item.input.triggerUser,
    operation: item.input.operation,
    targetPath: item.input.targetPath,
    replacementText: item.input.replacementText ?? null,
    savedAt: item.savedAt
  }));
}

function isDemoLaunchCommandHistoryItem(value: unknown): value is DemoLaunchCommandHistoryItem {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'string' &&
    typeof value.savedAt === 'string' &&
    isDemoLaunchCommandInput(value.input) &&
    isDemoLaunchCommand(value.result)
  );
}

function isDemoLaunchCommandInput(value: unknown): value is DemoLaunchCommandInput {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.repositoryOwner === 'string' &&
    typeof value.repositoryName === 'string' &&
    typeof value.issueNumber === 'number' &&
    typeof value.triggerUser === 'string' &&
    isDemoLaunchCommandOperation(value.operation) &&
    typeof value.targetPath === 'string' &&
    (value.replacementText === null || typeof value.replacementText === 'string')
  );
}

function isDemoLaunchCommand(value: unknown): value is DemoLaunchCommand {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.triggerComment === 'string' &&
    typeof value.githubIssueUrl === 'string' &&
    typeof value.summary === 'string' &&
    isDemoLaunchPreflightInput(value.preflightInput) &&
    Array.isArray(value.nextActions) &&
    value.nextActions.every((action) => typeof action === 'string')
  );
}

function isDemoLaunchPreflightInput(value: unknown): value is DemoLaunchPreflightInput {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.repositoryOwner === 'string' &&
    typeof value.repositoryName === 'string' &&
    typeof value.issueNumber === 'number' &&
    typeof value.triggerUser === 'string' &&
    typeof value.triggerComment === 'string'
  );
}

function isDemoLaunchCommandOperation(value: unknown): value is DemoLaunchCommandOperation {
  return value === 'replace' || value === 'touch';
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}
