package io.patchpilot.backend.runner.domain.vo;

public record TestRunResult(String command, int exitCode, String output) {
}
