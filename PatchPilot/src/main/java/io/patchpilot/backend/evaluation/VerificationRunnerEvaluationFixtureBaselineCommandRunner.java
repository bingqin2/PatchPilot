package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.VerificationRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VerificationRunnerEvaluationFixtureBaselineCommandRunner implements EvaluationFixtureBaselineCommandRunner {

    private final VerificationRunner verificationRunner;

    @Override
    public TestRunResult run(String caseId, Path repositoryRoot, List<String> command) {
        return verificationRunner.runVerification("evaluation-fixture-" + caseId, repositoryRoot, command);
    }
}
