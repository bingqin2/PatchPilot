package io.patchpilot.backend.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.domain.RepositoryPreflightRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RepositoryPreflightControllerTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    private Path repositoryDir;

    @Test
    void should_preflight_repository_path() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new RepositoryPreflightController(new RepositoryPreflightService(
                        new LanguageAdapterRegistry(List.of(path -> LanguageDetectionResult.supported(
                                "java",
                                "maven",
                                List.of("mvn", "test"),
                                "Detected Maven project"
                        ))),
                        new LanguageAdapterCatalogService()
                )))
                .build();

        mockMvc.perform(post("/api/repository-preflight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RepositoryPreflightRequest(repositoryDir.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.supported").value(true))
                .andExpect(jsonPath("$.data.language").value("java"))
                .andExpect(jsonPath("$.data.buildSystem").value("maven"))
                .andExpect(jsonPath("$.data.verificationCommand[0]").value("mvn"))
                .andExpect(jsonPath("$.data.reason").value("Detected Maven project"))
                .andExpect(jsonPath("$.data.operatorAction").value("Repository is supported. PatchPilot can run the detected verification command after patch generation."))
                .andExpect(jsonPath("$.data.repositoryPath").value(repositoryDir.toString()))
                .andExpect(jsonPath("$.data.supportedAdapters", hasSize(0)));
    }

    @Test
    void should_return_bad_request_for_blank_repository_path() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new RepositoryPreflightController(new RepositoryPreflightService(
                        new LanguageAdapterRegistry(List.of()),
                        new LanguageAdapterCatalogService()
                )))
                .build();

        mockMvc.perform(post("/api/repository-preflight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RepositoryPreflightRequest(" "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("repositoryPath is required"));
    }
}
