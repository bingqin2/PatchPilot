package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EvaluationCaseControllerTests {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new EvaluationCaseController(new EvaluationCaseCatalogService(), archiveService("snapshot-default")))
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
                .andExpect(jsonPath("$.data[0].expectedChangedFiles[0]").value("src/main/java/io/patchpilot/demo/GreetingService.java"))
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
    void should_archive_and_list_evaluation_run_snapshots() throws Exception {
        MockMvc snapshotMockMvc = MockMvcBuilders
                .standaloneSetup(new EvaluationCaseController(new EvaluationCaseCatalogService(), archiveService("snapshot-1")))
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
                .standaloneSetup(new EvaluationCaseController(new EvaluationCaseCatalogService(), archiveService("snapshot-1")))
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
}
