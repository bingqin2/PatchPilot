package io.patchpilot.backend.agent;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AdapterSmokeScriptTests {

    private static final Path SCRIPT = Path.of("..", "scripts", "adapter-smoke.sh");

    @Test
    void should_define_safe_adapter_detection_smoke_script() throws Exception {
        assertThat(SCRIPT).exists();

        String script = Files.readString(SCRIPT);

        assertThat(script).contains("LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures");
        assertThat(script).contains("LanguageAdapterRegistryTests");
        assertThat(script).contains("docs/demo-repositories");
        assertThat(script).contains("java-maven");
        assertThat(script).contains("node-pnpm");
        assertThat(script).contains("python-uv");
        assertThat(script).doesNotContain("docker compose");
        assertThat(script).doesNotContain("git push");
        assertThat(script).doesNotContain("PATCHPILOT_AGENT_API_KEY");
        assertThat(script).doesNotContain("/api/github/webhook");
    }
}
