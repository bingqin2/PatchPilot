package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;

import java.nio.file.Path;
import java.util.List;

public interface EvaluationFixtureBaselineCommandRunner {

    TestRunResult run(String caseId, Path repositoryRoot, List<String> command);
}
