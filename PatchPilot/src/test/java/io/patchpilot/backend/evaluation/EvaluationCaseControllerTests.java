package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunSnapshotArchiveRepository;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EvaluationCaseControllerTests {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(controller("snapshot-default"))
            .build();

    @Test
    void should_return_evaluation_case_catalog() throws Exception {
        mockMvc.perform(get("/api/evaluation/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(6)))
                .andExpect(jsonPath("$.data[0].id").value("java-maven-doc-fix"))
                .andExpect(jsonPath("$.data[0].category").value("SUPPORTED_FIX"))
                .andExpect(jsonPath("$.data[0].language").value("java"))
                .andExpect(jsonPath("$.data[0].buildSystem").value("maven"))
                .andExpect(jsonPath("$.data[0].repositoryFixturePath").value("docs/demo-repositories/java-maven"))
                .andExpect(jsonPath("$.data[0].expectedVerificationCommand[0]").value("mvn"))
                .andExpect(jsonPath("$.data[0].expectedVerificationCommand[1]").value("test"))
                .andExpect(jsonPath("$.data[0].expectedChangedFiles[0]").value("src/main/java/demo/Calculator.java"))
                .andExpect(jsonPath("$.data[0].expectedDecision").value("ACCEPT_AND_CREATE_PR"))
                .andExpect(jsonPath("$.data[0].expectedRejectionCategory").doesNotExist())
                .andExpect(jsonPath("$.data[4].id").value("unsafe-secret-exfiltration-rejection"))
                .andExpect(jsonPath("$.data[4].category").value("SAFETY_REJECTION"))
                .andExpect(jsonPath("$.data[4].expectedDecision").value("REJECT_BEFORE_TASK"))
                .andExpect(jsonPath("$.data[4].expectedRejectionCategory").value("DANGEROUS_INSTRUCTION"))
                .andExpect(jsonPath("$.data[4].expectedVerificationCommand", hasSize(0)))
                .andExpect(jsonPath("$.data[4].expectedChangedFiles", hasSize(0)));
    }

    @Test
    void should_return_evaluation_case_readiness_summary() throws Exception {
        mockMvc.perform(get("/api/evaluation/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.totalCaseCount").value(6))
                .andExpect(jsonPath("$.data.supportedFixCaseCount").value(4))
                .andExpect(jsonPath("$.data.safetyRejectionCaseCount").value(2))
                .andExpect(jsonPath("$.data.coveredLanguages[0]").value("go"))
                .andExpect(jsonPath("$.data.coveredLanguages[1]").value("java"))
                .andExpect(jsonPath("$.data.coveredLanguages[2]").value("node"))
                .andExpect(jsonPath("$.data.coveredLanguages[3]").value("python"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[0]").value("go"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[1]").value("maven"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[2]").value("npm"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[3]").value("pytest"))
                .andExpect(jsonPath("$.data.rejectionCategories[0]").value("DANGEROUS_INSTRUCTION"))
                .andExpect(jsonPath("$.data.rejectionCategories[1]").value("NOT_ACTIONABLE"))
                .andExpect(jsonPath("$.data.nextAction").value("Evaluation catalog is ready for demo evidence; automated evaluation runs are still future work."))
                .andExpect(jsonPath("$.data.readOnly").value(true))
                .andExpect(jsonPath("$.data.healthContract").value("Summary is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."));
    }

    @Test
    void should_return_evaluation_run_preview_report() throws Exception {
        mockMvc.perform(get("/api/evaluation/run-preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.title").value("Evaluation run preview"))
                .andExpect(jsonPath("$.data.previewRunId").value("preview-current-catalog"))
                .andExpect(jsonPath("$.data.caseCount").value(6))
                .andExpect(jsonPath("$.data.supportedFixCaseCount").value(4))
                .andExpect(jsonPath("$.data.safetyRejectionCaseCount").value(2))
                .andExpect(jsonPath("$.data.coveredLanguages[0]").value("go"))
                .andExpect(jsonPath("$.data.coveredLanguages[1]").value("java"))
                .andExpect(jsonPath("$.data.coveredLanguages[2]").value("node"))
                .andExpect(jsonPath("$.data.coveredLanguages[3]").value("python"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[0]").value("go"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[1]").value("maven"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[2]").value("npm"))
                .andExpect(jsonPath("$.data.coveredBuildSystems[3]").value("pytest"))
                .andExpect(jsonPath("$.data.expectedVerificationCommands[0]").value("go test ./..."))
                .andExpect(jsonPath("$.data.expectedVerificationCommands[1]").value("mvn test"))
                .andExpect(jsonPath("$.data.expectedVerificationCommands[2]").value("npm test"))
                .andExpect(jsonPath("$.data.expectedVerificationCommands[3]").value("python3 -m pytest"))
                .andExpect(jsonPath("$.data.safetyRejectionCategories[0]").value("DANGEROUS_INSTRUCTION"))
                .andExpect(jsonPath("$.data.safetyRejectionCategories[1]").value("NOT_ACTIONABLE"))
                .andExpect(jsonPath("$.data.gaps[0]").value("Automated benchmark execution is not implemented yet."))
                .andExpect(jsonPath("$.data.gaps[1]").value("Preview uses expected outcomes only; it does not verify repository fixtures."))
                .andExpect(jsonPath("$.data.nextAction").value("Use this preview as demo evidence now; implement stored evaluation runs next to measure real issue-to-PR outcomes."))
                .andExpect(jsonPath("$.data.readOnly").value(true))
                .andExpect(jsonPath("$.data.sideEffectContract").value("Preview is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.markdownReport").value(org.hamcrest.Matchers.containsString("# PatchPilot Evaluation Run Preview")));
    }

    @Test
    void should_return_evaluation_case_fixture_readiness() throws Exception {
        mockMvc.perform(get("/api/evaluation/case-readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.totalCaseCount").value(6))
                .andExpect(jsonPath("$.data.passingCaseCount").value(4))
                .andExpect(jsonPath("$.data.noFixtureRequiredCaseCount").value(2))
                .andExpect(jsonPath("$.data.failingCaseCount").value(0))
                .andExpect(jsonPath("$.data.cases", hasSize(6)))
                .andExpect(jsonPath("$.data.cases[0].caseId").value("java-maven-doc-fix"))
                .andExpect(jsonPath("$.data.cases[0].status").value("PASS"))
                .andExpect(jsonPath("$.data.cases[0].fixtureRequired").value(true))
                .andExpect(jsonPath("$.data.cases[0].fixtureExists").value(true))
                .andExpect(jsonPath("$.data.cases[0].adapterMatches").value(true))
                .andExpect(jsonPath("$.data.cases[0].expectedFilesExist").value(true))
                .andExpect(jsonPath("$.data.cases[0].expectedChangedFiles[0]").value("src/main/java/demo/Calculator.java"))
                .andExpect(jsonPath("$.data.cases[0].missingExpectedFiles", hasSize(0)))
                .andExpect(jsonPath("$.data.cases[4].caseId").value("unsafe-secret-exfiltration-rejection"))
                .andExpect(jsonPath("$.data.cases[4].status").value("NO_FIXTURE_REQUIRED"))
                .andExpect(jsonPath("$.data.sideEffectContract").value("Evaluation case fixture readiness checks local checked-in fixtures and adapter metadata only; it does not create tasks, call the model, run verification commands, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.markdownReport").value(org.hamcrest.Matchers.containsString("# PatchPilot Evaluation Case Fixture Readiness")));
    }

    @Test
    void should_run_evaluation_fixture_baseline() throws Exception {
        mockMvc.perform(post("/api/evaluation/fixture-baseline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.totalCaseCount").value(6))
                .andExpect(jsonPath("$.data.executedCaseCount").value(4))
                .andExpect(jsonPath("$.data.passedCaseCount").value(4))
                .andExpect(jsonPath("$.data.failedCaseCount").value(0))
                .andExpect(jsonPath("$.data.skippedCaseCount").value(2))
                .andExpect(jsonPath("$.data.cases", hasSize(6)))
                .andExpect(jsonPath("$.data.cases[0].caseId").value("java-maven-doc-fix"))
                .andExpect(jsonPath("$.data.cases[0].status").value("PASSED"))
                .andExpect(jsonPath("$.data.cases[0].executed").value(true))
                .andExpect(jsonPath("$.data.cases[0].verificationCommand[0]").value("mvn"))
                .andExpect(jsonPath("$.data.cases[0].verificationCommand[1]").value("test"))
                .andExpect(jsonPath("$.data.cases[0].exitCode").value(0))
                .andExpect(jsonPath("$.data.cases[0].outputSnippet").value("fixture ok"))
                .andExpect(jsonPath("$.data.cases[4].caseId").value("unsafe-secret-exfiltration-rejection"))
                .andExpect(jsonPath("$.data.cases[4].status").value("SKIPPED"))
                .andExpect(jsonPath("$.data.cases[4].executed").value(false))
                .andExpect(jsonPath("$.data.cases[4].verificationCommand", hasSize(0)))
                .andExpect(jsonPath("$.data.sideEffectContract").value("Evaluation fixture baseline runs local checked-in fixture verification commands only; it does not create tasks, call the model, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.markdownReport").value(org.hamcrest.Matchers.containsString("# PatchPilot Evaluation Fixture Baseline")));
    }

    @Test
    void should_archive_and_list_evaluation_run_snapshots() throws Exception {
        MockMvc snapshotMockMvc = MockMvcBuilders
                .standaloneSetup(controller("snapshot-1"))
                .build();

        snapshotMockMvc.perform(post("/api/evaluation/run-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.previewRunId").value("preview-current-catalog"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.caseCount").value(6))
                .andExpect(jsonPath("$.data.sideEffectContract").value("Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Evaluation Run Snapshot")));

        snapshotMockMvc.perform(get("/api/evaluation/run-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].previewRunId").value("preview-current-catalog"))
                .andExpect(jsonPath("$.data[0].title").value("Evaluation run preview"));
    }

    @Test
    void should_download_archived_evaluation_run_snapshot_report() throws Exception {
        MockMvc snapshotMockMvc = MockMvcBuilders
                .standaloneSetup(controller("snapshot-1"))
                .build();

        String response = snapshotMockMvc.perform(post("/api/evaluation/run-snapshots"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String snapshotId = response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        snapshotMockMvc.perform(get("/api/evaluation/run-snapshots/" + snapshotId + "/report/download"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# PatchPilot Evaluation Run Snapshot")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("- Snapshot id: `" + snapshotId + "`")));
    }

    private static EvaluationRunSnapshotArchiveService archiveService(String snapshotId) {
        return new EvaluationRunSnapshotArchiveService(
                new EvaluationCaseCatalogService(),
                new InMemoryEvaluationRunSnapshotArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-26T04:00:00Z"), ZoneOffset.UTC),
                () -> snapshotId
        );
    }

    private static EvaluationCaseController controller(String snapshotId) {
        EvaluationCaseCatalogService catalogService = new EvaluationCaseCatalogService();
        LanguageAdapterRegistry languageAdapterRegistry = new LanguageAdapterRegistry(List.of(
                new JavaMavenLanguageAdapter(),
                new NodeNpmLanguageAdapter(),
                new PythonPytestLanguageAdapter(),
                new GoLanguageAdapter()
        ));
        EvaluationCaseFixtureReadinessService readinessService = new EvaluationCaseFixtureReadinessService(
                catalogService,
                languageAdapterRegistry
        );
        return new EvaluationCaseController(
                catalogService,
                archiveService(snapshotId),
                readinessService,
                new EvaluationFixtureBaselineService(
                        catalogService,
                        readinessService,
                        languageAdapterRegistry,
                        (caseId, repositoryRoot, command) -> new TestRunResult(String.join(" ", command), 0, "fixture ok")
                )
        );
    }
}
