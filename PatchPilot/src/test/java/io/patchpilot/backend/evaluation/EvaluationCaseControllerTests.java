package io.patchpilot.backend.evaluation;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EvaluationCaseControllerTests {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new EvaluationCaseController(new EvaluationCaseCatalogService()))
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
}
