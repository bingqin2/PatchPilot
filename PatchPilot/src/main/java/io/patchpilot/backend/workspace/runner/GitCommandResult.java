package io.patchpilot.backend.workspace.runner;

public record GitCommandResult(int exitCode, String output) {
}
