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
}
