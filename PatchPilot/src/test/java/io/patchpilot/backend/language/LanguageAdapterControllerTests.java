package io.patchpilot.backend.language;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LanguageAdapterControllerTests {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new LanguageAdapterController(new LanguageAdapterCatalogService()))
            .build();

    @Test
    void should_return_supported_language_adapters() throws Exception {
        mockMvc.perform(get("/api/language-adapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(12)))
                .andExpect(jsonPath("$.data[0].language").value("java"))
                .andExpect(jsonPath("$.data[0].buildSystem").value("maven"))
                .andExpect(jsonPath("$.data[0].verificationCommand[0]").value("mvn"))
                .andExpect(jsonPath("$.data[0].verificationCommand[1]").value("test"))
                .andExpect(jsonPath("$.data[0].detectionSignals[0]").value("pom.xml"))
                .andExpect(jsonPath("$.data[0].demoFixturePath").value("docs/demo-repositories/java-maven"))
                .andExpect(jsonPath("$.data[0].status").value("SUPPORTED"))
                .andExpect(jsonPath("$.data[2].language").value("node"))
                .andExpect(jsonPath("$.data[2].buildSystem").value("bun"))
                .andExpect(jsonPath("$.data[2].verificationCommand[0]").value("bun"))
                .andExpect(jsonPath("$.data[2].verificationCommand[1]").value("test"))
                .andExpect(jsonPath("$.data[2].detectionSignals[1]").value("bun.lockb"))
                .andExpect(jsonPath("$.data[2].detectionSignals[2]").value("bun.lock"))
                .andExpect(jsonPath("$.data[2].demoFixturePath").value("docs/demo-repositories/node-bun"))
                .andExpect(jsonPath("$.data[6].language").value("python"))
                .andExpect(jsonPath("$.data[6].buildSystem").value("tox"))
                .andExpect(jsonPath("$.data[6].verificationCommand[0]").value("tox"))
                .andExpect(jsonPath("$.data[8].language").value("python"))
                .andExpect(jsonPath("$.data[8].buildSystem").value("hatch"))
                .andExpect(jsonPath("$.data[8].verificationCommand[0]").value("hatch"))
                .andExpect(jsonPath("$.data[8].verificationCommand[1]").value("test"))
                .andExpect(jsonPath("$.data[11].language").value("python"))
                .andExpect(jsonPath("$.data[11].buildSystem").value("pytest"));
    }
}
