package io.patchpilot.backend.demo.domain;

import io.patchpilot.backend.demo.DemoLaunchPreflightRequestDto;

import java.util.List;

public record DemoLaunchCommandVo(
        String triggerComment,
        DemoLaunchPreflightRequestDto preflightInput,
        String githubIssueUrl,
        String summary,
        List<String> nextActions
) {
    public DemoLaunchCommandVo {
        nextActions = List.copyOf(nextActions);
    }
}
