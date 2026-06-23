package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.GeneratedDiffRiskDecision;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedDiffRiskGateTests {

    private final GeneratedDiffRiskGate riskGate = new GeneratedDiffRiskGate();

    @Test
    void should_allow_small_source_diff() {
        String diff = """
                diff --git a/src/main/java/App.java b/src/main/java/App.java
                index 1111111..2222222 100644
                --- a/src/main/java/App.java
                +++ b/src/main/java/App.java
                @@ -1,3 +1,3 @@
                -return "old";
                +return "new";
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isTrue();
        assertThat(decision.reason()).isEqualTo("Generated diff passed risk checks");
    }

    @Test
    void should_block_sensitive_file_changes() {
        String diff = """
                diff --git a/.github/workflows/deploy.yml b/.github/workflows/deploy.yml
                index 1111111..2222222 100644
                --- a/.github/workflows/deploy.yml
                +++ b/.github/workflows/deploy.yml
                @@ -1,2 +1,2 @@
                -name: deploy
                +name: deploy changed
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("sensitive path .github/workflows/deploy.yml");
    }

    @Test
    void should_block_package_manager_credential_files() {
        String diff = """
                diff --git a/.npmrc b/.npmrc
                index 1111111..2222222 100644
                --- a/.npmrc
                +++ b/.npmrc
                @@ -1,2 +1,2 @@
                -registry=https://registry.npmjs.org/
                +registry=https://npm.pkg.github.com/
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("sensitive path .npmrc");
    }

    @Test
    void should_block_git_metadata_changes() {
        String diff = """
                diff --git a/.git/config b/.git/config
                index 1111111..2222222 100644
                --- a/.git/config
                +++ b/.git/config
                @@ -1,2 +1,2 @@
                -[core]
                +[credential]
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("sensitive path .git/config");
    }

    @Test
    void should_block_secret_like_added_lines() {
        String diff = """
                diff --git a/config/example.properties b/config/example.properties
                index 1111111..2222222 100644
                --- a/config/example.properties
                +++ b/config/example.properties
                @@ -1,2 +1,3 @@
                 service.enabled=true
                +PATCHPILOT_GITHUB_TOKEN=ghp_abcdefghijklmnopqrstuvwxyz123456
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("secret-like added line");
    }

    @Test
    void should_block_binary_generated_diffs() {
        String diff = """
                diff --git a/assets/logo.png b/assets/logo.png
                index 1111111..2222222 100644
                Binary files a/assets/logo.png and b/assets/logo.png differ
                """;

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("binary file change");
    }

    @Test
    void should_block_large_generated_diffs() {
        StringBuilder diff = new StringBuilder("""
                diff --git a/src/main/java/App.java b/src/main/java/App.java
                index 1111111..2222222 100644
                --- a/src/main/java/App.java
                +++ b/src/main/java/App.java
                @@ -1,1 +1,401 @@
                """);
        for (int index = 0; index < 401; index++) {
            diff.append("+line ").append(index).append("\n");
        }

        GeneratedDiffRiskDecision decision = riskGate.evaluate(diff.toString());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("too many changed lines");
    }
}
