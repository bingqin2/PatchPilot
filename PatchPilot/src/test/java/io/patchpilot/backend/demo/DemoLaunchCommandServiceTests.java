package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLaunchCommandServiceTests {

    private final DemoLaunchCommandService service = new DemoLaunchCommandService();

    @Test
    void should_compose_replace_command_for_controlled_demo_issue() {
        DemoLaunchCommandVo command = service.compose(new DemoLaunchCommandRequestDto(
                " bingqin2 ",
                " PatchPilot ",
                12L,
                " bingqin2 ",
                " replace ",
                " docs/demo.md ",
                " PatchPilot smoke test "
        ));

        assertThat(command.triggerComment()).isEqualTo("/agent fix replace docs/demo.md PatchPilot smoke test");
        assertThat(command.githubIssueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/12");
        assertThat(command.summary()).isEqualTo("Prepared a demo /agent fix replace command for bingqin2/PatchPilot#12.");
        assertThat(command.preflightInput()).isEqualTo(new DemoLaunchPreflightRequestDto(
                "bingqin2",
                "PatchPilot",
                12L,
                "bingqin2",
                "/agent fix replace docs/demo.md PatchPilot smoke test"
        ));
        assertThat(command.nextActions()).containsExactly(
                "Run launch preflight with the generated command before posting it on GitHub.",
                "Post the generated command on https://github.com/bingqin2/PatchPilot/issues/12 only after preflight reports ready."
        );
    }

    @Test
    void should_compose_touch_command_without_replacement_text() {
        DemoLaunchCommandVo command = service.compose(new DemoLaunchCommandRequestDto(
                "bingqin2",
                "PatchPilot",
                3L,
                "bingqin2",
                "touch",
                "docs/demo.md",
                null
        ));

        assertThat(command.triggerComment()).isEqualTo("/agent fix touch docs/demo.md");
        assertThat(command.preflightInput().triggerComment()).isEqualTo("/agent fix touch docs/demo.md");
    }

    @Test
    void should_reject_replace_command_without_replacement_text() {
        DemoLaunchCommandRequestDto request = new DemoLaunchCommandRequestDto(
                "bingqin2",
                "PatchPilot",
                3L,
                "bingqin2",
                "replace",
                "docs/demo.md",
                " "
        );

        assertThatThrownBy(() -> service.compose(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("replacementText must not be blank for replace commands");
    }

    @Test
    void should_reject_unsafe_target_paths() {
        DemoLaunchCommandRequestDto request = new DemoLaunchCommandRequestDto(
                "bingqin2",
                "PatchPilot",
                3L,
                "bingqin2",
                "touch",
                ".git/config",
                null
        );

        assertThatThrownBy(() -> service.compose(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("targetPath must not target protected repository metadata");
    }

    @Test
    void should_reject_ambiguous_target_paths_with_whitespace() {
        DemoLaunchCommandRequestDto request = new DemoLaunchCommandRequestDto(
                "bingqin2",
                "PatchPilot",
                3L,
                "bingqin2",
                "touch",
                "docs/demo file.md",
                null
        );

        assertThatThrownBy(() -> service.compose(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("targetPath must not contain whitespace");
    }
}
