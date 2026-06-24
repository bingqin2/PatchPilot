package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandSafetyGateTests {

    @Test
    void should_allow_agent_fix_when_allowlists_are_empty() {
        CommandSafetyGate safetyGate = new CommandSafetyGate();

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix touch docs/demo.md"
        ));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void should_reject_empty_agent_fix_instruction() {
        CommandSafetyGate safetyGate = new CommandSafetyGate();

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: instruction is not actionable");
        assertThat(decision.category()).isEqualTo("NOT_ACTIONABLE");
    }

    @Test
    void should_reject_vague_agent_fix_instruction() {
        CommandSafetyGate safetyGate = new CommandSafetyGate();

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix make it better"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: instruction is not actionable");
        assertThat(decision.category()).isEqualTo("NOT_ACTIONABLE");
    }

    @Test
    void should_allow_actionable_problem_instruction() {
        CommandSafetyGate safetyGate = new CommandSafetyGate();

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix build fails with NullPointerException in task worker"
        ));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void should_reject_trigger_user_not_in_allowlist() {
        CommandSafetyGate safetyGate = new CommandSafetyGate(safetyProperties(
                List.of("maintainer"),
                List.of("octocat/hello-world")
        ));

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix touch docs/demo.md"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: trigger user is not allowed");
        assertThat(decision.category()).isEqualTo("TRIGGER_USER_NOT_ALLOWED");
    }

    @Test
    void should_reject_short_agent_fix_trigger_user_not_in_allowlist_before_actionability() {
        CommandSafetyGate safetyGate = new CommandSafetyGate(safetyProperties(
                List.of("maintainer"),
                List.of("octocat/hello-world")
        ));

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: trigger user is not allowed");
        assertThat(decision.category()).isEqualTo("TRIGGER_USER_NOT_ALLOWED");
    }

    @Test
    void should_reject_repository_not_in_allowlist() {
        CommandSafetyGate safetyGate = new CommandSafetyGate(safetyProperties(
                List.of("alice"),
                List.of("octocat/allowed-repo")
        ));

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix touch docs/demo.md"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: repository is not allowed");
        assertThat(decision.category()).isEqualTo("REPOSITORY_NOT_ALLOWED");
    }

    @Test
    void should_reject_short_agent_fix_repository_not_in_allowlist_before_actionability() {
        CommandSafetyGate safetyGate = new CommandSafetyGate(safetyProperties(
                List.of("alice"),
                List.of("octocat/allowed-repo")
        ));

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: repository is not allowed");
        assertThat(decision.category()).isEqualTo("REPOSITORY_NOT_ALLOWED");
    }

    @Test
    void should_classify_dangerous_instruction_rejection() {
        CommandSafetyGate safetyGate = new CommandSafetyGate();

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix delete the repository"
        ));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: destructive or secret-exfiltration instruction");
        assertThat(decision.category()).isEqualTo("DANGEROUS_INSTRUCTION");
    }

    @Test
    void should_match_allowlists_case_insensitively() {
        CommandSafetyGate safetyGate = new CommandSafetyGate(safetyProperties(
                List.of("Alice"),
                List.of("OctoCat/Hello-World")
        ));

        SafetyGateDecision decision = safetyGate.evaluate(request(
                "octocat",
                "hello-world",
                "alice",
                "/agent fix touch docs/demo.md"
        ));

        assertThat(decision.allowed()).isTrue();
    }

    private static SafetyGateRequest request(
            String repositoryOwner,
            String repositoryName,
            String triggerUser,
            String triggerComment
    ) {
        return new SafetyGateRequest(repositoryOwner, repositoryName, triggerUser, triggerComment);
    }

    private static SafetyProperties safetyProperties(List<String> allowedTriggerUsers, List<String> allowedRepositories) {
        SafetyProperties properties = new SafetyProperties();
        properties.setAllowedTriggerUsers(allowedTriggerUsers);
        properties.setAllowedRepositories(allowedRepositories);
        return properties;
    }
}
